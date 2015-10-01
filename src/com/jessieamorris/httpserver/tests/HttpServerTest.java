package com.jessieamorris.httpserver.tests;

import com.jessieamorris.httpserver.handlers.HelloWorldHttpHandler;
import com.jessieamorris.httpserver.server.HttpServer;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by jessie.
 */
public class HttpServerTest {
	private static final int testPortNumber = 12485;

	@Test
	public void shouldOpenASocket() throws Exception {
		HttpServer httpServer = null;
		Socket clientSocket = null;
		try {
			httpServer = new HttpServer(testPortNumber, new HelloWorldHttpHandler());

			Thread.sleep(500);

			clientSocket = new Socket("localhost", testPortNumber);
			Assert.assertTrue("Socket connected", clientSocket.isConnected());
		} finally {
			if (clientSocket != null) {
				clientSocket.close();
			}
			if (httpServer != null) {
				httpServer.close();
			}
		}
	}

	@Test
	public void shouldRespond() throws Exception {
		HttpServer httpServer = null;
		Socket clientSocket = null;
		try {
			httpServer = new HttpServer(testPortNumber + 1, new HelloWorldHttpHandler());

			Thread.sleep(500);

			clientSocket = new Socket("localhost", testPortNumber + 1);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			out.println("GET /index.html " +
					"HTTP/1.1\n" +
					"Host: www.example.com\n");

			String response = "";
			int character;
			while ((character = in.read()) != -1) {
				response += (char) character;
			}

			Assert.assertTrue("Response via Socket",
					response.matches(
					"HTTP/1.1 200 OK\n" +
					"Date: .*\n" +
					"Content-Type: text/html\n" +
					"\n" +
					"Hello World!"));
			httpServer.close();
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpServer != null) {
				httpServer.close();
			}
			if (clientSocket != null) {
				clientSocket.close();
			}
		}
	}
}
