package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.exceptions.InvalidRequestException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;

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
			clientSocket = serverSocket.accept();

			System.out.println("Connection made?");

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			HttpRequest request = new HttpRequest();
			HttpResponse response = new HttpResponse(out);

			try {
				request.parseRequest(in);
				response.handleRequest(request);
			} catch(HttpException e) {
				response.handleException(e);
			} finally {
				in.close();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
