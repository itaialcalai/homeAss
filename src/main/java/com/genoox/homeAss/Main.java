// Itai Alcalai 206071110
package com.genoox.homeAss;

// Import necessary classes
import com.genoox.homeAss.io.AWSFileReader;
import com.genoox.homeAss.processor.VcfProcessor;

public class Main {

    // Main method
    public static void main(String[] args) {
        System.out.println("Hello");

        // Check if limit is provided and is an integer less than 10
        if (args.length < 1 || !isNumeric(args[0]) || Integer.parseInt(args[0]) >= 10) {
            System.out.println("Please provide the limit (an integer less than 10) as the first argument");
            System.exit(1);
        }

        // Parse the limit from the provided argument
        int limit = Integer.parseInt(args[0]);

        // Parse optional parameters, providing default values if they are not specified
        int start = args.length > 1 && isNumeric(args[1]) ? Integer.parseInt(args[1]) : 0;
        int end = args.length > 2 && isNumeric(args[2]) ? Integer.parseInt(args[2]) : Integer.MAX_VALUE;
        int minDP = args.length > 3 && isNumeric(args[3]) ? Integer.parseInt(args[3]) : 0;

        // denovo = 0 if true, 1 if false, and 2 as default (no denovo selection)
        int deNovo = 2;
        if (args.length > 4) {
            String deNovoArg = args[4].toLowerCase();
            if ("true".equals(deNovoArg)) {
                deNovo = 0;
            } else if ("false".equals(deNovoArg)) {
                deNovo = 1;
            }
            // else 2
        }

        // Check if end is greater than start
        if( end < start) {
            System.out.println("End position must be greater than or equal to start position");
            System.exit(1);
        }

        // Define bucket and object keys for the file to be read
        String bucketName = "resources.genoox.com";
//        String objectKey1 = "C:\\Users\\itaia\\OneDrive\\Genoox asignment\\ASW_50_samples.vcf.decomp.vcf.gz";
        String objectKey = "homeAssingment/demo_vcf_multisample.vcf.gz";

        // Initialize a VCF file reader and a VCF processor
        boolean stat;
        try {
            // Create a new reader for the VCF file
            try (AWSFileReader reader = new AWSFileReader(bucketName, objectKey)) {
                // Create a new processor for the VCF data
                VcfProcessor processor = new VcfProcessor(start, end, minDP, limit, deNovo);

                // Process the VCF file
                System.out.println("Processing VCF...");
                stat = processor.processVcf(reader);

                // Check the status of the VCF processing
                if (stat) {
                    System.out.println("Done.");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            // Handle any exceptions that occurred during VCF processing
            System.err.println("Error processing the VCF file: " + e.getMessage());
            System.exit(1);  // abnormal termination
        }
        System.err.println("Failed processing the VCF file");
        System.exit(0);
    }

    // Helper method to check if a string is numeric
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
