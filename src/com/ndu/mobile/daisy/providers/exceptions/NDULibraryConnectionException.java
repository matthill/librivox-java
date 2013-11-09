package com.ndu.mobile.daisy.providers.exceptions;

public class NDULibraryConnectionException extends NDULibraryException {

	private static final long serialVersionUID = 4997591756799425524L;
	
	public NDULibraryConnectionException(String message, Throwable exception)
	{
		super(message, exception);
	}
	public NDULibraryConnectionException(String message) {
		super(message);

	}


}
