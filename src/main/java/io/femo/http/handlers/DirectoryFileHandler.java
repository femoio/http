package io.femo.http.handlers;

import io.femo.http.*;
import io.femo.http.helper.HttpCacheControl;
import io.femo.http.helper.HttpHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by felix on 6/11/16.
 */
public class DirectoryFileHandler implements HttpRoutable<DirectoryFileHandler>, HttpHandler {

    private File parent;
    private boolean caching;
    private int cacheTime;

    public DirectoryFileHandler(File parent, boolean caching, int cacheTime) {
        this.parent = parent;
        this.caching = caching;
        this.cacheTime = cacheTime;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        String path = request.path();
        if(path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        File file = new File(parent, path);
        if(file.exists()) {
            if(caching) {
                if(HttpCacheControl.cacheControl(request, response, cacheTime, Long.toHexString(file.lastModified()))) {
                    return true;
                }
            }
            try {
                response.header("Content-Length", String.valueOf(file.length()));
                response.header("Content-Type", HttpHelper.context().mime().contentType(file));
                response.entity(new FileInputStream(file));
                return true;
            } catch (FileNotFoundException e) {
                throw new HttpHandleException(StatusCode.INTERNAL_SERVER_ERROR, "Could not open resource for read: " + path, e);
            }
        }
        return false;
    }

    @Override
    public DirectoryFileHandler use(HttpMiddleware handler) {
        return this;
    }

    @Override
    public DirectoryFileHandler use(String path, HttpMiddleware handler) {
        return this;
    }

    @Override
    public DirectoryFileHandler use(HttpHandler handler) {
        return this;
    }

    @Override
    public DirectoryFileHandler use(String path, HttpHandler httpHandler) {
        return this;
    }

    @Override
    public DirectoryFileHandler use(String method, String path, HttpHandler httpHandler) {
        return this;
    }

    @Override
    public DirectoryFileHandler after(HttpMiddleware middleware) {
        return this;
    }

    @Override
    public boolean matches(HttpRequest httpRequest) {
        String path = httpRequest.path();
        if(path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        return new File(parent, path).exists();
    }
}
