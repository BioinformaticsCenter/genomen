package com.genomen.tools.snpedia;

import java.net.MalformedURLException;
import java.util.ArrayList;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 *
 * @author jussi
 */
public class SNPediaBot {

    MediaWikiBot bot;

    public SNPediaBot() throws MalformedURLException {

        // TODO: Add username/password information using bot.login() method
        this.bot = new MediaWikiBot("http://snpedia.com/");
    }

    /**
     * Gets a list of names of all entries in the specified category in the wiki.
     * @param category Name of the category, e.g. "Is_a_snp".
     * @return List of entries in the specified category.
     * @throws Exception
     */
    public ArrayList<String> getCategoryEntryNames(String category) throws Exception {

        ArrayList<String> returnItems = new ArrayList<String>();
        CategoryMembersSimple items = new CategoryMembersSimple(this.bot, category);

        while (items.hasNext()) {
            returnItems.add(items.next());
        }

        return returnItems;
    }

    /**
     * Get a list of names of all SNP entries in the wiki.
     * @return
     * @throws Exception
     */
    public ArrayList<String> getSnpEntryNames() throws Exception {
        ArrayList<String> returnItems = this.getCategoryEntryNames("Is_a_snp");
        return returnItems;
    }

    /**
     * Get a list of names of all genotype entries in the wiki.
     * @return
     * @throws Exception
     */
    public ArrayList<String> getGenotypeEntryNames() throws Exception {
        ArrayList<String> returnItems = this.getCategoryEntryNames("Is_a_genotype");
        return returnItems;
    }

    /**
     * Retrieve a specified entry from the wiki.
     * @param entryName Name of the entry to retrieve.
     * @return
     * @throws Exception
     */
    public Entry getEntry(String entryName) throws Exception {

        Entry entry = new Entry(entryName, new SimpleArticle(this.bot.readContent(entryName)).getText());

        return entry;
    }

    /**
     * Retrieve a list of specified entries from the wiki.
     * @param entryNames Name of entries to retrieve.
     * @return
     * @throws Exception
     */
    public ArrayList<Entry> getEntries(ArrayList<String> entryNames) throws Exception {

        ArrayList<Entry> returnEntries = new ArrayList<Entry>();
        for (String entryName : entryNames) {
            returnEntries.add(getEntry(entryName));
            System.out.println(returnEntries.size() + " / " + entryNames.size() + " entries fetched.");
        }
        return returnEntries;
    }
}
