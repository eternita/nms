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
	
	@XmlJavaTypeAdapter(RelationListXMLAdapter.class)
	@XmlElement(name="relations")
	List<org.neuro4j.xml.internal.RelationXML> relations = new ArrayList<org.neuro4j.xml.internal.RelationXML>();
	
	public NetworkXML()
	{
		
	}
	
	public NetworkXML(org.neuro4j.core.Network n)
	{
		for (String eid : n.getEntities())
		{
			org.neuro4j.core.Entity e = n.getEntityByUUID(eid);
			entities.add(new org.neuro4j.xml.internal.EntityXML(e));
		}
		for (String rid : n.getRelations())
		{
			org.neuro4j.core.Relation e = n.getRelationByUUID(rid);
			relations.add(new org.neuro4j.xml.internal.RelationXML(e));
		}
		
	}

	public List<org.neuro4j.xml.internal.EntityXML> getEntities() {
		return entities;
	}

	public List<org.neuro4j.xml.internal.RelationXML> getRelations() {
		return relations;
	}
		
}
