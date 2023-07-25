package com.genoox.homeAss.io;

import java.io.PrintWriter; // add this line
import java.io.IOException; // add this line
import com.genoox.homeAss.model.Sample;

public class VcfWriter {
    public static void writeSample(Sample sample, String filePath) {
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");

            for(String metadataLine: sample.getMetadata()) {
                writer.println(metadataLine);
            }

            writer.println(sample.getHeader() + "\t" + sample.getName());

            for(String variantLine: sample.getVariants()) {
                writer.println(variantLine);
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}

