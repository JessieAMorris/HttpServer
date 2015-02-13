package com.jessieamorris.httpserver.logging;

/**
 * Created by jessie.
 */
public class Logger {
	private static boolean debug = false;

	public static void setDebug(boolean debug) {
		Logger.debug = debug;
	}

	public static void println(String output) {
		if(debug) {
			System.out.println(output);
		}
	}
}
