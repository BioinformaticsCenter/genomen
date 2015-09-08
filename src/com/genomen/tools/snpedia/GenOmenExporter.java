package com.genomen.tools.snpedia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author jussi
 */
public class GenOmenExporter {

    public static Genotype createGenotype(ParsedEntry entry) {

        Genotype genotype = new Genotype();

        genotype.setId(entry.getId());
        genotype.setBody(entry.getBody());

        for (TemplateContent tc : (entry.getTemplates())) {
            String templateId = tc.getId();

            if (templateId.equals("genotype")) {

                // Set id for the SNP, first check if rsid is available
                String rsid = (String) tc.getElements().get("rsid");
                if (rsid != null) {
                    genotype.setSnp("rs" + rsid);
                } else {
                    // If rsid is not found, try to find Illumina id (iid)
                    String iid = (String) tc.getElements().get("iid");
                    if (iid != null) {
                        genotype.setSnp("I" + iid);
                    }
                }

                String summary = (String) tc.getElements().get("summary");
                if (summary != null) {
                    genotype.setSummary(summary);
                }

                String allele1 = (String) tc.getElements().get("allele1");
                if (allele1 != null) {
                    genotype.setAllele1(allele1);
                }

                String allele2 = (String) tc.getElements().get("allele2");
                if (allele2 != null) {
                    genotype.setAllele2(allele2);
                }


                String magnitude = (String) tc.getElements().get("magnitude");

                if (magnitude != null) {
                    // Convert magnitude String into float
                    genotype.setMagnitude(Float.valueOf(magnitude.trim()).floatValue());
                }
            }
        }

        if (genotype.getId() == null || genotype.getSnp() == null) {
            genotype = null;
        }

        return genotype;
    }

    public static Snp createSnp(ParsedEntry entry) {

        Snp snp = new Snp();

        snp.setBody(entry.getBody());

        for (TemplateContent tc : (entry.getTemplates())) {
            String templateId = tc.getId();


            if (templateId.equals("rsnum")) {

                // Set id for the SNP, first check if rsid is available
                String rsid = (String) tc.getElements().get("rsid");
                if (rsid != null) {
                    snp.setId("rs" + rsid);
                } else {
                    // If rsid is not found, try to find Illumina id (iid)
                    String iid = (String) tc.getElements().get("iid");
                    if (iid != null) {
                        snp.setId("I" + iid);
                    }
                }

                String summary = (String) tc.getElements().get("summary");
                if (summary != null) {
                    snp.setSummary(summary);
                }
            }
        }

        if (snp.getId() == null) {
            snp = null;
        }

        return snp;
    }

    public static ArrayList<Snp> createSnps(ArrayList<ParsedEntry> parsedEntries) {

        ArrayList<Snp> snps = new ArrayList<Snp>();
        for (ParsedEntry parsedEntry : parsedEntries) {
            Snp snp = createSnp(parsedEntry);
            if (snp != null) {
                snps.add(snp);
            }
        }
        return snps;
    }

    public static ArrayList<Genotype> createGenotypes(ArrayList<ParsedEntry> parsedEntries) {

        ArrayList<Genotype> genotypes = new ArrayList<Genotype>();

        for (ParsedEntry parsedEntry : parsedEntries) {
            Genotype genotype = createGenotype(parsedEntry);
            if (genotype != null) {
                genotypes.add(genotype);
            }
        }
        return genotypes;

    }

    /**
     * Get a set of unique SNP ids from a list of genotypes.
     * @param genotypes
     * @return SNP ids
     */
    public static Set<String> getSnpIds(ArrayList<Genotype> genotypes) {

        Set snpIds = new HashSet<String>();

        for (Genotype genotype : genotypes) {
            snpIds.add(genotype.getSnp());
        }

        return snpIds;

    }

