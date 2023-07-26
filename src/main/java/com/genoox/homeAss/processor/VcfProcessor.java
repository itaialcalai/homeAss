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
    private int deNovo;

    private List<String> metadata = new ArrayList<>(); // uniform metadata
    private String header; // uniform header
    private List<Sample> samples = new ArrayList<>(); // all samples

    // constructor defiened by the user parameters
    public VcfProcessor(int start, int end, int minDP, int limit, int deNovo) {
        this.start = start;
        this.end = end;
        this.minDP = minDP;
        this.limit = limit;
        this.deNovo = deNovo;
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
    public String parseMeta(AWSFileReader reader) throws IOException {
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

    // Method to exract the Genotype and determine presence of variant by allel
    public boolean variantNotInSample(String info) {
        // Split the sample's information into fields
        String[] fields = info.split(":");

        // Extract the genotype from the first field
        String genotype = fields[0];

        // Check if the genotype is  0/0
        return genotype.equals("0/0");
    }

    // Method that handles the deNovo feature logic
    public void handleDeNovo(int probIndex, String probInfo, String[] samplesInfo, Variant variant) {
        int motherIndex = -1;
        int fatherIndex = -1;

        // Find the indices of mother and father samples dynamically
        for (int i = 0; i < samplesInfo.length; i++) {
            String sampleName = samples.get(i).getName();
            if (sampleName.equals("mother")) {
                motherIndex = i;
            } else if (sampleName.equals("father")) {
                fatherIndex = i;
            }
        }
        if (motherIndex == -1 || fatherIndex == -1) {
            // Either mother or father sample is missing; cannot proceed with deNovo check
            return;
        }
        // Get the genetic data of parents
        String motherInfo = samplesInfo[motherIndex];
        String fatherInfo = samplesInfo[fatherIndex];

        if (deNovo == 1) {
            // deNovo is set to false -> variant should apear in at least one of the parents
            if (!variantNotInSample(motherInfo) || !variantNotInSample(fatherInfo)) {
                samples.get(probIndex).addVariant(variant, probInfo);
            }
        } else {
            // deNovo is set to true -> variant shouldn't apear in both parents
            if (variantNotInSample(motherInfo) && variantNotInSample(fatherInfo)) {
                samples.get(probIndex).addVariant(variant, probInfo);
            }
        }
        return;
    }


    // Method to process the VCF file
    public boolean processVcf(AWSFileReader reader) throws IOException {
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
                        System.err.println("Failed to fetch gene");
                        e.printStackTrace();
                    }

                    // For each sample, if it's not finished, add the variant and its info
                    for (int i = 0; i < samplesInfo.length; i++) {
                        // If the sample hasnwt reached the limit
                        if (!samples.get(i).getStatus()) {
                            String info = samplesInfo[i];
                            char first = info.charAt(0);
                            // If the variant is present in the sample
                            if (first != '.' && !variantNotInSample(info)) {
                                // if deNovo option recieved and this is the proband sample
                                if (deNovo != 2 && samples.get(i).getName().equals("proband")) {
                                    handleDeNovo(i, info, samplesInfo, variant);
                                } else {
                                    samples.get(i).addVariant(variant, info);
                                }

                            }
                        }
                    }

                }
            }
            // Output sample files that did not reach the limit
            if (!reader.hasNext()) {
                samples.stream().filter(sample -> !sample.getStatus())
                        .forEach(sample -> {
                            sample.outputSample();
                        });
            }

        } catch (Exception e) {
            // Handle the exception (you can log it or take appropriate actions).
            success = false;
            throw e;
        }
        return success;
    }
}

