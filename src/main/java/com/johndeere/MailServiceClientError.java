package com.johndeere;

public class MailServiceClientError extends Exception {

	private static final long serialVersionUID = 1L;

	public String errorMsg;
	
	public MailServiceClientError(String errorMsg) {
		super(errorMsg);
	}
}
