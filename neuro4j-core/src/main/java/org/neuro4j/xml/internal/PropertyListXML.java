package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="property_list")
public class PropertyListXML {
	
	@XmlJavaTypeAdapter(PropertyListXMLAdapter.class)
	@XmlElement(name="properties")
	List<PropertyXML> reps = new ArrayList<PropertyXML>();

	public List<PropertyXML> getProperties() {
		return reps;
	}
	
	
}
