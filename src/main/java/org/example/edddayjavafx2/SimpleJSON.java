package org.example.edddayjavafx2;

import java.io.*;

class SimpleJSON {
    public static void saveToFile(String filename, String text) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("{\"text\": \"" + text.replace("\"", "\\\"") + "\"}");
        writer.close();
    }

    public static String loadFromFile(String filename) throws IOException {
        File file = new File(filename);
     if (!file.exists()) return "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
  StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();

      String json = content.toString();
        int start = json.indexOf("\"text\": \"");
        if (start == -1) return "";
        start = start + 9;
    int end = json.lastIndexOf("\"");
        if (end > start) {
            return json.substring(start, end).replace("\\\"", "\"");
        }
        return "";
    }
}