package com.jessieamorris.httpserver.tests;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

/**
 * Created by jessie.
 */
public class HttpResponseTest {
	@Test
	public void testSendingResponse() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		response.setHeader(new Header("Test", "The test header value"));
		response.setHeader(new Header("Date", "Wed, 30 Sep 2015 23:14:13 GMT"));
		response.setHeader(new Header("Content-Type", "text/html"));
		response.setBody("This is the body");

		Assert.assertFalse("Wasn't actually sent before it should be", response.wasSent());

		response.handleRequest(null);

		Assert.assertTrue("Was sent when it should be", response.wasSent());

		Assert.assertEquals("Response", "HTTP/1.1 200 OK\n" +
				"Test: The test header value\n" +
				"Date: Wed, 30 Sep 2015 23:14:13 GMT\n" +
				"Content-Type: text/html\n" +
				"\n" +
				"This is the body", out.toString(Charset.defaultCharset().name()));
	}

	@Test
	public void testSendingGZipResponse() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		response.setHeader(new Header("Test", "The test header value"));
		response.setHeader(new Header("Date", "Wed, 30 Sep 2015 23:14:13 GMT"));
		response.setHeader(new Header("Content-Type", "text/html"));
		response.setBody("This is the body");

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.addHeader(new Header("Accept-Encoding", "gzip"));
		response.handleRequest(httpRequest);

		String body = "";
		Character previousChar = null;
		int pos = 0;
		for(byte currentChar : out.toByteArray()) {
			pos++;
			if(currentChar == '\n' && previousChar != null && previousChar == '\n') {
				byte[] bytes = out.toByteArray();
				byte[] slicedBytes = new byte[bytes.length - pos];

				System.arraycopy(bytes, pos, slicedBytes, 0, slicedBytes.length);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(slicedBytes));
				int b;
				while ((b = gis.read()) != -1) {
					baos.write((byte) b);
				}
				body = baos.toString("UTF-8");
			} else {
				previousChar = (char) currentChar;
			}
		}

		Assert.assertEquals("Response", "This is the body", body);
	}

	@Test
	public void testSendingResponseWithNullBody() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		response.setHeader(new Header("Test", "The test header value"));
		response.setHeader(new Header("Date", "Wed, 30 Sep 2015 23:14:13 GMT"));
		response.setHeader(new Header("Content-Type", "text/html"));
		response.setBody(null);

		response.handleRequest(null);

		Assert.assertEquals("Response", "HTTP/1.1 200 OK\n" +
				"Test: The test header value\n" +
				"Date: Wed, 30 Sep 2015 23:14:13 GMT\n" +
				"Content-Type: text/html\n" +
				"\n" +
				"", out.toString(Charset.defaultCharset().name()));
	}

	@Test
	public void testSendingResponseWithBodyReader() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		response.setHeader(new Header("Test", "The test header value"));
		response.setHeader(new Header("Date", "Wed, 30 Sep 2015 23:14:13 GMT"));
		response.setHeader(new Header("Content-Type", "text/html"));
		response.setBodyReader(new BufferedReader(new StringReader("This is the body\nLine two")));

		response.handleRequest(null);

		Assert.assertEquals("Response", "HTTP/1.1 200 OK\n" +
				"Test: The test header value\n" +
				"Date: Wed, 30 Sep 2015 23:14:13 GMT\n" +
				"Content-Type: text/html\n" +
				"\n" +
				"This is the body\n" +
				"Line two", out.toString(Charset.defaultCharset().name()));
	}

	@Test
	public void testSendingException() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		response.setHeader(new Header("Test", "The test header value"));
		response.setHeader(new Header("Date", "Wed, 30 Sep 2015 23:14:13 GMT"));
		response.setHeader(new Header("Content-Type", "text/html"));
		response.setBodyReader(new BufferedReader(new StringReader("This is the body\nLine two")));

		response.handleException(new NotFoundException());

		Assert.assertEquals("Response", "HTTP/1.1 404 Not Found\n" +
				"Test: The test header value\n" +
				"Date: Wed, 30 Sep 2015 23:14:13 GMT\n" +
				"Content-Type: text/html\n" +
				"\n" +
				"This is the body\n" +
				"Line two", out.toString(Charset.defaultCharset().name()));
	}
}
