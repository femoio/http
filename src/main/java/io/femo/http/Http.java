package io.femo.http;

import io.femo.http.drivers.DefaultDriver;
import io.femo.http.drivers.DefaultHttpRouter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by felix on 9/10/15.
 */
public class Http {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";
    public static final String UPDATE = "UPDATE";

    private static HttpDriver driver = new DefaultDriver();

    public static void installDriver(HttpDriver driver) {
        Http.driver = driver;
    }

    public static HttpRequest url(URL url) {
        return driver.url(url);
    }

    public static HttpRequest url(String url)  {
        return driver.url(url);
    }

    public static HttpRequest get(URL url) {
        return url(url).method("GET");
    }

    public static HttpRequest post(URL url) {
        return url(url).method("POST");
    }

    public static HttpRequest put(URL url) {
        return url(url).method("PUT");
    }

    public static HttpRequest update(URL url) {
        return url(url).method("UPDATE");
    }

    public static HttpRequest delete(URL url) {
        return url(url).method("DELETE");
    }

    public static HttpRequest patch(URL url) {
        return url(url).method("PATCH");
    }

    public static HttpRequest get(String url) {
        return url(url).method("GET");
    }

    public static HttpRequest post(String url) {
        return url(url).method("POST");
    }

    public static HttpRequest put(String url) {
        return url(url).method("PUT");
    }

    public static HttpRequest update(String url) {
        return url(url).method("UPDATE");
    }

    public static HttpRequest delete(String url) {
        return url(url).method("DELETE");
    }

    public static HttpRequest patch(String url) {
        return url(url).method("PATCH");
    }


    public static HttpServer server(int port) {
        return server(port, false);
    }

    public static HttpServer server(int port, boolean ssl) {
        return driver.server(port, ssl);
    }

    @Contract(" -> !null")
    public static HttpRouter router() {
        return new DefaultHttpRouter();
    }
}
