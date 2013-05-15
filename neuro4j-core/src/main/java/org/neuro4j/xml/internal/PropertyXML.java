package org.neuro4j.xml.internal;

import javax.xml.bind.annotation.XmlAttribute;

public class PropertyXML {

	@XmlAttribute
	String key;
	
	@XmlAttribute
	String value;


	public PropertyXML() {
		super();
	}

	public PropertyXML(String key, String value) {
		super();
		this.key = key;
		this.value = value;
		
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	
}
