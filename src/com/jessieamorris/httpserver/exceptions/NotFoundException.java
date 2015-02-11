package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie.
 */
public class NotFoundException extends HttpException {
	public NotFoundException() {
		this.httpStatusCode = 404;
		this.httpStatusMessage = "Not Found";
	}
}
