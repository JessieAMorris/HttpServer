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
	private ServerSocket serverSocket;
	private IHttpHandler httpHandler;
	private Executor executor = Executors.newFixedThreadPool(8);

	/**
	 * Creates an HTTP server on the specified port number using the given HttpHandler. This is a multi-threaded
	 * HTTP server that runs in a background thread. To change the Executor used in handling requests, see setExecutor.
	 * The close method on this class should be called when you have finished with this object.
	 *
	 * @param  portNumber the port number to listen to
	 * @param  httpHandler the handler class used to listen to requests and respond to them
	 * @see         HttpRequest
	 * @see         HttpResponse
	 * @see         IHttpHandler
	 */
	public HttpServer(int portNumber, IHttpHandler httpHandler) {
		this.httpHandler = httpHandler;

		try {
			serverSocket = new ServerSocket(portNumber);

			System.out.println("Server started on port " + portNumber);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Socket clientSocket = null;
						while ((clientSocket = serverSocket.accept()) != null) {
							doLoop(clientSocket);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doLoop(final Socket clientSocket) throws IOException {
		Logger.println("Connection made?");

		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					OutputStream out = clientSocket.getOutputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					HttpRequest request = new HttpRequest();
					HttpResponse response = new HttpResponse(out);

					try {
						request.parseRequest(in);

						handleRequest(request, response);

						if (!response.wasSent()) {
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

	/**
	 * Sets the executor used for handling requests
	 *
	 * @param  executor the executor used for handling requests
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	// TODO: There's probably a better way, but I don't know what it is.
	private void handleRequest(HttpRequest request, HttpResponse response) throws Exception {
		switch (request.getMethod()) {
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

	/**
	 * Closes any open sockets or other resources used by the HttpServer
	 *
	 * @throws IOException  Thrown when the socket cannot be closed due to an IO error
	 */
	public void close() throws IOException {
		serverSocket.close();
	}
}
