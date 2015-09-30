package com.jessieamorris.httpserver.server;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.InvalidRequestException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;
import com.jessieamorris.httpserver.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
		CONNECT("CONNECT"),
		PATCH("PATCH");

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
	private Map<String, Header> headers = new HashMap<String, Header>();

	public HttpRequest() {}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	private void setURI(URI uri) {
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
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
		headers.put(header.getName(), header);
	}

	public Collection<Header> getHeadersCollection() {
		return headers.values();
	}

	public Map<String, Header> getHeadersMap() {
		return headers;
	}

	public void parseRequest(BufferedReader in) throws IOException, NotImplementedException, InvalidRequestException {
		Logger.println("Got some input");

		String requestLine = in.readLine();
		Logger.println("Input line was: " + requestLine);

		if(requestLine == null) {
			return;
		}

		String[] splitInput = requestLine.split("\\s+");

		Logger.println("Split input was: " + Arrays.toString(splitInput));

		if (splitInput.length != 3) {
			throw new InvalidRequestException();
		}

		if (splitInput[0].equals("GET")) {
			setMethod(HttpRequest.Method.GET);
			setURI(URI.create(splitInput[1]));
			setVersion(splitInput[2]);
		}

		validateRequestLine();

		Logger.println("Method: " + getMethod());

		switch(getMethod()) {
			case GET:
				parseHeaders(in);
				break;

			default:
				throw new NotImplementedException();
		}

		Logger.println("Headers done");
	}

	private void parseHeaders(BufferedReader in) throws IOException {
		String headerLine;

		Logger.println("Parsing headers!");

		while((headerLine = in.readLine()) != null && (!headerLine.equals("\r\n") && !headerLine.equals(""))) {
			Logger.println("Got a header: " + headerLine);

			String[] headerSplit = headerLine.split("\\s+", 2);

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
