package com.jessieamorris.httpserver.handlers;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.logging.Logger;
import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jessie.
 */
public class BasicFileHttpHandler extends SimpleHttpHandler {
	@Override
	public void onGet(HttpRequest request, HttpResponse response) throws Exception {
		try {
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
		} catch (NoSuchFileException e) {
			response.handleException(new NotFoundException());
		} catch (IOException e) {
			response.handleException(new NotFoundException());
		}
	}
}
