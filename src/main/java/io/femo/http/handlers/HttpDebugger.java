package io.femo.http.handlers;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpMiddleware;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * Created by felix on 6/6/16.
 */
public class HttpDebugger implements HttpMiddleware {

    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    private PrintStream printStream;

    public HttpDebugger(PrintStream printStream) {
        this.printStream = printStream;
        LOGGER.warn("Attention: HTTP Debugging has been activated! This might lead to excessive logging of HTTP Traffic!");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        request.print(printStream);
        response.print(printStream);
    }
}
