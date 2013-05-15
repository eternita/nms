package org.neuro4j.xml.internal;

import javax.xml.bind.annotation.XmlAttribute;

public class EntityTailXML {

	@XmlAttribute
	String uuid;
	
	public EntityTailXML() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EntityTailXML(String uuid) {
		super();
		this.uuid = uuid;
	}


	public String getUuid() {
		return uuid;
	}

}
