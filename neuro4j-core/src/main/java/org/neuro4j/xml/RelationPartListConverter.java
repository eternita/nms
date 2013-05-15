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
import org.neuro4j.core.Entity;
//import org.neuro4j.storage.rdbms.mysql.EntityManager;
import org.neuro4j.xml.internal.EntityXML;
import org.neuro4j.xml.internal.RelationPartListXML;


public class RelationPartListConverter {
	
	public static String rp2xml(Map<String, ERBase> rpset)
	{
		if (null == rpset)
			return null;

		RelationPartListXML rpl = new RelationPartListXML();
		for (String eid : rpset.keySet())
		{

			// TODO for debug only
			if (null != rpset.get(eid))
			{
				Entity e= (Entity) rpset.get(eid);
				rpl.getRelationParts().add(new EntityXML(eid, e.getName()));
			} else {
				rpl.getRelationParts().add(new EntityXML(eid));			
			}

//			rpl.getRelationParts().add(new EntityXML(rp));
		}
				
		StringWriter writer = new StringWriter();
		try {
			JAXBContext ctx = JAXBContext.newInstance(RelationPartListXML.class);
			
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(rpl, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
		return writer.toString();
	}

	/**
	 * 
	 * @param caStr
	 * @return
	 */
	public static Map<String, ERBase> xml2rp(String caStr)
	{
		if (null == caStr)
			return new HashMap<String, ERBase>();

		RelationPartListXML cal = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(RelationPartListXML.class);
			
			Unmarshaller um = ctx.createUnmarshaller();
			cal = (RelationPartListXML) um.unmarshal(new StringReader(caStr));
			if (null == cal)
				return null;

			Map<String, ERBase> rpset = new HashMap<String, ERBase>();
			for (EntityXML ca : cal.getRelationParts())
			{
//				RelationPartType rpt = RelationPartType.valueOf(ca.direction);
/*				Entity e = eMgr.getById(ca.getUuid(), true);
				if (null == e)
					continue;

				// remove stubs ??
				for(Relation rStub : e.getRelations())
					e.removeRelation(rStub.getUuid());
*/				
//				RelationPart rp = new RelationPart(rpt, e);
//				rpset.put(e.getUuid(), e);
				rpset.put(ca.getUuid(), null);
			}

			return rpset;
		} catch (JAXBException e) {
			e.printStackTrace();
			return new HashMap<String, ERBase>();
		}		
	}

	

}
