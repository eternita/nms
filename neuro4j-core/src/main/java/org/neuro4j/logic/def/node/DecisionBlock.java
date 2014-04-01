package org.neuro4j.logic.def.node;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.neuro4j.core.Connected;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;
import org.neuro4j.logic.swf.enums.DecisionCompTypes;
import org.neuro4j.logic.swf.enums.DecisionOperators;

public class DecisionBlock extends LogicBlock {

	private static final String NEXT_EXIT_RELATION = SWFConstants.NEXT_RELATION_NAME;
	private static final String FALSE_EXIT_RELATION = "FALSE";
	
	private DecisionOperators operator = null;
	private DecisionCompTypes compTypes = null;
	private String decisionKey = null;
	private String comparisonKey = null;
	private String trueExit = null;
	private String falseExit = null;
	
	public DecisionBlock() {
		super();
	}

	public int execute(LogicContext fctx)
			throws FlowExecutionException {
		
		switch (operator) {
		case DEFINED:
			Object decisionValue = fctx.get(decisionKey);
			if (decisionValue == null)
			{				
				fctx.setNextRelation(falseExit);
			} else {	
				fctx.setNextRelation(trueExit);
			}
			break;
		case UNDEFINED:
			decisionValue = fctx.get(decisionKey);
			if (decisionValue == null)
			{
				fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}			
			break;
		case EMPTY_STR:
			decisionValue = fctx.get(decisionKey);
			if (decisionValue != null && decisionValue instanceof String && "".equals(decisionValue.toString().trim()))
			{
				fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;	
		case EQ_STR:
			decisionValue = fctx.get(decisionKey);
		
			
			Object compValue = getComparisonValue(fctx);
			
			if (decisionValue != null && compValue != null && decisionValue instanceof String && compValue instanceof String  && decisionValue.toString().equals(compValue.toString()))
			{
				fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;	
		case NEQ_STR:
			decisionValue = fctx.get(decisionKey);
			compValue = getComparisonValue(fctx);
			
			if (decisionValue != null && compValue != null && !decisionValue.toString().equals(compValue.toString()))
			{
				fctx.setNextRelation(falseExit);
			} else {				
				fctx.setNextRelation(trueExit);
			}
			break;	
		case NEQ:
			decisionValue = fctx.get(decisionKey);			
			compValue = getComparisonValue(fctx);

	        double numberValue = ((Number)decisionValue).doubleValue();
	        double numberCompareValue = new Double((String)compValue).doubleValue();
			
	        if (numberValue == numberCompareValue)
			{
	        	fctx.setNextRelation(falseExit);
			} else {				
				fctx.setNextRelation(trueExit);
			}
			break;	
		case LESS:
			decisionValue = fctx.get(decisionKey);			
			compValue = getComparisonValue(fctx);
			
			if (compValue == null){
				fctx.setNextRelation(falseExit);
				break;
			}
			
	         numberValue = ((Number)decisionValue).doubleValue();
	         numberCompareValue = new Double((String)compValue).doubleValue();
			
	        if (numberValue < numberCompareValue)
			{
	        	fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;
		case GREATER:
			decisionValue = fctx.get(decisionKey);			
			compValue = getComparisonValue(fctx);
			if (compValue == null){
				fctx.setNextRelation(falseExit);
				break;
			}
	         numberValue = ((Number)decisionValue).doubleValue();
	         numberCompareValue = new Double((String)compValue).doubleValue();
			
	        if (numberValue > numberCompareValue)
			{
	        	fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;			
		case EQ:
			decisionValue = fctx.get(decisionKey);
			
			compValue = getComparisonValue(fctx);

	        numberValue = ((Number)decisionValue).doubleValue();
	        numberCompareValue = new Double((String)compValue).doubleValue();
	        
			if (numberValue == numberCompareValue)
			{
				fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;	
		case HAS_EL:
			
			Object value = fctx.get(decisionKey);
            boolean result = false;
	        if (value != null)
	        {
	          if ((value instanceof Iterator))
	          {
	            result = ((Iterator)value).hasNext();
	          }
	          else if ((value instanceof Enumeration))
	          {
	            result = ((Enumeration)value).hasMoreElements();
	          }
	          else if ((value instanceof Collection))
	          {
	            result = !((Collection)value).isEmpty();
	          }
	          else if ((value instanceof Object[]))
	          {
	            result = ((Object[])value).length > 0;
	          }
	        }
			
			if (result)
			{
				fctx.setNextRelation(trueExit);
			} else {				
				fctx.setNextRelation(falseExit);
			}
			break;	
		default:
			throw new FlowExecutionException("Decision node: Wrong configuration");
		}
		return NEXT;
	}

	private Object getComparisonValue(LogicContext fctx)
	{
		Object compValue = null;
		switch (compTypes) {
		case context:
			compValue = fctx.get(comparisonKey);
			break;
		default:
			compValue = comparisonKey;
			break;
		}
		return compValue;
	}
	
	
	public void load(Connected entity) throws FlowInitializationException
	{
		super.load(entity);
		
		String sOperator = getNotEmptyProperty(SWFParametersConstants.DECISION_NODE_OPERATOR);
		
		if (sOperator != null)
		{
			operator = DecisionOperators.valueOf(sOperator);
		}
		
		String sCompType = getNotEmptyProperty(SWFParametersConstants.DECISION_NODE_COMP_TYPE);
		
		if (sCompType != null)
		{
			compTypes = DecisionCompTypes.valueOf(sCompType);
		} else {
			compTypes = DecisionCompTypes.context;
		}
		
		decisionKey = getNotEmptyProperty(SWFParametersConstants.DECISION_NODE_DECISION_KEY);
		
		comparisonKey = getNotEmptyProperty(SWFParametersConstants.DECISION_NODE_COMP_KEY);
		

		 List<String> nextRelations = getOutgoingRelationsUUID(NEXT_EXIT_RELATION);
		 if (nextRelations != null && nextRelations.size() >= 1)
		 {
			 trueExit = nextRelations.get(0);
		 }
		 
		 List<String> falseRelations = getOutgoingRelationsUUID(FALSE_EXIT_RELATION);
		 if (falseRelations != null && falseRelations.size() >= 1)
		 {
		
			 falseExit = falseRelations.get(0);
		 }
		if (operator == null) {
			throw new FlowInitializationException(
					"Decision node: Opearator not defined");
		}
		if (compTypes == null) {
			throw new FlowInitializationException(
					"Decision node: CompTypes not defined");
		}

	}

	@Override
	public void validate(LogicContext ctx) throws FlowExecutionException {
		super.validate(ctx);
		

		
		if (trueExit == null || falseExit == null)
		{
			throw new FlowExecutionException("Decision node: Connector not defined.");
		}
		
		switch (operator) {
		case DEFINED:
		case UNDEFINED:
		case HAS_EL:
		case EMPTY_STR:

			if (decisionKey == null)
			{
				throw new FlowExecutionException("Decision node: decisionKey is not defined");
			}	
			break;	
			
		case EQ_STR:
		case EQ:
		case LESS:	
		case GREATER:	
		case NEQ:
		case NEQ_STR:
			if (decisionKey == null || comparisonKey == null)
			{
				throw new FlowExecutionException("Decision node: decisionKey and comparisonKey are mandatory");
			} 
			break;			
		default:
			
		}
	}	
	
	

}
