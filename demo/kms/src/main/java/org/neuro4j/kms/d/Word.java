package org.neuro4j.kms.d;

import org.neuro4j.core.Connected;

public class Word extends Connected {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Word(String word, String lang) {
		super(word);
		setProperty("language", lang);
	}

}
