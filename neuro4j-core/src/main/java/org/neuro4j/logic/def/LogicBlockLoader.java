package org.neuro4j.logic.def;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.ExecutableEntityFactory;
import org.neuro4j.logic.ExecutableEntityNotFoundException;
import org.neuro4j.logic.swf.FlowInitializationException;

public class LogicBlockLoader {
	
	private static LogicBlockLoader INSTANCE = new LogicBlockLoader();
	
	private LogicBlockLoader() {
		super();
	}
	
	public static LogicBlockLoader getInstance(){
		return INSTANCE;
	}
	
	public  LogicBlock  lookupBlock(Entity entity, String className) throws ExecutableEntityNotFoundException, FlowInitializationException
	{
		LogicBlock  block = (LogicBlock) ExecutableEntityFactory.getActionEntity(className); 
		
		block.load(entity);

		return block;
	}
}
