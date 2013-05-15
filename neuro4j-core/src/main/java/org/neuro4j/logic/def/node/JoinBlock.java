package org.neuro4j.logic.def.node;

import java.util.List;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;

public class JoinBlock extends LogicBlock {
	
    private String next = null;
    

	public int execute(LogicContext ctx)
			throws FlowExecutionException {
		
		ctx.setNextRelation(next);
		return NEXT;
	}

	public synchronized void load(Entity entity) throws FlowInitializationException {
		super.load(entity);

		List<String> nextRelations = getOutgoingRelationsUUID(SWFConstants.NEXT_RELATION_NAME);
		if (nextRelations != null && nextRelations.size() == 1) {
			next = nextRelations.get(0);
		}

		setLoaded(true);
	}
	
	@Override
	public void validate(LogicContext ctx) throws FlowExecutionException {
		super.validate(ctx);
		
		if (next == null)
		{
			throw new FlowExecutionException("JoinBlock: Wrong configuration");
		}

	}	
	
}
