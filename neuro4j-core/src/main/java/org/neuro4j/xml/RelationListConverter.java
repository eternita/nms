package org.neuro4j.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.neuro4j.core.ERBase;
import org.neuro4j.xml.internal.RelationListXML;


public class RelationListConverter {
	
	public static String relations2xml(Map<String, ERBase> rmap)
	{
		if (null == rmap)
			return null;

		RelationListXML relListRoot = new RelationListXML();
		for (String rid: rmap.keySet())
		{
			// TODO for debug only
			if (null != rmap.get(rid))
			{
				org.neuro4j.core.Relation r = (org.neuro4j.core.Relation) rmap.get(rid);
				relListRoot.relations.add(new org.neuro4j.xml.internal.RelationXML(r, true));
			} else {
				relListRoot.relations.add(new org.neuro4j.xml.internal.RelationXML(rid));
			}
//			relListRoot.relations.add(new org.neuro4j.xml.internal.RelationXML(rid));
		}
				
		StringWriter writer = new StringWriter();
		try {
			JAXBContext ctx = JAXBContext.newInstance(RelationListXML.class);
			
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(relListRoot, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
		return writer.toString();
	}

	/**
	 * 
	 * @param relationsStr
	 * @return
	 */
	public static Map<String, ERBase> xml2relations(String relationsStr)
	{
		if (null == relationsStr)
			return new HashMap<String, ERBase>();

		RelationListXML relListRoot = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(RelationListXML.class);
			
			Unmarshaller um = ctx.createUnmarshaller();
			relListRoot = (RelationListXML) um.unmarshal(new StringReader(relationsStr));
			if (null == relListRoot)
				return null;

			Map<String, ERBase> relationMap = new HashMap<String, ERBase>();
			for (org.neuro4j.xml.internal.RelationXML r : relListRoot.relations)
			{
				org.neuro4j.core.Relation rel = rel2rel(r); 
				relationMap.put(rel.getUuid(), rel);
			}
			
			return relationMap;
		} catch (JAXBException e) {
			e.printStackTrace();
			return new HashMap<String, ERBase>();
		}		
	}


	/**
	 * !! return relation names and ids only  !! 
	 * 
	 * @param r
	 * @return
	 */
	static org.neuro4j.core.Relation rel2rel (org.neuro4j.xml.internal.RelationXML r)
	{
		org.neuro4j.core.Relation relation = new org.neuro4j.core.Relation(r.getName());
		relation.setUuid(r.getUuid());
//		relation.setNetworkId(net.getUuid());
//		relation.setLastModifiedDate(r.getLastModifiedDate());
		
//		for (org.neuro4j.xml.internal.RelationPart rp : r.getRelationParts())
//		{
//			org.neuro4j.core.rel.RelationPart relationPart = 
//					new org.neuro4j.core.rel.RelationPart(
//							RelationPartType.valueOf(rp.getType()), 
//							network.getEntityByUUID(rp.getEntityId()));
//			relation.addParticipant(relationPart);
//		}
		
		return relation;
	}
	

}
