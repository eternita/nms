package org.neuro4j.logic.def.node;

import java.util.Map;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.def.LogicBlockInfo;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;

public class CallBlock extends LogicBlock {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public CallBlock() {
		super();
	}

	public CallBlock(String name) {
		super();
		lba.setName(name);
		lba.setProperty(SWFConstants.N4J_CONSOLE_NODE_TYPE, "star"); 
	}

//	public void validate(LogicContext ctx) throws LogicException
//	{
//		checkContextParameter(ctx, SWFConstants.AC_NEURO_STORAGE);
//		checkContextParameter(ctx, SWFConstants.AC_LOGIC_PROCESSOR);
//		return;
//	}
//	

	public int execute(LogicContext ctx)
			throws FlowExecutionException {
		String callNodeId = (String)ctx.get("CALL_NODE_ID");
		
		NeuroStorage neuroStorage = (NeuroStorage) ctx.get(SWFConstants.AC_NEURO_STORAGE);
		Network network = (Network) ctx.get(SWFConstants.AC_FLOW_NETWORK);
		LogicProcessor logicProcessor = (LogicProcessor) ctx.get(SWFConstants.AC_LOGIC_PROCESSOR);
		
		Entity e = network.getEntityByUUID(callNodeId);

		if (null == e)
			try {
				e = neuroStorage.getEntityByUUID(callNodeId);
			} catch (StorageException e1) {
				throw new FlowExecutionException("Can't load from storage entity with id " + lba.getUuid(), e1);
			}
		
		
		try {
			logicProcessor.action(e, network, neuroStorage, ctx);
		} catch (LogicProcessorException e1) {
            throw new FlowExecutionException(e1.getCause());
		}
		return NEXT;
	}



	public synchronized void load(Entity entity) throws FlowInitializationException
	{
		super.load(entity);
		setLoaded(true);
	}
	
}
