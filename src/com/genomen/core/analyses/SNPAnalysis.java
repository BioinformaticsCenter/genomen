package com.genomen.core.analyses;

import com.genomen.core.AnalysisTask;
import com.genomen.core.AnalyzationLogic;
import com.genomen.core.Individual;
import com.genomen.core.Results;
import com.genomen.core.Rule;
import com.genomen.core.SNPResultEntity;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.RuleDAO;
import com.genomen.core.scripts.LogicResult;
import com.genomen.core.scripts.JythonLogicExecutor;
import java.util.ArrayList;
import java.util.List;


/**
 * Defines an expert system based analysis performed on SNP data.
 * @author ciszek
 */
public class SNPAnalysis extends AnalyzationLogic {


    public String getTag() {
        return this.getClass().getName();
    }

    @Override
    public void analyze( AnalysisTask analysisTask  ) {

        ArrayList<Rule> ruleList = new ArrayList<Rule>();
        //Create a data access object for extracting rules from the database.
        RuleDAO ruleDAO = DAOFactory.getDAOFactory().getRuleDAO();
        //Add all available rules from the database to the list of rules to be analyzed.
        ruleList.addAll( ruleDAO.getRules(true) );

        List<Individual> individuals = analysisTask.getIndividuals();

        //Perform all available sub-analyses (rules) for each individual
        for ( int i = 0; i < individuals.size(); i++) {
            Results results = new Results(this.getTag(), false);
            performSubAnalyses( analysisTask, individuals.get(i), ruleList, results );
            analysisTask.addResults( individuals.get(i).getId(), this.getTag(), results);
        }

    }

    private void performSubAnalyses( AnalysisTask analysisTask, Individual individual, List<Rule> ruleList, Results results) {

        JythonLogicExecutor jythonLogicExecutor = new JythonLogicExecutor(analysisTask);

       //Loop through all listed rules.
        for ( int i = 0; i < ruleList.size(); i++) {
            
            LogicResult logicResult = jythonLogicExecutor.execute( ruleList.get(i).getLogic(), individual, ruleList.get(i).getInterestLevel() );

            //If the logic script executed fails to return LogicResult, move to next iteration
            if ( logicResult == null ) {
                continue;
            }

            String effectType = ruleList.get(i).getEffectType();
            String traitSymbolicName = ruleList.get(i).getTraitSymbolicName();
            String ruleID = ruleList.get(i).getId();

            SNPResultEntity snpResultEntity;
            double interestLevel = logicResult.getInterestLevel();
            String effect = logicResult.getValue();
            snpResultEntity = new SNPResultEntity( effect, effectType, traitSymbolicName, interestLevel, ruleID, logicResult.getMissingGenotypes(), logicResult.getMissingPhenotypes() );

            snpResultEntity.addTag(this.getTag());
            results.addResult(snpResultEntity);
        }
    }


}
