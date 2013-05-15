package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;


class RelationListXMLAdapter extends XmlAdapter<RelationXML[],List<RelationXML>> {
	  
	public List<RelationXML> unmarshal( RelationXML[] value ) {
	    List<RelationXML> r = new ArrayList<RelationXML>();
	    for( RelationXML c : value )
	      r.add(c);
	    return r;
	}
	
	@Override
	public RelationXML[] marshal(List<RelationXML> value)
			throws Exception {
	    return value.toArray(new RelationXML[value.size()]);
	}
}