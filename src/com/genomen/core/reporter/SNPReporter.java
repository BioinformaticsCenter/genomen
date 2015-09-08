package com.genomen.core.reporter;

import com.genomen.core.AnalysisTask;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.RuleDAO;
import com.genomen.dao.TraitDAO;
import com.genomen.core.ResultEntity;
import com.genomen.core.Results;
import com.genomen.core.SNPResultEntity;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Reporter for the results of SNP analyses.
 * @author ciszek
 */
public class SNPReporter extends Reporter {


    private static final String REQUIRED_TAG = "com.genomen.core.analyses.SNPAnalysis";
    private static final String[] TABLE_HEADERS = {"Status", "TraitName", "InterestLevel", "EffectSize", "Resolvable", "Logic"};
    private static final String[] TABLE_HEADER_DESCRIPTIONS = {"Status", "Trait Name" , "Interest Level", "Effect Size", "Resolvable", "Logic"};
    private static final String TABLE_TITLE = "SNP";

    private static final String RESOLVABLE = "True";
    private static final String UNRESOLVABLE = "False";

    @Override
    public void createReportComponent( AnalysisTask analysisTask, ReportComponent reportComponent, String language ) {

        IndividualEntry individualEntry = (IndividualEntry)reportComponent;
        
        Results snpResults = null;
        List<Results> individualsResults = analysisTask.getResultsList( individualEntry.getIndividual().getId() );
        //Search the result list for required results.
        for ( int i = 0; i < individualsResults.size(); i++) {

            if ( individualsResults.get(i).getTag().equals(REQUIRED_TAG)) {
                snpResults = individualsResults.get(i);
                break;
            }
        }
        //If required results are found
        if ( snpResults != null) {
            addTraitTables(individualEntry, language, snpResults);
        }
        
    }
    
    private HashMap<String, List<SNPResultEntity>> createTraitListMap( List<ResultEntity> results ) {

        HashMap<String, List<SNPResultEntity>> traitListMap = new HashMap<String, List<SNPResultEntity>>();

        //Loop through result entities.
        for ( int i = 0; i < results.size(); i++) {

             SNPResultEntity snpResultEntity = (SNPResultEntity)results.get(i);
             //If the map does not already contain a list matching the trait in question, create a new list.
             if ( !traitListMap.containsKey(snpResultEntity.getTraitSymbolicName())) {
                traitListMap.put(snpResultEntity.getTraitSymbolicName(), new LinkedList<SNPResultEntity>());
             }
             //Add result entity to a matching list.
             traitListMap.get(snpResultEntity.getTraitSymbolicName()).add(snpResultEntity);
        }
        return traitListMap;
    }

    private void addTraitTables( IndividualEntry individualEntry, String language, Results results) {

        HashMap<String, List<SNPResultEntity>> traitListMap = createTraitListMap(results.getResultEntities());
        List<String> keyList = new LinkedList( traitListMap.keySet() );
        TraitDAO traitDAO = DAOFactory.getDAOFactory().getTraitDAO();

        TraitTable traits = new TraitTable(TABLE_HEADERS, TABLE_HEADER_DESCRIPTIONS, TABLE_TITLE);
        
        //Loop through a list of unique traits
        for ( int i = 0; i < keyList.size(); i++ ) {

            String traitName = traitDAO.getTraitName(keyList.get(i), language );
            String shortDescription = traitDAO.getShortDescription(keyList.get(i), language);
            String longDescription = traitDAO.getDetailedDescription(keyList.get(i), language);
            //Create a new trait table for the trait.
            TraitEntry traitEntry = new TraitEntry( TABLE_HEADERS, TABLE_HEADER_DESCRIPTIONS, traitName, shortDescription, longDescription );
            //Add all results associated with this trait to the map.
            addRows( traitListMap.get( keyList.get(i) ), traitEntry, language );
            //Add the result to the report
            traits.addRow(traitEntry);
        }
        individualEntry.addComponent(traits);

    }

    private static void addRows( List<SNPResultEntity> snpResults, TraitEntry entry, String language ) {

        RuleDAO ruleDAO = DAOFactory.getDAOFactory().getRuleDAO();
        //Loop through all results.
        for ( int i = 0; i < snpResults.size(); i++) {

            SNPResultEntity snpResultEntity = snpResults.get(i);

            String[] values = new String[TABLE_HEADERS.length];

            String interestLevel = Double.toString(snpResultEntity.getInterestLevel());
            String traitName = entry.getTable().getTitle();
            String effect = "";
            String traitStatus = "";
            String resolvable = RESOLVABLE;
            String logic = ruleDAO.getRuleLogic(snpResultEntity.getRuleID());

            if ( snpResultEntity.getEffectType().equals("NUMERIC") || snpResultEntity.getEffectType().equals("RISK")) {
                effect =  snpResultEntity.getEffect();
            }
            else {
                traitStatus = ruleDAO.getResultDescription(snpResultEntity.getEffect(), language);
            }
            //If the result is based on partial data, mark the result unresolvable.
            if (!snpResultEntity.isResolved()) {
                resolvable = UNRESOLVABLE;
            }

            values[0] = traitStatus;
            values[1] = traitName;
            values[2] = interestLevel;
            values[3] = effect;
            values[4] = resolvable;
            values[5] = logic;

            entry.getTable().addRow(values);
        }


    }


}
