package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.neuro4j.xml.internal.RelationListXMLAdapter;


@XmlRootElement(name="relation_list")
public class RelationListXML {
	
	@XmlJavaTypeAdapter(RelationListXMLAdapter.class)
	@XmlElement(name="relations")
	public List<org.neuro4j.xml.internal.RelationXML> relations = new ArrayList<org.neuro4j.xml.internal.RelationXML>();
}
