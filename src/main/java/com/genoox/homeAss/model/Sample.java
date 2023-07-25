// Itai Alcalai 206071110
package com.genoox.homeAss.model;

// import
import java.util.List;
import java.util.ArrayList;
import com.genoox.homeAss.io.VcfWriter;

// Defines a public class that holds all information for a single sample file
public class Sample {
    private boolean finished; // if reached limit
    private List<String> metadata;
    private String header;
    private String name; // sample name
    private List<String> variants;
    private int limit;
    private VcfWriter writer; // writer object for io

    // Constructor for the Sample class
    public Sample(String name, List<String> metadata, String header, int limit) {
        this.name = name;
        this.metadata = metadata;
        this.header = header;
        this.variants = new ArrayList<>();
        this.limit = limit;
        this.finished = false;
        this.writer = new VcfWriter();
    }

    // Method to add a variant and its corresponding info to the list of variants
    public void addVariant(Variant variant, String info) {
        // Convert variant object to string and add the samples corespanding information of that variant
        String line = variant.toString();
        String compLine = line + "\t" + info;

        // Add the complete line to the variants list
        this.variants.add(compLine);

        // If the number of variants reached the limit, output the sample and mark the sample as finished
        if(this.variants.size() == this.limit) {
            outputSample();
            this.finished = true;
        }
    }

    // this methods calls the class writer to io the sample content
    public void outputSample() {
        // set output file path
        String filePath = "outputs/" + this.name + "_filtered.vcf";
        writer.writeSample(this, filePath);
    }

    // print for debug
    public void printSample() {
        System.out.println(this.name);

        for(String metadataLine: this.metadata) {
            System.out.println(metadataLine);
        }

        System.out.println(this.header + "\t" + this.name);

        for(String variantLine: this.variants) {
            System.out.println(variantLine);
        }

        System.out.println("XXXXXXXXX\n");
    }

    // Getter methods
    public String getName() {
        return this.name;
    }

    public String getHeader() {
        return this.header;
    }

    public List<String> getMetadata() {
        return this.metadata;
    }

    public List<String> getVariants() {
        return this.variants;
    }

    public boolean getStatus(){
        return this.finished;
    }
}
