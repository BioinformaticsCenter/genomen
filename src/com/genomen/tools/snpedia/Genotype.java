package com.genomen.tools.snpedia;

/**
 *
 * @author jussi
 */
public class Genotype {

    private String id;
    private String body = "";
    private String snp;
    private String allele1 = "";
    private String allele2 = "";
    private String summary = "";
    private float magnitude = 0;

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAllele1() {
        return allele1;
    }

    public void setAllele1(String allele1) {
        this.allele1 = allele1;
    }

    public String getAllele2() {
        return allele2;
    }

    public void setAllele2(String allele2) {
        this.allele2 = allele2;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
    }
    
}
