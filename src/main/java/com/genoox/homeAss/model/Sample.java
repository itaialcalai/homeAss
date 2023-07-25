package com.genoox.homeAss.model;
import java.util.List;
import java.util.ArrayList;

public class Sample {
    private boolean finished;
    private List<String> metadata;
    private String header;
    private String name;
    private List<String> variants;
    private int limit;

    public Sample(String name, List<String> metadata, String header, int limit) {
        this.name = name;
        this.metadata = metadata;
        this.header = header;
        this.variants = new ArrayList<>();
        this.limit = limit;
        this.finished = false;
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

    public boolean getStatus(){
        return this.finished;
    }
}
