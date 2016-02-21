package com.genomen.analyses;

import com.genomen.core.AnalysisTask;
import org.w3c.dom.Element;

/**
 * Base class for different analyses.
 * @author ciszek
 */
public abstract class AnalyzationLogic {


    private Element element;

    /**
     * Returns the DOM element containing the parameters used by this analysis.
     * @return <code>Element</code> containing analysis parameters
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the tag of this analysis.
     * @return
     */
    public abstract String getTag();

    public abstract void analyze( AnalysisTask p_analysisTask );

    /**
     * Initializes the AnalyzationLogic according to the data presented in XML form given
     * @param p_element <code>Element</code> containing the initialization data.
     */
    public void initialize( Element p_element  ) {
        element = p_element;
    }


}
