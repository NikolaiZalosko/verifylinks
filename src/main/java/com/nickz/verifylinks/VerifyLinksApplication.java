package com.nickz.verifylinks;

import com.nickz.verifylinks.service.PresignedUrlsService;

import java.io.File;

public class VerifyLinksApplication {

  public static void main(String[] args) {
    checkArgs(args);

    PresignedUrlsService service = new PresignedUrlsService();
    String fileName = args[0];

    int numOfTasks = Runtime.getRuntime().availableProcessors();
    try {
      service.verifyPresignedUrls(fileName, numOfTasks);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void checkArgs(String[] args) {
    if (args.length < 1) {
      System.err.println("Please provide a file name.");
      System.exit(1);
    }

    String fileName = args[0];
    if (!new File(fileName).exists()) {
      System.err.println("File " + fileName + " does not exist.");
      System.exit(1);
    }
  }
}
