package org.neuro4j.storage;

import org.neuro4j.core.Constants;

public class StorageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;
	public StorageException() {
	}

	public StorageException(String arg0) {
		super(arg0);
	}

	public StorageException(Throwable arg0) {
		super(arg0);
	}

	public StorageException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
