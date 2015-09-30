package com.jessieamorris.httpserver.tests;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.InvalidRequestException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;
import com.jessieamorris.httpserver.server.HttpRequest;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by jessie.
 */
public class HttpRequestTest {
	@Test(expected=InvalidRequestException.class)
	public void shouldParseEmptyRequest() throws NotImplementedException, IOException, InvalidRequestException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("").getBytes(StandardCharsets.UTF_8)))));
	}

	@Test(expected=InvalidRequestException.class)
	public void shouldParseNonsenseRequest() throws NotImplementedException, IOException, InvalidRequestException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(("stahoeusahtosuanoths").getBytes(StandardCharsets.UTF_8)))));
	}

	@Test(expected=InvalidRequestException.class)
	public void shouldParseInvalidVersion() throws NotImplementedException, IOException, InvalidRequestException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("GET /index.html " +
				"HTTP/1.0\n" + "Host: www.example.com").getBytes(StandardCharsets.UTF_8)))));
	}

	@Test(expected=InvalidRequestException.class)
	public void shouldParseInvalidMethod() throws IOException, InvalidRequestException, NotImplementedException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("FOO /index.html " +
				"HTTP/1.1\n" + "Host: www.example.com").getBytes(StandardCharsets.UTF_8)))));
	}

	@Test
	public void shouldParseGetRequest() throws NotImplementedException, IOException, InvalidRequestException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("GET /index.html " +
				"HTTP/1.1\n" + "Host: www.example.com").getBytes(StandardCharsets.UTF_8)))));

		Assert.assertEquals("Version", "HTTP/1.1", request.getVersion());
		Assert.assertEquals("Method", HttpRequest.Method.GET, request.getMethod());
		Assert.assertEquals("Path", "/index.html", request.getURI().getPath());
		Assert.assertEquals("Host Header", "www.example.com", request.getHeadersMap().get("Host").getValue());
		Assert.assertEquals("Body", null, request.getBody());
	}

	@Test
	public void shouldParseLotsOfHeadersRequest() throws NotImplementedException, IOException, InvalidRequestException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("GET /index.html " +
				"HTTP/1.1\n" + "Host: www.example.com\n" + "Accepts: application/json").getBytes(StandardCharsets.UTF_8)))));

		ArrayList<Header> headersList = new ArrayList<Header>();
		headersList.add(new Header("Host", "www.example.com"));
		headersList.add(new Header("Accepts", "application/json"));

		Assert.assertEquals("Version", "HTTP/1.1", request.getVersion());
		Assert.assertEquals("Method", HttpRequest.Method.GET, request.getMethod());
		Assert.assertEquals("Path", "/index.html", request.getURI().getPath());
		Assert.assertEquals("Host Header", "www.example.com", request.getHeadersMap().get("Host").getValue());
		Assert.assertEquals("Accepts Header", "application/json", request.getHeadersMap().get("Accepts").getValue());

		Assert.assertArrayEquals("Headers collection", headersList.toArray(), request.getHeadersCollection().toArray());

		Assert.assertEquals("Body", null, request.getBody());
	}

	@Test
	public void shouldParsePost() throws IOException, InvalidRequestException, NotImplementedException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("POST /index.html " +
				"HTTP/1.1\n" + "Host: www.example.com\n\n" + "THIS IS THE BODY").getBytes(StandardCharsets.UTF_8)))));

		Assert.assertEquals("Version", "HTTP/1.1", request.getVersion());
		Assert.assertEquals("Method", HttpRequest.Method.POST, request.getMethod());
		Assert.assertEquals("Path", "/index.html", request.getURI().getPath());
		Assert.assertEquals("Host Header", "www.example.com", request.getHeadersMap().get("Host").getValue());
		Assert.assertEquals("Body", "THIS IS THE BODY", request.getBody());
	}

	@Test
	public void shouldParsePostWithNewlines() throws IOException, InvalidRequestException, NotImplementedException {
		HttpRequest request = new HttpRequest();
		request.parseRequest(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("POST /index.html " +
				"HTTP/1.1\n" + "Host: www.example.com\n\n" + "THIS IS THE BODY\n\nWITH A LINE BREAK").getBytes(StandardCharsets.UTF_8)))));

		Assert.assertEquals("Version", "HTTP/1.1", request.getVersion());
		Assert.assertEquals("Method", HttpRequest.Method.POST, request.getMethod());
		Assert.assertEquals("Path", "/index.html", request.getURI().getPath());
		Assert.assertEquals("Host Header", "www.example.com", request.getHeadersMap().get("Host").getValue());
		Assert.assertEquals("Body", "THIS IS THE BODY\n\nWITH A LINE BREAK", request.getBody());
	}
}
