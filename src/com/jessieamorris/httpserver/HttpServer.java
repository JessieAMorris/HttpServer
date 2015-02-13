package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;
import com.jessieamorris.httpserver.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jessie.
 */
public class HttpServer {
	public HttpServer(int portNumber) {
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);

			Logger.println("Server started");

			Socket clientSocket = null;

			while ((clientSocket = serverSocket.accept()) != null) {
				Logger.println("Connection made?");

				OutputStream out = clientSocket.getOutputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				HttpRequest request = new HttpRequest();
				HttpResponse response = new HttpResponse(out);

				try {
					request.parseRequest(in);

					if (request.getMethod() == HttpRequest.Method.GET) {
						URI requestUri = request.getURI();

						// TODO: Security issues, could escalate by using "../" in paths
						Path path = Paths.get("./", requestUri.getPath());

						Logger.println("Trying to get a file at: " + path);

						Charset charset = Charset.forName("US-ASCII");
						response.setBodyReader(Files.newBufferedReader(path, charset));

						String mimeType = Files.probeContentType(path);
						if (mimeType == null) {
							mimeType = "text/plain";
						}

						response.setHeader(new Header("Content-Type", mimeType));

						response.handleRequest(request);
					} else {
						response.handleException(new NotImplementedException());
					}
				} catch (HttpException e) {
					response.handleException(e);
				} catch (NoSuchFileException e) {
					response.handleException(new NotFoundException());
				} catch (IOException e) {
					response.handleException(new NotFoundException());
				} finally {
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
