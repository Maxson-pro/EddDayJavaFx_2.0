package org.example.edddayjavafx2;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchiveManager {

    public static void ZipReporting(File zipFile, String projectRoot, List<String> errorReports) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                String entryName = entry.getName().replace("\\", "/");
                String finalPath = entryName.startsWith("data/") ? entryName : "data/" + entryName;
                File filePatg = new File(projectRoot, finalPath);
                try {
                    if (entry.isDirectory()) {
                        filePatg.mkdirs();
                    }else {
                        File parent = filePatg.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }
                        try(FileOutputStream fos = new FileOutputStream(filePatg)) {
                            byte[] bytes = new byte[4096];
                            int len;
                            while ((len = zip.read(bytes)) > 0) {
                                fos.write(bytes, 0 , len);
                            }
                        }
                    }

                } catch (Exception e) {
                    errorReports.add("Ошибка в файле '" + entryName + "':" + e.getMessage());
                }
                zip.closeEntry();
                entry = zip.getNextEntry();
            }


        }
    }
}