package io.femo.http;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public interface HttpRouter extends HttpHandler, HttpRoutable<HttpRouter> {

    HttpRouter parentPath(String path);

}
