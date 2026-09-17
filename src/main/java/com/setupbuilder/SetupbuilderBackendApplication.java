package com.setupbuilder;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;

@SpringBootApplication
public class SetupbuilderBackendApplication {

    public static void main(String[] args) {

//        FileInputStream serviceAccount =
//                new FileInputStream("path/to/serviceAccountKey.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        FirebaseApp.initializeApp(options);

        SpringApplication.run(SetupbuilderBackendApplication.class, args);
    }

}
