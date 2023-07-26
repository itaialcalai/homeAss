// Itai Alcalai 206071110
package com.genoox.homeAss.io;

//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.NoSuchElementException;
import java.util.Iterator;

// **this is a local Reader for debugg
public class VcfFileReader implements Iterator<String>, Closeable {
//    private S3Client s3;
    private String bucketName;
    private String objectKey;
    private InputStream fileStream;
    private InputStream gzipStream;
    private BufferedReader reader;
    private String nextLine;

    public VcfFileReader(String bucketName, String objectKey) throws IOException {
//        this.s3 = S3Client.builder().build();
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.fileStream = new FileInputStream(objectKey);
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
        } catch (IOException e) {
            throw new RuntimeException("Error closing the file.", e);
        }
    }

//    public Stream<String> readLines() throws IOException {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .build();
//
//        GZIPInputStream gzipInputStream = new GZIPInputStream(s3.getObject(getObjectRequest).response().inputStream());
//        BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
//
//        return reader.lines();
//    }

//    public Stream<String> readLocalLines() throws IOException {
//        InputStream fileStream = new FileInputStream(objectKey);
//        InputStream gzipStream = new GZIPInputStream(fileStream);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));
//
//        return reader.lines();
//    }

//    public void processLines(Consumer<String> lineProcessor) throws IOException {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .build();
//
//        try (InputStream response = s3.getObject(getObjectRequest);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                lineProcessor.accept(line);
//            }
//        }
//    }

}


