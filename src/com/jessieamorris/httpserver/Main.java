package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jessie on 15-02-05.
 */
public class Main {
	private static int PORT = 8888;
	public static void main(String[] args) {
		int portNumber = PORT;

		if(args.length > 0) {
			portNumber = Integer.parseInt(args[0]);
		}

		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);

			System.out.println("Server started");

			Socket clientSocket = null;

			while((clientSocket = serverSocket.accept()) != null) {

				System.out.println("Connection made?");

				OutputStream out = clientSocket.getOutputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				HttpRequest request = new HttpRequest();
				HttpResponse response = new HttpResponse(out);

				try {
					request.parseRequest(in);

					if(request.getMethod() == HttpRequest.Method.GET) {
						URI requestUri = request.getURI();

						// TODO: Security issues, could escalate by using "../" in paths
						Path path = Paths.get("./", requestUri.getPath());

						System.out.println("Trying to get a file at: " + path);

						Charset charset = Charset.forName("US-ASCII");
						response.setBodyReader(Files.newBufferedReader(path, charset));

						String mimeType = Files.probeContentType(path);
						if(mimeType == null) {
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