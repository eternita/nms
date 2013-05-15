package org.neuro4j.logic;

@SuppressWarnings("serial")
public class LogicException extends Exception {

	public LogicException() {
		super();
	}

	public LogicException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public LogicException(String msg) {
		super(msg);
	}

	public LogicException(Throwable thr) {
		super(thr);
	}

}
