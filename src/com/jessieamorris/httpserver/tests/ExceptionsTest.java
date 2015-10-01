package com.jessieamorris.httpserver.tests;

import com.jessieamorris.httpserver.exceptions.HttpException;
import com.jessieamorris.httpserver.exceptions.InternalServerException;
import com.jessieamorris.httpserver.exceptions.InvalidRequestException;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.exceptions.NotImplementedException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jessie.
 */
public class ExceptionsTest {
	@Test
	public void shouldReturn404OnNotFound() {
		HttpException e = new NotFoundException();
		Assert.assertEquals("Status code", 404, e.getHttpStatusCode());
		Assert.assertEquals("Status message", "Not Found", e.getHttpStatusMessage());
	}

	@Test
	public void shouldReturn500OnInternalError() {
		Exception thrown;

		try {
			throw new IllegalArgumentException("Test!");
		} catch(Exception e) {
			thrown = e;
		}

		HttpException e = new InternalServerException(thrown);
		Assert.assertEquals("Status code", 500, e.getHttpStatusCode());
		Assert.assertEquals("Status message", "Internal Server Error", e.getHttpStatusMessage());
		Assert.assertEquals("Body", thrown.toString(), e.getHttpBody());
	}

	@Test
	public void shouldReturn400BadRequest() {
		HttpException e = new InvalidRequestException();
		Assert.assertEquals("Status code", 400, e.getHttpStatusCode());
		Assert.assertEquals("Status message", "Bad Request", e.getHttpStatusMessage());
	}

	@Test
	public void shouldReturn501NotImplemented() {
		HttpException e = new NotImplementedException();
		Assert.assertEquals("Status code", 501, e.getHttpStatusCode());
		Assert.assertEquals("Status message", "Not Implemented", e.getHttpStatusMessage());
	}
}
