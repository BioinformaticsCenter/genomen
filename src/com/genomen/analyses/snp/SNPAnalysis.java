package com.genomen.analyses.snp;

import com.genomen.core.AnalysisTask;
import com.genomen.analyses.Analysis;
import com.genomen.core.Sample;
import com.genomen.core.Results;
import com.genomen.core.SNPResultEntity;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.RuleDAO;
import com.genomen.scripts.LogicResult;
import com.genomen.scripts.JythonLogicExecutor;
import java.util.ArrayList;
import java.util.List;


/**
 * Defines an expert system based analysis performed on SNP data.
 * @author ciszek
 */
public class SNPAnalysis extends Analysis {


    public String getTag() {
        return this.getClass().getName();
    }

    @Override
    public void analyze( AnalysisTask analysisTask  ) {

        ArrayList<Rule> ruleList = new ArrayList<Rule>();
        //Create a data access object for extracting rules from the database.
        RuleDAO ruleDAO = DAOFactory.getDAOFactory().getRuleDAO();
        //Add all available rules from the database to the list of rules.
        ruleList.addAll( ruleDAO.getRules(true) );

        List<Sample> samples = analysisTask.getSamples();

        //Perform all available sub-analyses (rules) for each sample
        for ( int i = 0; i < samples.size(); i++) {
            Results results = new Results(this.getTag(), false);
            performSubAnalyses(analysisTask, samples.get(i), ruleList, results );
            analysisTask.addResults(samples.get(i).getId(), this.getTag(), results);
        }

    }

    private void performSubAnalyses( AnalysisTask analysisTask, Sample sample, List<Rule> ruleList, Results results) {

        JythonLogicExecutor jythonLogicExecutor = new JythonLogicExecutor(analysisTask);

       //Loop through all listed rules.
        for ( int i = 0; i < ruleList.size(); i++) {
            
            LogicResult logicResult = jythonLogicExecutor.execute( ruleList.get(i).getLogic(), sample, ruleList.get(i).getInterestLevel() );

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
