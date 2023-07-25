package com.genoox.homeAss.processor;
import com.genoox.homeAss.util.*;
import com.genoox.homeAss.model.*;
import com.genoox.homeAss.io.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;



public class VcfProcessor {
    private int start;
    private int end;
    private int minDP;
    private int limit;
    private List<String> metadata = new ArrayList<>(); // static ?
    private String header;
    private List<Sample> samples = new ArrayList<>();


    public VcfProcessor(int start, int end, int minDP, int limit) {
        this.start = start;
        this.end = end;
        this.minDP = minDP;
        this.limit = limit;
    }

    public void parseHeader(String line) {
        String[] headers = line.split("\t"); // splits the line on tabs

        this.header = String.join("\t", Arrays.asList(headers).subList(0, 9)); // stores the header up to the format as a String

        for(int i=9; i<headers.length; i++) { // iterates over each sample name
            Sample newSample = new Sample(headers[i], this.metadata, this.header, this.limit);
            samples.add(newSample); // add the new sample to the list
        }
    }

    public String parseMeta(VcfFileReader reader) throws IOException {
        String line = "0";
        while (reader.hasNext()) {
            line = reader.next();
            if (line.startsWith("##")) {
                metadata.add(line);
            } else {
                break;
            }
        }
        return line;
    }

    public boolean processVcf(VcfFileReader reader) throws IOException {
        boolean success = true;
        try {

            String head = parseMeta(reader);
            if (head != "0") {
                parseHeader(head);
            }
            while (reader.hasNext() && samples.stream().anyMatch(sample -> !sample.getStatus())){
                // while not all samples finished
                String line = reader.next();
                String[] columns = line.split("\t");
                String[] attr = Arrays.copyOfRange(columns, 0, 9);
                String[] samplesInfo = Arrays.copyOfRange(columns, 9, columns.length);
                int pos = Integer.parseInt(attr[1]); // assuming POS is in the second column
                // DP
//                String info = attr[7]; // assuming INFO is in the eighth column
//                String dpStr = extractDP(info); // a method to extract DP from INFO (you need to implement it)
//                int dp = Integer.parseInt(dpStr);
                //

                if (pos >= start && pos <= end) {
//                    if (dp > minDP) {
//                    }
                    Variant variant = new Variant(attr);

                    // add gene
                    try {
                        String gene = ApiUtils.fetchGeneFromVariant(variant);
                        variant.setGene(gene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
