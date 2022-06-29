package com.nickz.verifylinks.service;

import com.nickz.verifylinks.service.task.VerifyPresignedUrlsTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class PresignedUrlsService {

  public void verifyPresignedUrls(String sourceFileName, int numTasks) throws IOException, ExecutionException, InterruptedException {
    long totalRows;
    try (Stream<String> stream = Files.lines(Paths.get(sourceFileName), StandardCharsets.UTF_8)) {
      totalRows = stream.count();
    }
    long rowsToReadPerTask = totalRows / numTasks + 1;
    long currentRow = 0;
    List<Callable<List<String>>> tasks = new ArrayList<>();
    for (int i = 0; i < numTasks; i++) {
      tasks.add(new VerifyPresignedUrlsTask(sourceFileName, currentRow, rowsToReadPerTask, totalRows));
      currentRow += rowsToReadPerTask;
    }

    ExecutorService exec = Executors.newFixedThreadPool(numTasks);
    List<Future<List<String>>> results;
    results = exec.invokeAll(tasks);

    exec.shutdown();

    // collect results
    List<String> finalBadUrls = new ArrayList<>();
    for (Future<List<String>> future : results) {
      List<String> result = future.get();
      finalBadUrls.addAll(result);
    }

    if (finalBadUrls.isEmpty()) {
      System.out.println("Result: SUCCESS");
    } else {
      System.out.println("***** Broken links *****");
      finalBadUrls.forEach(System.out::println);
      System.out.println("Result: FAIL");
    }
  }
}
