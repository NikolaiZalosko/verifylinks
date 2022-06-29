package com.nickz.verifylinks.service.task;

import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Returns bad URLs
 */
@AllArgsConstructor
public class VerifyPresignedUrlsTask implements Callable<List<String>> {

  private String sourceCsvFileName;
  private long startRow;
  private long portionOfRows;
  private long totalRows;

  private static final AtomicLong count = new AtomicLong(0);

  @Override
  public List<String> call() throws IOException {
    List<String> badUrls = new ArrayList<>();

    Reader in = null;
    in = new FileReader(sourceCsvFileName);
    Iterator<CSVRecord> it = CSVFormat.DEFAULT.withHeader().parse(in).iterator();
    int currentLine = 1;

    // go to startLine
    while (currentLine < startRow && it.hasNext()) {
      it.next();
      currentLine++;
    }

    while (it.hasNext() && currentLine < startRow + portionOfRows) {
      CSVRecord row = it.next();
      String urlString = row.get("url");
      System.out.println(urlString);

      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();

      int code = connection.getResponseCode();
      if (code != 200) {
        badUrls.add(urlString);
      }

      currentLine++;

      double percentDone = (double) count.incrementAndGet() / totalRows * 100;
      DecimalFormat df = new DecimalFormat("0.00");
      System.out.println(df.format(percentDone) + " % ...");
    }

    return badUrls;
  }
}
