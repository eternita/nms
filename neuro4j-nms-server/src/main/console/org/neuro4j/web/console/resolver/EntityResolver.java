package org.neuro4j.web.console.resolver;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.vlh.EntryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityResolver implements EntryResolver {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Storage storage = null;
	private Network network = null;
	private int connectedCountLimit = Integer.MAX_VALUE;
	
	public EntityResolver(Storage storage, Network network)
	{
		this.storage = storage;
		this.network = network;
	}
	
	public EntityResolver(Storage storage, Network network, int connectedCountLimit)
	{
		this.storage = storage;
		this.network = network;
		this.connectedCountLimit = connectedCountLimit;
	}
	
	public Object resolve(String id, String language) {
		
		try {
			Entity e = network.getEntityByUUID(id);
			if (null == e)
			{
				e = storage.getEntityByUUID(id);
				network.add(e);
			}
			NetworkUtils.loadConnected(e, network, storage, connectedCountLimit);
			return e;
		} catch (StorageException e) {
			logger.error("Can't resolve entity " + id, e);
		}
		
		return null;
	}

}
