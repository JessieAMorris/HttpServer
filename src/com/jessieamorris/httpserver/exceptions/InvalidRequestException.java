package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie on 15-02-05.
 */
public class InvalidRequestException extends HttpException {
	public InvalidRequestException() {
		this.httpStatusCode = 400;
		this.httpStatusMessage = "Bad Request";
	}
}
