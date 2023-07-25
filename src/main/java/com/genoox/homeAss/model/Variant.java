// Itai Alcalai 206071110
package com.genoox.homeAss.model;

// Defines a public class that holds all information for a single variant
public class Variant {
    private String chromosome;
    private int position;
    private String id;
    private String ref;
    private String alt;
    private String qual;
    private String filter;
    private String info;
    private String format;
    private String gene;  // to store gene from fetch_variant_details API

    // constructor recieves a list of the variants data
    public Variant(String[] data) {
        this.chromosome = data[0];
        this.position = Integer.parseInt(data[1]);
        this.id = data[2];
        this.ref = data[3];
        this.alt = data[4];
        this.qual = data[5];
        this.filter = data[6];
        this.info = data[7];
        this.format = data[8];
    }

    // add getters and setters for all fields
    public String getChromosome() {
        return chromosome;
    }

    public int getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getQual() {
        return qual;
    }

    public String getFilter() {
        return filter;
    }

    public String getInfo() {
        return info;
    }

    public String getFormat() {
        return format;
    }

    public String getGene() {
        return gene;
    }

    // set the gene and update the INFO line
    public void setGene(String gene) {
        this.gene = gene;
        this.info += ";GENE=" + gene;
    }
    // concat all components to the vcf file format
    @Override
    public String toString() {
        return chromosome + "\t" + position + "\t" + id + "\t" + ref + "\t" + alt + "\t" + qual + "\t" + filter + "\t" + info + "\t" + format;
    }

}
