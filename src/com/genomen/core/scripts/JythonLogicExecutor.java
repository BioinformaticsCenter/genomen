package com.genomen.core.scripts;

import com.genomen.core.AnalysisTask;
import com.genomen.core.entities.DataEntity;
import com.genomen.core.entities.DataType;
import com.genomen.core.entities.DataTypeManager;
import com.genomen.core.Individual;
import com.genomen.core.Rule;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.RuleDAO;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;

/**
 * Executes analysis rules presented as jython scripts
 * @author ciszek
 */
public class JythonLogicExecutor {

    private AnalysisTask analysisTask;
    private ScriptEngine scriptEngine;

    /**
     * Constructs a logic executor for the given analysis task
     * @param p_analysisTask task on which the executor is to be used
     */
    public JythonLogicExecutor( AnalysisTask p_analysisTask ) {

        analysisTask = p_analysisTask;
        ScriptEngineManager manager = new ScriptEngineManager ();
        scriptEngine = manager.getEngineByName ("jython");
        String script = JythonBaseScriptReader.getInstance().getBaseScript();

        try {
            scriptEngine.put("jythonLogicExecutor", this );
            scriptEngine.eval(script);
        }
        catch (ScriptException ex) {
            Logger.getLogger( JythonLogicExecutor.class ).error(ex);
        }
    }

    /**
     * Compares given value to source data
     * @param type type of the data
     * @param individual individual(source) to whom the data is associated
     * @param genotypeId genotype id of the data
     * @param attribute name of the attribute 
     * @param value value of the data
     * @return result of comparison as an instance of <code>LogicResult</code>
     */
    public LogicResult compareToData( String type, Individual individual, String genotypeId, String attribute,  String value ) {

        DataType dataType = DataTypeManager.getDataType(type);
        
        LogicResult result = new LogicResult();
        result.setType(LogicResult.BOOLEAN);

        DataEntity dataEntity = analysisTask.getData( dataType, individual.getId(), genotypeId );


        if ( dataEntity == null ) {
            result.addMissingGenotype(genotypeId);
            result.setResult(true);
            result.setValue(value);
            result.setUnresolvable(true);
        }
        else {
            result.setValue(dataEntity.getDataEntityAttribute(attribute).getString());
        }

        result.compareToAllele(value);

        return result;
    }
    
    /**
     * Executes a rule on an individual
     * @param individual individual
     * @param id rule id
     * @return result of rule evaluation
     */
    public LogicResult executeRule( Individual individual, String id ) {

        RuleDAO ruleDAO = DAOFactory.getDAOFactory().getRuleDAO();
        Rule rule = ruleDAO.getRule(id);
        LogicResult result = null;

        if ( rule == null ) {

            return result;
        }

        result = execute(rule.getLogic(), individual, rule.getInterestLevel());

        return result;
    }    

    /**
     * Applies the a rule to an individual
     * @param logic id of the logic to be applied
     * @param individual an individual to whom the rule is applied
     * @param defaultInterestLevel the default interest level of the rule
     * @return results of the decision rule
     */
    public LogicResult execute(String logic, Individual individual, int defaultInterestLevel ) {

        LogicResult result = null;

        try {
            scriptEngine.put("defaultInterestLevel", defaultInterestLevel );
            scriptEngine.put("individual", individual );
            scriptEngine.eval(logic);
            Object object = scriptEngine.eval("result.getResult()");
            result = (LogicResult)object;
        }
        catch (ScriptException ex) {
            Logger.getLogger( JythonLogicExecutor.class ).error(ex);
        }
        
        return result;
    }


}
