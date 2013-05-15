package org.neuro4j.logic.def;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.neuro4j.logic.ExecutableEntityFactory;
import org.neuro4j.logic.ExecutableEntityNotFoundException;

public class LogicBlockLoader {
	
	private static LogicBlockLoader INSTANCE = new LogicBlockLoader();
	
	private ConcurrentMap<String, LogicBlock> logicBlocks = null;
	
	private LogicBlockLoader() {
		super();
		logicBlocks = new ConcurrentHashMap<String, LogicBlock>();
		
	}
	
	public static LogicBlockLoader getInstance(){
		return INSTANCE;
	}
	
	public  LogicBlock  lookupBlock(String uuid, String className) throws ExecutableEntityNotFoundException
	{
		LogicBlock  block = logicBlocks.get(uuid);
		if (block == null)
		{
			block = (LogicBlock) ExecutableEntityFactory.getActionEntity(className);
			if (null != block)
			{
				logicBlocks.put(uuid, block);
			} else {
				throw new ExecutableEntityNotFoundException("Entity not found. ClassName: " + className + " uuid: " + uuid);
			}
		}
		return block;
	}
	
	public void removeLogicBlock(String uuid)
	{
		logicBlocks.remove(uuid);
	}

	public void clearBlocks()
	{
		logicBlocks.clear();
	}
}
