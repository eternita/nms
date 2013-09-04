package org.neuro4j.kms.d;

import org.neuro4j.core.ERBase;

public class Word extends ERBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Word(String word, String lang) {
		super(word);
		setProperty("language", lang);
	}

}
