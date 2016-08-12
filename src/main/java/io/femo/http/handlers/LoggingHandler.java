package io.femo.http.handlers;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpMiddleware;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by felix on 6/6/16.
 */
public class LoggingHandler implements HttpMiddleware {

    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        LOGGER.info("{} {} - {} {} {} byte(s)", request.method().toUpperCase(), request.path(),
                response.statusCode(), response.status().statusMessage(),
                response.hasHeader("Content-Length") ? response.header("Content-Length").asInt() : "---");
    }

    @Contract(" -> !null")
    public static HttpMiddleware log() {
        return new LoggingHandler();
    }
}
