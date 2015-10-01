package com.jessieamorris.httpserver.handlers;

import com.jessieamorris.httpserver.common.Header;
import com.jessieamorris.httpserver.exceptions.NotFoundException;
import com.jessieamorris.httpserver.logging.Logger;
import com.jessieamorris.httpserver.server.HttpRequest;
import com.jessieamorris.httpserver.server.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A simple implementation of a file serving HTTP handler. It serves files from the current directory only.
 */
public class BasicFileHttpHandler extends SimpleHttpHandler {
	File currentDirectory = new File(Paths.get("./").toAbsolutePath().toUri());

	@Override
	public void onGet(HttpRequest request, HttpResponse response) throws Exception {
		try {
			URI requestUri = request.getURI();

			Path path = Paths.get("./", requestUri.getPath()).toAbsolutePath();

			Logger.println("Trying to get a file at: " + path);

			checkForPathTransversalIssues(path);

			Charset charset = Charset.forName("US-ASCII");
			response.setBodyReader(Files.newBufferedReader(path, charset));

			String mimeType = Files.probeContentType(path);
			if (mimeType == null) {
				mimeType = "text/plain";
			}

			response.setHeader(new Header("Content-Type", mimeType));

			response.handleRequest(request);
		} catch (NoSuchFileException e) {
			throw new NotFoundException();
		} catch (IOException e) {
			throw new NotFoundException();
		}
	}

	// Used to prevent path transversing issues (for example a malaicious entity including /../ in the path
	// to get your /etc/shadow file.
	private void checkForPathTransversalIssues(Path path) throws NotFoundException, IOException {
		File file = new File(path.toUri());
		final String canonicalDirPath = currentDirectory.getCanonicalPath() + File.separator;
		final String canonicalEntryPath = file.getCanonicalPath();

		if (!canonicalEntryPath.startsWith(canonicalDirPath)) {
			throw new NotFoundException();
		}
	}

}
