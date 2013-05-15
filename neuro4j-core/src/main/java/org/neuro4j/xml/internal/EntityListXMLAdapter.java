package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class EntityListXMLAdapter extends XmlAdapter<EntityXML[],List<EntityXML>> {
	  
	public List<EntityXML> unmarshal( EntityXML[] value ) {
	    List<EntityXML> r = new ArrayList<EntityXML>();
	    for( EntityXML c : value )
	      r.add(c);
	    return r;
	}
	
	@Override
	public EntityXML[] marshal(List<EntityXML> value)
			throws Exception {
	    return value.toArray(new EntityXML[value.size()]);
	}
}