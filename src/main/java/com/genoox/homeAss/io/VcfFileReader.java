package com.genoox.homeAss.io;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class VcfFileReader {
    private S3Client s3;
    private String bucketName;
    private String objectKey;

    public VcfFileReader(String bucketName, String objectKey) {
        this.s3 = S3Client.builder().build();
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public Stream<String> readLines() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        String content = s3.getObjectAsBytes(getObjectRequest).asUtf8String();
        return content.lines();
    }

    public void processLines(Consumer<String> lineProcessor) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try (InputStream response = s3.getObject(getObjectRequest);
             BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineProcessor.accept(line);
            }
        }
    }

}


