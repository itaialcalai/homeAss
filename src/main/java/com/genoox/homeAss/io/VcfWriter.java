// Itai alcalai 206071110
package com.genoox.homeAss.io;

// Importing necessary libraries for handling files and exceptions
import java.io.PrintWriter;
import java.io.IOException;

// Importing the Sample model class from our own package
import com.genoox.homeAss.model.Sample;

// Define a public class named 'VcfWriter'
public class VcfWriter {

    // Method for writing a Sample object to a file specified by a filePath
    public static void writeSample(Sample sample, String filePath) {
        try {
            // Initialize a PrintWriter to write to the file
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");

            // Iterate over the metadata in the sample
            for(String metadataLine: sample.getMetadata()) {
                // Write each line of metadata to the file
                writer.println(metadataLine);
            }

            // Write the header and the sample name to the file
            writer.println(sample.getHeader() + "\t" + sample.getName());

            // Iterate over the variants in the sample
            for(String variantLine: sample.getVariants()) {
                // Write each variant line to the file
                writer.println(variantLine);
            }

            // Close the PrintWriter to free up system resources
            writer.close();
        } catch (IOException e) { // Catch any exceptions that might occur during the file operations
            // Print an error message to the console
            System.out.println("An error occurred while writing to the file.");
            // Print the stack trace of the exception
            e.printStackTrace();
        }
    }
}


