package org.neuro4j.core.rep;

import org.neuro4j.core.Representation;

public interface RepresentationProxy {

	public void init() throws RepresentationProxyException;
	
	public void put(Object data, Representation representation) throws RepresentationProxyException;
	public Object get(Representation representation) throws RepresentationProxyException;
	
}
