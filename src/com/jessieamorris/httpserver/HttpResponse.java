package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.HttpException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Created by jessie.
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

	public HttpResponse(OutputStream outputStream) {
		this.outputStream = outputStream;

		responseHeaders.put("Date", new Header("Date", getDate()));
		responseHeaders.put("Content-Type", new Header("Content-Type", "text/html"));
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setBodyReader(BufferedReader bodyReader) {
		this.bodyReader = bodyReader;
	}

	public void handleRequest(HttpRequest request) throws IOException {
		boolean doGzip = false;
		Header acceptEncodings = (request != null && request.getHeadersMap() != null) ? request.getHeadersMap().get("Accept-Encoding") : null;
		if(acceptEncodings != null) {
			List<String> encodingValues = Arrays.asList(acceptEncodings.getValue().split("\\s*,\\s*"));

			if(encodingValues.contains("gzip")) {
				System.out.println("Changing output to gzip");

				doGzip = true;
				responseHeaders.put("Content-Encoding", new Header("Content-Encoding", "gzip"));
			}
		}

		out = new PrintWriter(outputStream, false);

		out.println(httpVersion + " " + statusCode + " " + statusMessage);

		for (Header header : responseHeaders.values()) {
			System.out.println("Responding with a header: " + header);
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
			String line;
			while((line = bodyReader.readLine()) != null) {
				out.println(line);
			}
		} else {
			if(body == null) {
				body = "";
			}

			out.print(body);
		}

		out.flush();
		out.close();
	}

	public void handleException(HttpException exception) throws IOException {
		statusCode = exception.getHttpStatusCode();
		statusMessage = exception.getHttpStatusMessage();

		body = statusCode + " " + statusMessage;

		handleRequest(null);
	}

	private static String getDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}

	public void setHeader(Header header) {
		responseHeaders.put(header.getName(), header);
	}
}
