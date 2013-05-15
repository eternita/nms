package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;


class RelationTailListXMLAdapter extends XmlAdapter<RelationTailXML[],List<RelationTailXML>> {
	  
	public List<RelationTailXML> unmarshal( RelationTailXML[] value ) {
	    List<RelationTailXML> r = new ArrayList<RelationTailXML>();
	    for( RelationTailXML c : value )
	      r.add(c);
	    return r;
	}
	
	@Override
	public RelationTailXML[] marshal(List<RelationTailXML> value)
			throws Exception {
	    return value.toArray(new RelationTailXML[value.size()]);
	}
}