package org.neuro4j.logic.swf;

import org.neuro4j.logic.LogicProcessorException;


public class FlowExecutionException extends LogicProcessorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlowExecutionException(String msg) {
		super(msg);
	}

	public FlowExecutionException(String string, Exception e1) {
		super(string, e1);
	}

	public FlowExecutionException(Throwable cause) {
		super(cause);
	}
}
