package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.InvalidRequestException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jessie on 15-02-05.
 */
public class HttpRequest {
	public enum Method {
		OPTIONS("OPTIONS"),
		GET("GET"),
		HEAD("HEAD"),
		POST("POST"),
		PUT("PUT"),
		DELETE("DELETE"),
		TRACE("TRACE"),
		CONNECT("CONNECT");

		private String method;

		Method(String method) {
			this.method = method;
		}

		public String getMethod() {
			return method;
		}
	}

	private Method method;
	private URI uri;
	private String version;
	private List<Header> headers = new ArrayList<Header>();

	public HttpRequest() {}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void validateRequestLine() throws InvalidRequestException {
		if(!version.equalsIgnoreCase("HTTP/1.1")) {
			throw new InvalidRequestException();
		}
	}

	public void addHeader(Header header) {
		headers.add(header);
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void parseRequest(BufferedReader in) throws IOException, NotImplementedException, InvalidRequestException {
		System.out.println("Got some input");

		String requestLine = in.readLine();
		System.out.println("Input line was: " + requestLine);

		String[] splitInput = requestLine.split("\\s+");

		System.out.println("Split input was: " + Arrays.toString(splitInput));

		if (splitInput.length != 3) {
			throw new InvalidRequestException();
		}

		if (splitInput[0].equals("GET")) {
			setMethod(HttpRequest.Method.GET);
			setURI(URI.create(splitInput[1]));
			setVersion(splitInput[2]);
		}

		validateRequestLine();

		System.out.println("Method: " + getMethod());

		switch(getMethod()) {
			case GET:
				parseHeaders(in);
				break;

			default:
				throw new NotImplementedException();
		}

		System.out.println("Headers done");
	}

	private void parseHeaders(BufferedReader in) throws IOException {
		String headerLine;

		System.out.println("Parsing headers!");

		while((headerLine = in.readLine()) != null && (!headerLine.equals("\r\n") && !headerLine.equals(""))) {
			System.out.println("Got a header: " + headerLine);

			String[] headerSplit = headerLine.split("\\s+");

			if(headerSplit.length == 2) {
				String headerName = headerSplit[0];
				String headerValue = headerSplit[1];

				if(headerName.charAt(headerName.length() - 1) == ':') {
					headerName = headerName.substring(0, headerName.length() - 1);
				}

				addHeader(new Header(headerName, headerValue));
			}
		}
	}
}
