package org.neuro4j.kms;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neuro4j.NeuroManager;
import org.neuro4j.storage.Storage;

public class Config {

	private static final String STORAGE_HOME_DIR = System.getenv().get("NMS_HOME") + "/kms-solr";
	
	public static final Storage storage = NeuroManager.newInstance().getStorage(STORAGE_HOME_DIR, "storage.properties");
	
	public static final String EN = "en";
	
	public static final String RU = "ru";
	
	public static final String ES = "ES";
	
	public static final String DE = "de";
	
	public static final Map<String,String> languages = new LinkedHashMap<String, String>();
	
	static {
		languages.put(EN, "English");
		languages.put(RU, "Русский");
		languages.put(ES, "Español");
		languages.put(DE, "Deutsch");
	}

}
