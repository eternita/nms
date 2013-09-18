package org.neuro4j.kms.d;

import org.neuro4j.core.Connected;

public class ExternalExperience extends Connected {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExternalExperience() {
		super("external-experience");
		setProperty("external-experience", "true");
	}

}