    public static String createRule(ArrayList<Snp> snps, ArrayList<Genotype> genotypes) throws CharacterCodingException {

        String resultString = "";


        for (Snp snp : snps) {
            String snpId = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(snp.getId(), "UTF-8"));
            String snpSummary = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(snp.getSummary(), "UTF-8"));
            String snpBody = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(snp.getBody(), "UTF-8"));

            String symbolicName = "SNPEDIA_" + snpId;
            resultString += "<GENOTYPE><SYMBOLIC_NAME_ID>" + snpId + "</SYMBOLIC_NAME_ID></GENOTYPE>\n";
            resultString += "<TRAIT><SYMBOLIC_NAME_ID>" + symbolicName + "</SYMBOLIC_NAME_ID><CLASS>TRAIT</CLASS></TRAIT>\n";
            resultString += "<TRAIT_DESCRIPTION><SYMBOLIC_NAME_ID>" + symbolicName + "</SYMBOLIC_NAME_ID><LANGUAGE_SYMBOLIC_NAME_ID>ENG</LANGUAGE_SYMBOLIC_NAME_ID><TRAIT_SYMBOLIC_NAME_ID>" + symbolicName + "</TRAIT_SYMBOLIC_NAME_ID><NAME>" + snpId + "</NAME><SHORT_DESC>" + snpSummary + "</SHORT_DESC><DESCRIPTION>" + snpBody + "</DESCRIPTION></TRAIT_DESCRIPTION>\n";

            resultString += "<RULE><SYMBOLIC_NAME_ID>" + symbolicName + "</SYMBOLIC_NAME_ID><TRAIT_SYMBOLIC_NAME_ID>" + symbolicName + "</TRAIT_SYMBOLIC_NAME_ID><LOGIC>\n";

            int counter = 1;
            String results = "";
            for (Genotype genotype : genotypes) {
                String resultId = symbolicName + "_" + counter;
                String genotypeAllele1 = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(genotype.getAllele1(), "UTF-8"));
                String genotypeAllele2 = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(genotype.getAllele2(), "UTF-8"));
                String genotypeSummary = StringEscapeUtils.escapeXml(com.genomen.utils.StringUtils.forceEncoding(genotype.getSummary(), "UTF-8"));
                String genotypeMagnitude = "" + genotype.getMagnitude();

                resultString += "RESULT(GENO(\"" + snpId + "\", \"" + genotypeAllele1 + "/" + genotypeAllele2 + "\"), true_result=\"" + resultId + "\", true_interest=" + genotypeMagnitude + ")\n";
                results += "<RESULT_DESCRIPTION><SYMBOLIC_NAME_ID>" + resultId + "</SYMBOLIC_NAME_ID><LANGUAGE_SYMBOLIC_NAME_ID>ENG</LANGUAGE_SYMBOLIC_NAME_ID><RULE_SYMBOLIC_NAME_ID>" + symbolicName + "</RULE_SYMBOLIC_NAME_ID><DESCRIPTION>" + genotypeSummary + "</DESCRIPTION></RESULT_DESCRIPTION>\n";

                counter++;

            }

            resultString += "</LOGIC><EFFECT_TYPE>TEXT</EFFECT_TYPE><EFFECT_UNIT></EFFECT_UNIT><INTEREST_LEVEL>0</INTEREST_LEVEL><SUBRESULT>N</SUBRESULT></RULE>";
            resultString += results;
        }

        return resultString;
    }

    public static String getXmlHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TABLES>\n<LANGUAGE><SYMBOLIC_NAME_ID>ENG</SYMBOLIC_NAME_ID><NAME>English</NAME></LANGUAGE>\n";
    }

    public static String getXmlFooter() {
        return "</TABLES>\n";
    }

    public static String combineSnpsAndGenotypesToXML(ArrayList<ParsedEntry> parsedSnps, ArrayList<ParsedEntry> parsedGenotypes) throws CharacterCodingException {

        ArrayList<Snp> tmpSnps = createSnps(parsedSnps);
        ArrayList<Snp> snps = new ArrayList<Snp>();

        ArrayList<Genotype> genotypes = createGenotypes(parsedGenotypes);
        Set<String> snpIds = getSnpIds(genotypes);

        // Remove SNPs that are not linked to genotypes or don't have body text
        for (Snp snp : tmpSnps) {
            if (snpIds.contains(snp.getId()) && snp.getBody().length() > 1) {
                snps.add(snp);
            }

        }
        tmpSnps = null;

        String xmlString = "";

        xmlString += getXmlHeader();

        // Loop through all unique genotype related SNP ids and find corresponding SNP
        for (String snpid : snpIds) {
            ArrayList<Snp> collectedSnps = new ArrayList<Snp>();
            ArrayList<Genotype> collectedGenotypes = new ArrayList<Genotype>();

            for (Snp snp : snps) {
                if (snpid.equals(snp.getId())) {
                    collectedSnps.add(snp);

                    // If corresponding SNP is found, find all corresponding genotypes
                    for (Genotype genotype : genotypes) {
                        if (snpid.equals(genotype.getSnp())) {
                            collectedGenotypes.add(genotype);

                        }
                    }

                }
            }

            // Collected SNPs linked to this SNP id, and related genotypes
            if (collectedSnps.size() > 0 && collectedGenotypes.size() > 0) {
                String ruleString = createRule(collectedSnps, collectedGenotypes);
                xmlString += ruleString;
            }
        }
        xmlString += getXmlFooter();
        xmlString = com.genomen.utils.StringUtils.forceEncoding(xmlString, "UTF-8");
        return xmlString;

    }

    public static void saveXml(String fileName, String xml) {
        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            bufferedWriter.write(xml);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.toString());
        } finally {
            com.genomen.utils.ResourceReleaser.close(bufferedWriter);

        }
    }
}
