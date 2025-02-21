package com.polytech.webscraipper.services;

import org.springframework.stereotype.Service;

@Service
public class HtmlService {
  public String generateHtml(String url, String content) {
    return "<!DOCTYPE html>\n<html>\n<head>\n<title>Document Summary</title>\n</head>\n<body>\n"
        + "<h1>Document Summary</h1>\n"
        + "<p>URL: "
        + escapeHtml(url)
        + "</p>\n"
        + "<p>Content:</p>\n<pre>"
        + escapeHtml(content)
        + "</pre>\n"
        + "<script>\n"
        + "window.documentSummary = { url: \""
        + escapeHtml(url)
        + "\", content: \""
        + escapeHtml(content)
        + "\" };\n"
        + "</script>\n"
        + "</body>\n</html>";
  }

  private String escapeHtml(String input) {
    // Replace line breaks with the escape sequence for JavaScript newlines
    String escapedInput = input.replace("\n", "\\n");

    // Escape HTML special characters to prevent execution
    return escapedInput
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
