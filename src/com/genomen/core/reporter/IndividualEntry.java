package com.genomen.core.reporter;

import com.genomen.core.Individual;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.log4j.Logger;

/**
 * Results associated with a specific individual
 * @author ciszek
 */
public class IndividualEntry extends CompositeReportComponent {

    private Individual individual;

    /**
     * Gets the individual whose results are presented in this entry.
     * @return whose results are presented in this entry
     */
    public Individual getIndividual() {
        return individual;
    }

    /**
     * Creates an entry for the results associated with the individual given as a parameter.
     * @param p_individual individual whose results are presented in this entry
     */
    public IndividualEntry( Individual p_individual ) {
        individual = p_individual;
    }

    @Override
    public void writeXML(BufferedWriter bufferedWriter) {

        try {
            bufferedWriter.write("<individual>");
            bufferedWriter.write("\n\t<id>" + individual.getId() + "</id>\n");
            for (int i = 0; i < getComponents().size(); i++) {
                getComponents().get(i).writeXML(bufferedWriter);
            }
            bufferedWriter.write("\n</individual>");
        } catch (IOException ex) {
            Logger.getLogger( IndividualEntry.class ).error(ex);
        }

    }

    @Override
    public void print(PrintStream printStream) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
