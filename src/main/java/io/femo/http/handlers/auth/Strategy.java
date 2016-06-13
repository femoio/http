package io.femo.http.handlers.auth;

import io.femo.http.HttpRequest;

/**
 * Created by felix on 6/13/16.
 */
public interface Strategy {

    boolean authenticate(HttpRequest request);

    String name();
    String realm();
    String authenticateHeader();
}