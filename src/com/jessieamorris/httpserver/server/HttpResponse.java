package com.jessieamorris.httpserver.server;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Created by jessie.
 */

/**
 * This class handles the HttpResponse. This is used to set the headers and body of a response to a given request.
 *
 * @see HttpRequest
 */
public class HttpResponse {
	private OutputStream outputStream = null;

	private int statusCode = 200;
	private String statusMessage = "OK";
	private static final String httpVersion = "HTTP/1.1";

	private Map<String, Header> responseHeaders = new HashMap<String, Header>();

	private String body = "";
	private PrintWriter out;
	private BufferedReader bodyReader;
	private boolean wasSent = false;

	/**
	 * Constructor for the HttpResponse object. The Date and Content-Type headers are automatically set.
	 * Date is set to the current date and Content-Type defaults to text/html.
	 *
	 * @param  outputStream the stream to output the HttpResponse once it is finished
	 * @see    OutputStream
	 */
	public HttpResponse(OutputStream outputStream) {
		this.outputStream = outputStream;

		responseHeaders.put("Date", new Header("Date", getDate()));
		responseHeaders.put("Content-Type", new Header("Content-Type", "text/html"));
	}

	/**
	 * Sets the HTTP body to be sent in the response
	 *
	 * @param  body a string containing the HTTP response body.
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Sets the HTTP body to be sent by using the output from a BufferedReader
	 *
	 * @param  bodyReader a BufferedReader containing the HTTP response body. If both a String body (via setBody) and
	 *                    a BufferedReader body (via this method) are set the BufferedReader will overwrite the String
	 *                    body.
	 */
	public void setBodyReader(BufferedReader bodyReader) {
		this.bodyReader = bodyReader;
	}

	/**
	 * Sends the request to a given request. The request is used to determine any custom encoding that needs to be
	 * performed, such as GZip. Any output will be sent to the OutputStream given in the constructor
	 *
	 * @param  request the HttpRequest corresponding to a given respnose. Can be null.
	 * @see         HttpRequest
	 * @throws      IOException throws an IOException when the outputStream is invalid.
	 */
	public void handleRequest(HttpRequest request) throws IOException {
		boolean doGzip = false;
		Header acceptEncodings = (request != null && request.getHeadersMap() != null) ? request.getHeadersMap().get("Accept-Encoding") : null;
		if(acceptEncodings != null) {
			List<String> encodingValues = Arrays.asList(acceptEncodings.getValue().split("\\s*,\\s*"));

			if(encodingValues.contains("gzip")) {
				Logger.println("Changing output to gzip");

				doGzip = true;
				responseHeaders.put("Content-Encoding", new Header("Content-Encoding", "gzip"));
			}
		}

		out = new PrintWriter(outputStream, false);

		out.println(httpVersion + " " + statusCode + " " + statusMessage);

		for (Header header : responseHeaders.values()) {
			Logger.println("Responding with a header: " + header);
			out.println(header.getName() + ": " + header.getValue());
		}

		out.println();
		out.flush();

		if(doGzip) {
			outputStream = new GZIPOutputStream(outputStream);
		}
		out = new PrintWriter(outputStream, false);

		if(bodyReader != null) {
			body = "";
			int character;
			while((character = bodyReader.read()) != -1) {
				out.print((char) character);
			}
		} else {
			if(body == null) {
				body = "";
			}

			out.print(body);
		}

		out.flush();
		out.close();

		wasSent = true;
	}

	/**
	 * Sends an error to the client. The HttpException has additional parameters used for determining error code
	 * and message. For general exceptions the InternalServerException class can be used.
	 *
	 * @param  exception the HttpException that maps to a given error on the server
	 * @see         HttpException
	 * @see         com.jessieamorris.httpserver.exceptions.InternalServerException
	 * @throws      IOException thrown when the outputStream is invalid
	 */
	public void handleException(HttpException exception) throws IOException {
		statusCode = exception.getHttpStatusCode();
		statusMessage = exception.getHttpStatusMessage();
		body = exception.getHttpBody();

		if(body == null) {
			body = statusCode + ": " + statusMessage;
		}

		handleRequest(null);
	}

	private static String getDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * Sets a header for the response. Any previously set headers with the same name will be overwritten.
	 *
	 * @param  header   the HTTP header to be sent in the response
	 */
	public void setHeader(Header header) {
		responseHeaders.put(header.getName(), header);
	}

	/**
	 * Used to determine if the response has been sent to the OutputStream
	 *
	 * @return      true if the response was sent to the OutputStream of false if it has not been sent
	 */
	public boolean wasSent() {
		return wasSent;
	}
}
