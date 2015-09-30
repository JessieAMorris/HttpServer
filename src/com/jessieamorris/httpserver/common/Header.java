package com.jessieamorris.httpserver.common;

/**
 * Created by jessie on 15-02-05.
 */
public class Header {
	private String name;
	private String value;

	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}
}
