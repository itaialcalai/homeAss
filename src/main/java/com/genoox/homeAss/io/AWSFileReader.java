package com.genoox.homeAss.io;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class AWSFileReader implements Iterator<String>, Closeable {
    private S3Client s3;
    private String bucketName;
    private String objectKey;
    private InputStream fileStream;
    private InputStream gzipStream;
    private BufferedReader reader;
    private String nextLine;

    public AWSFileReader(String bucketName, String objectKey) throws IOException {
        this.s3 = S3Client.create(); // Init client
        this.bucketName = bucketName;
        this.objectKey = objectKey;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        this.fileStream = s3.getObject(getObjectRequest, ResponseTransformer.toInputStream());
        this.gzipStream = new GZIPInputStream(fileStream);
        this.reader = new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));
        this.nextLine = reader.readLine();
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines to read.");
        }
        String currentLine = nextLine;
        try {
            nextLine = reader.readLine();
            if (nextLine == null) {
                close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading the next line.", e);
        }
        return currentLine;
    }

    @Override
    public void close() {
        try {
            reader.close();
            gzipStream.close();
            fileStream.close();
            s3.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the file.", e);
        }
    }
}
