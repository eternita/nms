package org.neuro4j.xml.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.neuro4j.core.Entity;

public class EntityXML {

	@XmlAttribute
	String uuid;
	
	@XmlAttribute
	String name;
	
	@XmlAttribute
	Date lastModifiedDate;

	@XmlJavaTypeAdapter(PropertyListXMLAdapter.class)
	@XmlElement(name="properties")
	List<PropertyXML> properties = new ArrayList<PropertyXML>();
	
	@XmlJavaTypeAdapter(RelationTailListXMLAdapter.class)
	@XmlElement(name="relations")
	List<RelationTailXML> relations = new ArrayList<RelationTailXML>();
	
	public EntityXML() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EntityXML(String uuid) {
		super();
		this.uuid = uuid;
	}

	public EntityXML(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
	}

	public EntityXML(Entity entity) {
		super();
		this.uuid = entity.getUuid();
		this.name = entity.getName();
		this.lastModifiedDate = entity.getLastModifiedDate();
		for (String key : entity.getPropertyKeysWithRepresentations())
			properties.add(new PropertyXML(key, entity.getProperty(key)));
		
		for (String rid : entity.getRelationsKeys())
			relations.add(new RelationTailXML(rid));
		
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

	public List<PropertyXML> getRepresentations() {
		return properties;
	}
	
	public List<RelationTailXML> getRelations() {
		return relations;
	}
	
}
