package com.genoox.homeAss.model;
import java.util.List;
import java.util.ArrayList;
import com.genoox.homeAss.io.VcfWriter;

public class Sample {
    private boolean finished;
    private List<String> metadata;
    private String header;
    private String name;
    private List<String> variants;
    private int limit;
    private VcfWriter writer;

    public Sample(String name, List<String> metadata, String header, int limit) {
        this.name = name;
        this.metadata = metadata;
        this.header = header;
        this.variants = new ArrayList<>();
        this.limit = limit;
        this.finished = false;
        this.writer = new VcfWriter();
    }

    public void addVariant(Variant variant, String info) {
        String line = variant.toString();
        String compLine = line + "\t" + info;
        this.variants.add(compLine);

        if(this.variants.size() == this.limit) {
            outputSample();
            this.finished = true;
        }
    }

    public void outputSample() {
        String filePath = "outputs/" + this.name + "_filtered.vcf";
        writer.writeSample(this, filePath);
    }

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
