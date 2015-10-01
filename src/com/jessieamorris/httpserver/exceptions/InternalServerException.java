package com.jessieamorris.httpserver.exceptions;

/**
 * Created by jessie.
 */
public class InternalServerException extends HttpException {
	public InternalServerException(Exception e) {
		this.httpStatusCode = 500;
		this.httpStatusMessage = "Internal Server Error";

		this.httpBody = getStackTraceString(e);
	}

	public static String getStackTraceString(Throwable throwable) {
		String ret = "";
		StackTraceElement[] stack = throwable.getStackTrace();
		for (StackTraceElement s : stack) {
			ret = ret + s.toString() + "\n\t\t";
		}

		return ret;
	}
}
