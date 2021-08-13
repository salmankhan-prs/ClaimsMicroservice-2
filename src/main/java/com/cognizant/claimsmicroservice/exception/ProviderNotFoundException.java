package com.cognizant.claimsmicroservice.exception;

public class ProviderNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProviderNotFoundException(String msg) {

		super(msg);
	}
	public ProviderNotFoundException() {

		super();
	}

}
