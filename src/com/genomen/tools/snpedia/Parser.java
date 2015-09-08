package com.genomen.tools.snpedia;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jussi
 */
public class Parser {

    EntryErrorLog errorLog = new EntryErrorLog();

    public EntryErrorLog getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(EntryErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    /**
     * Checks if content of entry can be encoded as UTF-8. Will record encoding errors to error log.
     * @param entry
     * @return True if encoded text equals original text.
     */
    public boolean checkEncoding(Entry entry) {

        String text = entry.getContent();
        String encodedString = null;
        try {
            encodedString = com.genomen.utils.StringUtils.forceEncoding(text, "UTF-8");
            if (!encodedString.equals(text)) {
                errorLog.logError(entry.getName(), "Non-UTF-8 characters detected while encoding.");
            }
        } catch (Exception e) {
            errorLog.logError(entry.getName(), "Error while checking UTF-8 encoding.");

        }
        return (encodedString.equals(text));
    }

    /**
     * Check if there is matching number of opening and closing square- & curly-brackets in the content of the entry.
     * @param entry
     * @return
     */
    public boolean checkBracketCount(Entry entry) {

        String text = entry.getContent();
        int bracketCounter = 0;

        Pattern pattern = Pattern.compile("\\{|\\[");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            bracketCounter++;
        }

        pattern = Pattern.compile("\\}|\\]");
        matcher = pattern.matcher(text);
        while (matcher.find()) {
            bracketCounter--;
        }

        if (!(bracketCounter == 0)) {
            errorLog.logError(entry.getName(), "Non-matching number of opening and closing square-/curly-brackets.");
        }
        return (bracketCounter == 0);
    }

    public TemplateContent populateTemplateContent(String[] elements) {

        TemplateContent tc = new TemplateContent();
        int counter = 0;
        for (String element : elements) {
            element = element.trim();
            // Set first element as id
            if (counter == 0) {
                tc.setId(element.toLowerCase());
            } else {
                String subElements[] = element.split("=");
                // If element is of type key=value, then pick key and value, 
                // otherwise pick whole element as key and "" as value.
                if (subElements.length == 2) {
                    tc.getElements().put(subElements[0].trim(), subElements[1].trim());
                } else {
                    tc.getElements().put(element.trim(), "");
                }
            }
            counter++;
        }
        return tc;
    }

    public void checkMultipleTemplates(ParsedEntry parsedEntry) {

        ArrayList<TemplateContent> templateContents = parsedEntry.getTemplates();

        int rsnumCounter = 0;
        int genotypeCounter = 0;
        for (TemplateContent tc : templateContents) {
            if (tc.getId().equals("rsnum")) {
                rsnumCounter++;
            }
            if (tc.getId().equals("genotype")) {
                genotypeCounter++;
            }

        }
        if (rsnumCounter > 1) {
            errorLog.logError(parsedEntry.getId(), "Multiple Rsnum-templates.");
        }
        if (genotypeCounter > 1) {
            errorLog.logError(parsedEntry.getId(), "Multiple Genotype-templates.");
        }

    }

    public ParsedEntry parseTemplates(Entry entry) {

        String text = com.genomen.utils.StringUtils.removeLineFeeds(entry.getContent());

        ParsedEntry parsedEntry = new ParsedEntry();
        parsedEntry.setId(entry.getName());
        // Remove templates
        String bodyText = text.replaceAll("\\{\\{.*?\\}\\}", "").trim();
        // Remove double square brackets
        bodyText = bodyText.replaceAll("\\[\\[|\\]\\]", "").trim();
        parsedEntry.setBody(bodyText);

        // Process templates
        Pattern pattern = Pattern.compile("\\{\\{.*?\\}\\}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String match = matcher.group();
            // Remove surrounding square brackets
            match = match.substring(2, match.length() - 2);

            TemplateContent tc = populateTemplateContent(match.split("\\|"));
            parsedEntry.getTemplates().add(tc);

        }
        return parsedEntry;
    }

    public ParsedEntry parseEntry(Entry entry) {

        //errorLog.logError(entry.getName(), "Parsed this.");
        boolean checkBracketCount = checkBracketCount(entry);
        boolean checkEncoding = checkEncoding(entry);

        ParsedEntry parsedEntry = parseTemplates(entry);
        checkMultipleTemplates(parsedEntry);
        return parsedEntry;
    }

    public ArrayList<ParsedEntry> parseEntries(ArrayList<Entry> entries) {

        ArrayList<ParsedEntry> parsedEntries = new ArrayList<ParsedEntry>();

        for (Entry entry : entries) {
            parsedEntries.add(this.parseEntry(entry));
        }
        return parsedEntries;
    }
}
