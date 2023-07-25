// Itai Alcalai 206071110
package com.genoox.homeAss.processor;

//import program classes
import com.genoox.homeAss.util.*;
import com.genoox.homeAss.model.*;
import com.genoox.homeAss.io.*;

// import java
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;


// Class that executes the the programs main functionality
public class VcfProcessor {
    // parameters
    private int start;
    private int end;
    private int minDP;
    private int limit;

    private List<String> metadata = new ArrayList<>(); // uniform metadata
    private String header; // uniform header
    private List<Sample> samples = new ArrayList<>(); // all samples

    // constructor defiened by the user parameters
    public VcfProcessor(int start, int end, int minDP, int limit) {
        this.start = start;
        this.end = end;
        this.minDP = minDP;
        this.limit = limit;
    }

    // Method to parse the VCF file header and initialize Sample objects
    public void parseHeader(String line) {
        // Split the line on tabs into separate header elements
        String[] headers = line.split("\t");

        // Store the header up to the format (first 9 columns) as a String
        this.header = String.join("\t", Arrays.asList(headers).subList(0, 9));

        // Iterate over each sample name in the header, starting from the 10th column
        for(int i=9; i<headers.length; i++) {
            // Create a new Sample object for each sample name
            Sample newSample = new Sample(headers[i], this.metadata, this.header, this.limit);

            // Add the new Sample object to the list of samples
            samples.add(newSample);
        }
    }

    // Method to parse the metadata lines in the VCF file
    public String parseMeta(VcfFileReader reader) throws IOException {
        String line = "0";

        // Continue reading lines until a line that doesn't start with ## is encountered
        while (reader.hasNext()) {
            line = reader.next();
            if (line.startsWith("##")) {
                // Add the metadata line to the metadata list
                metadata.add(line);
            } else {
                break;
            }
        }

        // Return the first line that doesn't start with ##
        return line;
    }

    // Method to Extract the DP value from the variant INFO
    public String extractDP(String infoLine) {
        // Split the string into key-value pairs
        String[] pairs = infoLine.split(";");

        // Iterate over each pair
        for (String pair : pairs) {
            // Split the pair into key and value
            String[] keyValue = pair.split("=");

            // Check if the key is "DP"
            if (keyValue[0].equals("DP")) {
                // Return the value
                return keyValue[1];
            }
        }

        // If "DP" was not found in the line, return null for default value
        return null;
    }


    // Method to process the VCF file
    public boolean processVcf(VcfFileReader reader) throws IOException {
        boolean success = true;
        try {
            // Parse the metadata lines in the VCF file
            String head = parseMeta(reader);

            // If there's a valid header line, parse it
            if (!head.equals("0")) {
                parseHeader(head);
            }

            // Continue processing while there are still lines to read and not all samples are finished
            while (reader.hasNext() && samples.stream().anyMatch(sample -> !sample.getStatus())){
                // Read the next line
                String line = reader.next();

                // Split the line on tabs into separate columns
                String[] columns = line.split("\t");

                // Store the first 9 columns (standard VCF columns) in a separate array
                String[] attr = Arrays.copyOfRange(columns, 0, 9);

                // Store the sample information in a separate array
                String[] samplesInfo = Arrays.copyOfRange(columns, 9, columns.length);

                // Parse the position of the variant (assumed to be in the second column)
                int pos = Integer.parseInt(attr[1]);
                // DP
                int dp = Integer.MAX_VALUE; // Default
                String variantInfo = attr[7]; // assuming INFO is in the eighth column
                String dpStr = extractDP(variantInfo); // a method to extract DP from INFO
                if (dpStr != null) {
                    dp = Integer.parseInt(dpStr); // if DP information is found
                }

                // Process the variant if its position is within the specified range
                if (pos >= start && pos <= end && dp > minDP) {
                    // Create a new Variant object from the attr array
                    Variant variant = new Variant(attr);

                    // Try to fetch the gene associated with the variant from an API
                    try {
                        String gene = ApiUtils.fetchGeneFromVariant(variant);
                        variant.setGene(gene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // For each sample, if it's not finished, add the variant and its info
                    for (int i = 0; i < samplesInfo.length; i++) {
                        if (!samples.get(i).getStatus()) {
                            String info = samplesInfo[i];
                            if (!info.equals("...")) {
                                samples.get(i).addVariant(variant, info);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            // Handle the exception (you can log it or take appropriate actions).
            success = false;
            throw e;
        }
        return success;
    }

}

