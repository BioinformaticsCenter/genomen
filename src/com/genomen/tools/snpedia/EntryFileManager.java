package com.genomen.tools.snpedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *
 * @author jussi
 */
public class EntryFileManager {

    static private String ENTRY_SEPARATOR = "##### ENTRY SEPARATOR ##### PREVIOUS ENTRY NAME: ";

  
    /**
     * Saves entries in to a text-file.
     * @param entries Entries to be saved.
     * @param fileName Filename of the save file.
     */
    public static void saveEntries(ArrayList<Entry> entries, String fileName) {

        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }
        BufferedWriter bufferedWriter = null;
        try {
            // TODO: Force UTF-8 encoding on entries
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));

            for (Entry entry : entries) {
                bufferedWriter.write(entry.getContent());
                bufferedWriter.write("\n" + ENTRY_SEPARATOR + entry.getName() + "\n");

            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.toString());
        } finally {

            com.genomen.utils.ResourceReleaser.close(bufferedWriter);
        }
    }

    /**
     * Loads entries from a text-file.
     * @param fileName Filename of the file to load.
     * @return
     */
    public static ArrayList<Entry> loadEntries(String fileName) {

        ArrayList<Entry> returnEntries = new ArrayList<Entry>();

        File file = new File(fileName);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            String line;
            String tmpContent = "";

            while ((line = bufferedReader.readLine()) != null) {

                // Check if the line is an entry separator line
                if (!(line.length() > ENTRY_SEPARATOR.length() && line.substring(0, ENTRY_SEPARATOR.length()).equals(ENTRY_SEPARATOR))) {
                    tmpContent += line + "\n";
                } else {
                    // Pick entry name from the end of the entry separator line
                    String tmpName = line.substring(ENTRY_SEPARATOR.length(), line.length());

                    returnEntries.add(new Entry(tmpName, tmpContent));
                    tmpContent = "";
                }

            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.toString());
        } finally {
                     com.genomen.utils.ResourceReleaser.close(bufferedReader);


        }

        return returnEntries;
    }
}
