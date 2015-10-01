package com.jessieamorris.httpserver.server;

import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.exceptions.InternalServerException;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.handlers.IHttpHandler;
import com.jessieamorris.httpserver.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jessie.
 */
public class HttpServer {
	private IHttpHandler httpHandler;
	private Executor executor = Executors.newFixedThreadPool(8);

	public HttpServer(int portNumber, IHttpHandler httpHandler) {
		this.httpHandler = httpHandler;

		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);

			System.out.println("Server started on port " + portNumber);

			Socket clientSocket = null;

			while ((clientSocket = serverSocket.accept()) != null) {
				Logger.println("Connection made?");

				final Socket finalClientSocket = clientSocket;

				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							OutputStream out = finalClientSocket.getOutputStream();
							BufferedReader in = new BufferedReader(new InputStreamReader(finalClientSocket.getInputStream()));

							HttpRequest request = new HttpRequest();
							HttpResponse response = new HttpResponse(out);

							try {
								request.parseRequest(in);

								handleRequest(request, response);

								if(!response.wasSent()) {
									throw new NotFoundException();
								}
							} catch (HttpException e) {
								response.handleException(e);
							} catch (Exception e) {
								response.handleException(new InternalServerException(e));
							} finally {
								in.close();
								out.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	// TODO: There's probably a better way, but I don't know what it is.
	private void handleRequest(HttpRequest request, HttpResponse response) throws Exception {
		switch(request.getMethod()) {
			case OPTIONS:
				httpHandler.onOptions(request, response);
				break;
			case GET:
				httpHandler.onGet(request, response);
				break;
			case HEAD:
				httpHandler.onHead(request, response);
				break;
			case POST:
				httpHandler.onPost(request, response);
				break;
			case PUT:
				httpHandler.onPut(request, response);
				break;
			case DELETE:
				httpHandler.onDelete(request, response);
				break;
			case TRACE:
				httpHandler.onTrace(request, response);
				break;
			case CONNECT:
				httpHandler.onConnect(request, response);
				break;
			case PATCH:
				httpHandler.onPatch(request, response);
				break;
		}
	}
}
