package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie.
 */
public class InternalServerException extends HttpException {
	public InternalServerException(Exception e) {
		this.httpStatusCode = 500;
		this.httpStatusMessage = "Internal Server Error";

		this.httpBody = e.toString();
	}
}
