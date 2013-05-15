package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.neuro4j.core.Relation;

public class RelationXML {

	@XmlAttribute
	String uuid;
	
	@XmlAttribute
	String name;
	
	@XmlAttribute
	Date lastModifiedDate;
	
	@XmlJavaTypeAdapter(PropertyListXMLAdapter.class)
	@XmlElement(name="properties")
	List<PropertyXML> properties = new ArrayList<PropertyXML>();	

	@XmlJavaTypeAdapter(EntityTailListXMLAdapter.class)
	@XmlElement(name="relation_parts")
	List<org.neuro4j.xml.internal.EntityTailXML> relationParts = new ArrayList<org.neuro4j.xml.internal.EntityTailXML>();

	public RelationXML() {
		super();
	}

	public RelationXML(String ruuid) {
		super();
		this.uuid = ruuid;
	}

	public RelationXML(Relation relation) {
		this(relation, false);
	}

	public RelationXML(Relation relation, boolean relPartsOnly) {
		super();
		this.uuid = relation.getUuid();
		this.name = relation.getName();
		if (!relPartsOnly)
		{
			for (String key : relation.getPropertyKeysWithRepresentations())
				properties.add(new PropertyXML(key, relation.getProperty(key)));
		}
		
		this.lastModifiedDate = relation.getLastModifiedDate();
		for (String rp : relation.getParticipantsKeys())
		{
			relationParts.add(new EntityTailXML(rp));
		}
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public List<EntityTailXML> getRelationParts() {
		return relationParts;
	}
	
	public List<PropertyXML> getRepresentations() {
		return properties;
	}
		
	
}
