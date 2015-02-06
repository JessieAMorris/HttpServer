package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.HttpException;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jessie on 15-02-05.
 */
public class HttpResponse {
	private PrintWriter out = null;

	private int statusCode = 200;
	private String statusMessage = "OK";
	private static final String httpVersion = "HTTP/1.1";

	private List<Header> headers = new ArrayList<Header>();

	private String body = "HELLO WORLD!";

	public HttpResponse(PrintWriter out) {
		this.out = out;

		headers.add(new Header("Date", getDate()));
		headers.add(new Header("Content-Type", "text/html"));
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void handleRequest(HttpRequest request) {
		switch(request.getMethod()) {

		}

		out.println(httpVersion + " " + statusCode + " " + statusMessage);

		for (Header header : headers) {
			System.out.println("Responding with a header: " + header);
			out.println(header.getName() + ": " + header.getValue());
		}

		out.println();

		out.print(body);

		out.close();
	}

	public void handleException(HttpException exception) {
		out.println(exception.getHttpStatusCode() + " " + exception.getHttpStatusMessage());
		out.close();
	}

	private static String getDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}
}
