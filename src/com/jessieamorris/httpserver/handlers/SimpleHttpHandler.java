package com.jessieamorris.httpserver.handlers;

import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;

/**
 * Created by jessie.
 */
public abstract class SimpleHttpHandler implements IHttpHandler {
	@Override
	public void onGet(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onHead(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onPost(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onPut(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onDelete(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onTrace(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onOptions(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onConnect(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void onPatch(HttpRequest request, HttpResponse response) throws Exception {
	}
}
