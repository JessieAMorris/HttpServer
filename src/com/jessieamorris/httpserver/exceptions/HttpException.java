package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie on 15-02-05.
 */
public class HttpException extends Exception {
	protected int httpStatusCode = 500;
	protected String httpStatusMessage = "Internal Server Error";

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getHttpStatusMessage() {
		return httpStatusMessage;
	}
}
