package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class EntityTailListXMLAdapter extends XmlAdapter<EntityTailXML[],List<EntityTailXML>> {
	  
	public List<EntityTailXML> unmarshal( EntityTailXML[] value ) {
	    List<EntityTailXML> r = new ArrayList<EntityTailXML>();
	    for( EntityTailXML c : value )
	      r.add(c);
	    return r;
	}
	
	@Override
	public EntityTailXML[] marshal(List<EntityTailXML> value)
			throws Exception {
	    return value.toArray(new EntityTailXML[value.size()]);
	}
}