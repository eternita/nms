package org.neuro4j.web.console;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class Locales {

	public final static String EN = "en";

	public final static String DEFAULT_LANGUAGE = EN;
	
	private static Locale DEFAULT_LOCALE = new Locale(DEFAULT_LANGUAGE);

	
	/**
	 * Get locale
	 *  - from request
	 *  - if no -> from session
	 *  - if no -> set default
	 *  
	 *  and store it to session
	 * 
	 * @param request
	 * @return
	 */
	public static Locale getSupportedLocale (HttpServletRequest request) {

		return DEFAULT_LOCALE;
	}

		
}
