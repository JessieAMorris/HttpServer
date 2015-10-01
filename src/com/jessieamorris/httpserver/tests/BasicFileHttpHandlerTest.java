package com.jessieamorris.httpserver.tests;

import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.handlers.BasicFileHttpHandler;
import com.jessieamorris.httpserver.handlers.IHttpHandler;
import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by jessie.
 */
public class BasicFileHttpHandlerTest {
	@Test
	public void shouldServerFiles() throws Exception {
		IHttpHandler handler = new BasicFileHttpHandler();

		HttpRequest request = new HttpRequest() {
			public URI getURI() {
				try {
					uri = new URI("/testResources/example.txt");
				} catch (URISyntaxException e) {
				}

				return uri;
			}
		};

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		handler.onGet(request, response);

		Assert.assertTrue("Response", out.toString(Charset.defaultCharset().name()).matches(
				"HTTP/1.1 200 OK\n" +
				"Date: .*\n" +
				"Content-Type: text/plain\n" +
				"\n" +
				"This is an example file."));
	}

	@Test(expected= NotFoundException.class)
	public void shouldNotServePastRoot() throws Exception {
		IHttpHandler handler = new BasicFileHttpHandler();

		HttpRequest request = new HttpRequest() {
			public URI getURI() {
				try {
					uri = new URI("/../testResources/example.txt");
				} catch (URISyntaxException e) {
				}

				return uri;
			}
		};

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(out);

		handler.onGet(request, response);
	}
}
