package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="network")
public class NetworkXML {

	@XmlJavaTypeAdapter(EntityListXMLAdapter.class)
	@XmlElement(name="entities")
	List<org.neuro4j.xml.internal.EntityXML> entities = new ArrayList<org.neuro4j.xml.internal.EntityXML>();
	
	public NetworkXML()
	{
		
	}
	
	public NetworkXML(org.neuro4j.core.Network n)
	{
		for (String eid : n.getIds())
		{
			org.neuro4j.core.Connected e = n.getById(eid);
			entities.add(new org.neuro4j.xml.internal.EntityXML(e));
		}
		
	}

	public List<org.neuro4j.xml.internal.EntityXML> getEntities() {
		return entities;
	}

}
