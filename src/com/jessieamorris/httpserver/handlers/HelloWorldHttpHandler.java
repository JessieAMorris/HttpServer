package com.jessieamorris.httpserver.handlers;

import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;

/**
 * Created by jessie.
 */
public class HelloWorldHttpHandler extends SimpleHttpHandler {
	@Override
	public void onGet(HttpRequest request, HttpResponse response) throws Exception {
		response.setBody("Hello World!");
		response.handleRequest(null);
	}
}
