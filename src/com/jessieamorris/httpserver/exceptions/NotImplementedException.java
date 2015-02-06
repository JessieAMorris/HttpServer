package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie on 15-02-05.
 */
public class NotImplementedException extends HttpException {
	public NotImplementedException() {
		httpStatusCode = 501;
		httpStatusMessage = "Not Implemented";
	}
}
