package org.neuro4j.storage.solr;


/**
 * 
 * fields configuration are stored in conf/schema.xml
 *
 */
public class SearchIndexConfiguration {

	public static final String FIELD_UUID = "UUID";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ER_TYPE = "er_type";

	public static final String ER_TYPE_ENTITY = "entity";
	public static final String ER_TYPE_RELATION = "relation";
	
	public static final String PROPERTY_PREFIX = "pr_";
	public static final String RELATIONS = "relations";
	public static final String ENTITIES = "entities";

	
	private SearchIndexConfiguration() {
		// TODO Auto-generated constructor stub
	}
	

}
