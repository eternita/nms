package org.neuro4j.logic;

import java.util.Properties;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.logic.swf.SimpleWorkflowException;
import org.neuro4j.storage.Storage;

public interface LogicProcessor {

	public void init(Properties properties) throws LogicProcessorException;

	/**
	 * Network usually local - much faster but may not have all data
	 * NeuroStorage has all data but slower
	 * 
	 * @param start
	 * @param network
	 * @param storage
	 * @param logicContext
	 * @return
	 * @throws SimpleWorkflowException 
	 */
	public LogicContext action(ERBase start, Network network, Storage storage, LogicContext logicContext) throws LogicProcessorException;
	
}
