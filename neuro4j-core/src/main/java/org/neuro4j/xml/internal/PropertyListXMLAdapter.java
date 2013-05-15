package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class PropertyListXMLAdapter extends XmlAdapter<PropertyXML[],List<PropertyXML>> {
	  
	public List<PropertyXML> unmarshal( PropertyXML[] array ) {
	    List<PropertyXML> propList = new ArrayList<PropertyXML>();
	    for( PropertyXML p : array)
	      propList.add(p);
	    return propList;
	}
	
	@Override
	public PropertyXML[] marshal(List<PropertyXML> value)
			throws Exception {
	    return value.toArray(new PropertyXML[value.size()]);
	}
}