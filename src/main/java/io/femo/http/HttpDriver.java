package io.femo.http;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by felix on 9/10/15.
 */
public abstract class HttpDriver {

    public abstract HttpRequest url(URL url);

    public HttpRequest url(String url) {
        try {
            return url(new URL(url));
        } catch (MalformedURLException e) {
            throw new HttpException(null, e);
        }
    }

    public abstract HttpServer server(int port, boolean ssl);
}
