package org.neuro4j.logic;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;

public interface ExecutableEntity {

	public void validate(LogicContext ctx) throws FlowExecutionException;
	public void load(Entity entity) throws FlowInitializationException;
	public int execute(LogicContext ctx) throws FlowExecutionException;
	public void process(LogicContext ctx) throws FlowExecutionException;
	public boolean isLoaded();

}

