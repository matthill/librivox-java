package com.ndu.mobile.daisy.providers.exceptions;

public class NDULibraryParsingException extends NDULibraryException {

	private static final long serialVersionUID = -8152700849687327540L;

	public NDULibraryParsingException(String message, Throwable exception)
	{
		super(message, exception);
	}
	
	public NDULibraryParsingException(String message) {
		super(message);
	}


}
