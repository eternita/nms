package org.neuro4j.logic.def;

import org.neuro4j.core.Entity;

/**
 * 
 * Implements persistence for CustomBlock inside Network
 *
 */
public class LogicBlockAdapter extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void init(Entity entity) 
	{
	
		setUuid(entity.getUuid());
		setName(entity.getName());
		setLastModifiedDate(entity.getLastModifiedDate());
		setVirtual(entity.isVirtual());
		
		for (String key : entity.getPropertyKeys())
			setProperty(key, entity.getProperty(key));
		
		for(org.neuro4j.core.Relation r: entity.getRelations()){
			addRelation(r);
		}
	
	}

	public LogicBlockAdapter() {
		super();
	}

}
