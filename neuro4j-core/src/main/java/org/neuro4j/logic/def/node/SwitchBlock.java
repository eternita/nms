package org.neuro4j.logic.def.node;

import java.util.List;

import org.neuro4j.core.ERBase;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;

public class SwitchBlock extends LogicBlock {

	private  String relationName = null;
	private  String defaultRelation = null;
	
	public void validate(LogicContext fctx) throws FlowExecutionException
	{
		if (relationName == null && defaultRelation == null)
		{
			throw new FlowExecutionException("Switch node has wrong configuration.");
		}


	}

	public int execute(LogicContext ctx)
			throws FlowExecutionException {
		
		 String nextStepUUID = null;
		 String relation = relationName;
		 
		 if (relationName != null && !relationName.startsWith(SWFConstants.QUOTES_SYMBOL))
		 {
			 relation = (String) ctx.get(relationName);
		 } else if(relationName != null) {
			 relation = relation.replace(SWFConstants.QUOTES_SYMBOL, "");
		 }
		 
		 if (null == relation)
			 relation = "null";
		 
		 List<String> nextRelations = getOutgoingRelationsUUID(relation);
		 if (nextRelations != null && nextRelations.size() == 1)
		 {
			 nextStepUUID = nextRelations.get(0);
		 }
		 
		 if (nextStepUUID == null && defaultRelation != null)
		 {
			 nextStepUUID = defaultRelation;
		 }
		 
		 if (nextStepUUID != null)
		 {
			 ctx.put(SWFConstants.AC_NEXT_NODE_UUID, nextStepUUID); 
			 
		 } else {
			 throw new FlowExecutionException("Switch: NextStep is unknown.");
		 }
	
		 return NEXT;
	}
	
	

	public void load(ERBase entity) throws FlowInitializationException
	{
		 super.load(entity);
		 relationName = getNotEmptyProperty(SWFParametersConstants.SWITCH_NODE_ACTION_NAME);
		 if(relationName == null)
		 {
			 relationName = SWFParametersConstants.SWITCH_NODE_DEFAULT_PARAMETER_VALUE;
		 }
		 List<String> nextRelations = getOutgoingRelationsUUID(SWFParametersConstants.SWITCH_NODE_DEFAULT_ACTION_NAME_2);
		 if (nextRelations != null && nextRelations.size() > 0)
		 {
			 defaultRelation = nextRelations.get(0);
		 } else {

			 nextRelations = getOutgoingRelationsUUID(SWFParametersConstants.SWITCH_NODE_DEFAULT_ACTION_NAME);
			 if (nextRelations != null && nextRelations.size() == 1)
			 {
				 defaultRelation = nextRelations.get(0);
			 } else {
				 nextRelations = getOutgoingRelationsUUID(SWFParametersConstants.SWITCH_NODE_DEFAULT_ACTION); 
				 if (nextRelations != null && nextRelations.size() == 1){
					 defaultRelation = nextRelations.get(0);
				 }
			 }
			 
		 }
		 return;
	}
	
	

}
