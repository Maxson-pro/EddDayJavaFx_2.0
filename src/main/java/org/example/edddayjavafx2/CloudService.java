package org.example.edddayjavafx2;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.json.Link;
import java.io.File;

public class CloudService {
    public static void uploadFolder(File localFolder, String token) throws Exception {
        Credentials credentials = new Credentials("", token);
        RestClient client = new RestClient(credentials);
        try {
            client.makeFolder("data");
        } catch (Exception e) {
        }
        File[] files = localFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Link uploadLink = client.getUploadLink("data/" + file.getName(), true);
                    client.uploadFile(uploadLink, false, file, null);
                    System.out.println("Файл " + file.getName());
                }
            }
        }
    }
}