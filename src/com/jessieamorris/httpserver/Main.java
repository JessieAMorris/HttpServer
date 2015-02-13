package com.jessieamorris.httpserver;

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

		new HttpServer(portNumber);
	}


}
