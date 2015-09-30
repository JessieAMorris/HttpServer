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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Header) {
			Header header = (Header) obj;
			return (header.name.equals(this.name) && header.value.equals(this.value));
		}

		return false;
	}
}
