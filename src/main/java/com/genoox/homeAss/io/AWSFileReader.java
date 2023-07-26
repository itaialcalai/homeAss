// Itai Alcalai 206071110
package com.genoox.homeAss.io;

// Necessary import statements
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

// This class reads gzipped files from AWS S3, implements Iterator to read line by line and Closeable to ensure resources are properly closed
public class AWSFileReader implements Iterator<String>, Closeable {
    // AWS S3 client
    private S3Client s3;
    private String bucketName;
    private String objectKey;
    // InputStreams for the S3 object and gzip decompression
    private InputStream fileStream;
    private InputStream gzipStream;
    // Reader to read lines from the gzipped input stream
    private BufferedReader reader;
    // Variable to hold the next line of the file
    private String nextLine;

    // Constructor for AWSFileReader that initializes the S3 client and sets up the necessary streams
    public AWSFileReader(String bucketName, String objectKey) throws IOException {
        // Initialize AWS S3 client
        this.s3 = S3Client.create();
        // Initialize bucketName and objectKey
        this.bucketName = bucketName;
        this.objectKey = objectKey;

        // Build GetObjectRequest for the specified bucket and key
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        // Get the object from S3 and set up the input streams
        this.fileStream = s3.getObject(getObjectRequest, ResponseTransformer.toInputStream());
        this.gzipStream = new GZIPInputStream(fileStream);
        this.reader = new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));
        // Read the first line of the file
        this.nextLine = reader.readLine();
    }

    // Returns true if there is another line to read
    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    // Returns the next line in the file
    @Override
    public String next() {
        if (!hasNext()) {
            // If there is no next line, throw an exception
            throw new NoSuchElementException("No more lines to read.");
        }
        String currentLine = nextLine;
        try {
            // Read the next line
            nextLine = reader.readLine();
            if (nextLine == null) {
                // If there is no more lines to read, close all the streams
                close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading the next line.", e);
        }
        return currentLine;
    }

    // Closes all the resources
    @Override
    public void close() {
        try {
            // Close the BufferedReader, GZIPInputStream, FileInputStream, and S3 client
            reader.close();
            gzipStream.close();
            fileStream.close();
            s3.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the file.", e);
        }
    }
}
