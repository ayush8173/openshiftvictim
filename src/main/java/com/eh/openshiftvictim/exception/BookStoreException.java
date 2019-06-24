package com.eh.openshiftvictim.exception;

public class BookStoreException extends Exception {

	private static final long serialVersionUID = 1L;

	public BookStoreException() {
		super();
	}
	
	public BookStoreException(String errorMessage) {
		super(errorMessage);
	}
}
