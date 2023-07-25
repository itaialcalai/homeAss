package com.genoox.homeAss;

import com.genoox.homeAss.io.VcfFileReader;
import com.genoox.homeAss.processor.VcfProcessor;
import java.util.List;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide the limit as an argument");
            System.exit(1);
        }

        int limit = Integer.parseInt(args[0]);

        String bucketName = "resources.genoox.com";
        String objectKey = "C:\\Users\\itaia\\OneDrive\\Genoox asignment\\ASW_50_samples.vcf.decomp.vcf.gz";
        String objectKey1 = "homeAssingment/demo_vcf_multisample.vcf.gz";


        // Fill in with your actual start, end and minDP
        int start = 10000;
        int end = 80000;
        int minDP = 10;

        VcfFileReader reader;
        boolean stat;
        try {
            reader = new VcfFileReader(bucketName, objectKey);
            VcfProcessor processor = new VcfProcessor(start, end, minDP, limit);
            stat = processor.processVcf(reader);
            if (stat) {
                System.out.println("FINISH!!!!");
            }
        } catch (Exception e) {
            System.err.println("Error processing the VCF file: " + e.getMessage());
            return;
        }
    }
}


