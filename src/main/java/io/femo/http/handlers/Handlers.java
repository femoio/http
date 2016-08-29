package io.femo.http.handlers;

import io.femo.http.HttpHandler;
import io.femo.http.HttpMiddleware;
import io.femo.http.middleware.EnvironmentReplacerMiddleware;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.PrintStream;

/**
 * Created by felix on 6/28/16.
 */
public final class Handlers {

    @Contract("_, _, _ -> !null")
    public static HttpHandler buffered(File source, boolean caching, String mimeType) {
        return FileHandler.buffered(source, caching, mimeType);
    }

    @Contract("_, _, _ -> !null")
    public static HttpHandler resource(String resourceName, boolean caching, String mimeType) {
        return FileHandler.resource(mimeType, caching, resourceName);
    }

    @Contract("_, _, _ -> !null")
    public static HttpHandler directory(File dir, boolean caching, int cacheTime) {
        return new DirectoryFileHandler(dir, caching, cacheTime);
    }

    @Contract("_ -> !null")
    public static HttpMiddleware debug(PrintStream printStream) {
        return new HttpDebugger(printStream);
    }

    @Contract(" -> !null")
    public static HttpMiddleware log() {
        return LoggingHandler.log();
    }

    @Contract(" -> !null")
    public static HttpMiddleware environment() {
        return new EnvironmentReplacerMiddleware();
    }
}
