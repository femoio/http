package io.femo.http.drivers;

import io.femo.http.HttpDriver;
import io.femo.http.HttpRequest;

import java.net.URL;

/**
 * Created by felix on 9/11/15.
 */
public class DefaultDriver extends HttpDriver {

    @Override
    public HttpRequest url(URL url) {
        return new DefaultHttpRequest(url);
    }
}