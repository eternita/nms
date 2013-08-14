package org.neuro4j.web.console.resolver;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.vlh.EntryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationResolver implements EntryResolver {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private Storage storage = null;
	private Network network = null;
	private int connectedCountLimit = Integer.MAX_VALUE;

	public RelationResolver(Storage storage, Network network)
	{
		this.storage = storage;
		this.network = network;
	}
	
	public RelationResolver(Storage storage, Network network, int connectedCountLimit)
	{
		this.storage = storage;
		this.network = network;
		this.connectedCountLimit = connectedCountLimit;
	}
	
	public Object resolve(String id, String language) {
		
		try {
			Relation r = network.getRelationByUUID(id);
			if (null == r)
			{
				r = storage.getRelationByUUID(id);
				network.add(r);
			}
			NetworkUtils.loadConnected(r, network, storage, connectedCountLimit);
			
			return r;
		} catch (StorageException e) {
			logger.error("Can't resolve relation " + id, e);
		}
		
		return null;
	}

}
