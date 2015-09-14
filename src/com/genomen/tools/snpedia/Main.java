package com.genomen.tools.snpedia;

import java.util.ArrayList;

/**
 *
 * @author jussi
 */
public class Main {

    public static void getAllSnps(SNPediaBot bot, String fileName) throws Exception {

        ArrayList<String> entryNames = bot.getSnpEntryNames();
        System.out.println("Got entry list: " + entryNames.size());
        EntryFileManager.saveEntries(bot.getEntries(entryNames), fileName);
    }

    public static void getAllGenotypes(SNPediaBot bot, String fileName) throws Exception {

        ArrayList<String> entryNames = bot.getGenotypeEntryNames();
        System.out.println("Got entry list: " + entryNames.size());
        EntryFileManager.saveEntries(bot.getEntries(entryNames), fileName);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting..");

            /*
            // Uncomment to retrieve entries from the SNPedia website. Will likely take several hours.
            SNPediaBot bot = new SNPediaBot();
            getAllGenotypes(bot, "snpedia_work/genotypes.txt");
            getAllSnps(bot, "snpedia_work/snps.txt");
             */

            ArrayList<Entry> snpEntries = EntryFileManager.loadEntries("snpedia_work/snps.txt");
            ArrayList<Entry> genotypeEntries = EntryFileManager.loadEntries("snpedia_work/genotypes.txt");

            Parser parser = new Parser();

            ArrayList<ParsedEntry> parsedSnps = parser.parseEntries(snpEntries);
            ArrayList<ParsedEntry> parsedGenotypes = parser.parseEntries(genotypeEntries);

            GenOmenExporter.saveXml("snpedia_work/snpedia_export.xml", GenOmenExporter.combineSnpsAndGenotypesToXML(parsedSnps, parsedGenotypes));


            System.out.println(parser.getErrorLog().toString());
            //System.out.println(parser.getErrorLog().toWikiMediaString());


            System.out.println("Done.");

        } catch (Exception ex) {
                System.out.println(ex.toString());
        }
    }
}
