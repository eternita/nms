package org.neuro4j.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.neuro4j.xml.internal.NetworkXML;
import org.neuro4j.xml.internal.PropertyXML;


public class NetworkConverter {
	
	public static String network2xml(org.neuro4j.core.Network network)
	{
		if (null == network)
			return null;

		org.neuro4j.xml.internal.NetworkXML net = new org.neuro4j.xml.internal.NetworkXML(network);
				
		StringWriter writer = new StringWriter();
		try {
			JAXBContext ctx = JAXBContext.newInstance(NetworkXML.class);
			
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(net, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
		return writer.toString();
	}

	public static void network2xmlstream(org.neuro4j.core.Network network, OutputStream out)
	{
		if (null == network)
			return;

		org.neuro4j.xml.internal.NetworkXML net = new org.neuro4j.xml.internal.NetworkXML(network);
				
		try {
			JAXBContext ctx = JAXBContext.newInstance(NetworkXML.class);
			
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(net, out);
		} catch (JAXBException e) {
			e.printStackTrace();
			return;
		}		
		return;
	}	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static org.neuro4j.core.Network xml2network(String xml)
	{
		if (null == xml)
			return null; //new HashSet<Representation>();

		org.neuro4j.xml.internal.NetworkXML net = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(org.neuro4j.xml.internal.NetworkXML.class);
			
			Unmarshaller um = ctx.createUnmarshaller();
			net = (org.neuro4j.xml.internal.NetworkXML) um.unmarshal(new StringReader(xml));
			if (null == net)
				return null;		
			
			return netXML2net(net);

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
	}

	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static org.neuro4j.core.Network xml2network(InputStream xml)
	{
		if (null == xml)
			return null; //new HashSet<Representation>();

		org.neuro4j.xml.internal.NetworkXML net = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(org.neuro4j.xml.internal.NetworkXML.class);
			
			Unmarshaller um = ctx.createUnmarshaller();
			net = (org.neuro4j.xml.internal.NetworkXML) um.unmarshal(xml);
			if (null == net)
				return null;

			return netXML2net(net);
			
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	private static org.neuro4j.core.Network netXML2net(org.neuro4j.xml.internal.NetworkXML net)
	{
		org.neuro4j.core.Network network = new org.neuro4j.core.Network();

		for (org.neuro4j.xml.internal.EntityXML e : net.getEntities())
		{
			org.neuro4j.core.Entity entity = new org.neuro4j.core.Entity(e.getName());
			entity.setUuid(e.getUuid());
			for (PropertyXML rep : e.getRepresentations())
				entity.setProperty(rep.getKey(), rep.getValue());
			
			for (org.neuro4j.xml.internal.RelationTailXML rp : e.getRelations())
			{
//				org.neuro4j.core.Relation relation = network.getEntityByUUID(rp.getUuid());
				entity.addRelation(rp.getUuid());
			}			
			network.add(entity);
		}
		
		for (org.neuro4j.xml.internal.RelationXML r : net.getRelations())
		{
			org.neuro4j.core.Relation relation = new org.neuro4j.core.Relation(r.getName());
			relation.setUuid(r.getUuid());
			
			for (PropertyXML rep : r.getRepresentations())
				relation.setProperty(rep.getKey(), rep.getValue());


			for (org.neuro4j.xml.internal.EntityTailXML rp : r.getRelationParts())
			{
				// try to resolve entity (if it has been loaded)
				org.neuro4j.core.Entity relationPart = network.getEntityByUUID(rp.getUuid());
				if (null != relationPart)
					relation.addParticipant(relationPart);
				else
					relation.addParticipant(rp.getUuid());
			}
			
			network.add(relation);
		}
		
		
		return network;			
	}
	
}
