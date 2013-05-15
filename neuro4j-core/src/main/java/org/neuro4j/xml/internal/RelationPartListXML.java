package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name="relation_part_list")
public class RelationPartListXML {
	@XmlJavaTypeAdapter(EntityListXMLAdapter.class)
	@XmlElement(name="relation_parts")
	List<org.neuro4j.xml.internal.EntityXML> relationParts = new ArrayList<org.neuro4j.xml.internal.EntityXML>();

	public List<org.neuro4j.xml.internal.EntityXML> getRelationParts() {
		return relationParts;
	}
	
	
	
}
