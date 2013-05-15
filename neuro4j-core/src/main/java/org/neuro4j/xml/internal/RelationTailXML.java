package org.neuro4j.xml.internal;

import javax.xml.bind.annotation.XmlAttribute;

public class RelationTailXML {

	@XmlAttribute
	String uuid;
	
	public RelationTailXML() {
		super();
	}

	public RelationTailXML(String ruuid) {
		super();
		this.uuid = ruuid;
	}		
	
	public String getUuid() {
		return uuid;
	}

}
