package com.jessieamorris.httpserver;

import com.jessieamorris.httpserver.logging.Logger;

/**
 * Created by jessie on 15-02-05.
 */
public class Main {
	private static int PORT = 8888;
	public static void main(String[] args) {
		int portNumber = PORT;

		Logger.setDebug(false);

		if(args.length > 0) {
			portNumber = Integer.parseInt(args[0]);
		}

		new HttpServer(portNumber);
	}


}
