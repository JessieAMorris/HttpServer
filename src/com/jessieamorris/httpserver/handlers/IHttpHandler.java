package com.jessieamorris.httpserver.handlers;

import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;

/**
 * Interface to implement for handling HTTP requests.
 *
 * @see         HttpRequest
 * @see         HttpResponse
 * @see         SimpleHttpHandler
 */
public interface IHttpHandler {
	void onGet(HttpRequest request, HttpResponse response) throws Exception;
	void onHead(HttpRequest request, HttpResponse response) throws Exception;
	void onPost(HttpRequest request, HttpResponse response) throws Exception;
	void onPut(HttpRequest request, HttpResponse response) throws Exception;
	void onDelete(HttpRequest request, HttpResponse response) throws Exception;
	void onTrace(HttpRequest request, HttpResponse response) throws Exception;
	void onOptions(HttpRequest request, HttpResponse response) throws Exception;
	void onConnect(HttpRequest request, HttpResponse response) throws Exception;
	void onPatch(HttpRequest request, HttpResponse response) throws Exception;
}
