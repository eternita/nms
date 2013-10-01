package org.neuro4j.storage;

import org.neuro4j.core.Constants;

public class StorageNotFoundException extends StorageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	public StorageNotFoundException(String string) {
		super(string);
	}

}
