package org.neuro4j.logic.def.node;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;

public class EndBlock extends LogicBlock {


	public EndBlock() {
		super();
	}

	public EndBlock(String name) {
		super();
		lba.setName(name);
		lba.setProperty(SWFConstants.N4J_CONSOLE_NODE_TYPE, "square"); 
	}


	public int execute(LogicContext ctx)
			throws FlowExecutionException {
		return NEXT;
	}


	public synchronized void load(Entity entity) throws FlowInitializationException
	{
		super.load(entity);
		setLoaded(true);
	}
	
}
