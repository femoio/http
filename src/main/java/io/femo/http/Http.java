package io.femo.http;

import io.femo.http.drivers.DefaultDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by felix on 9/10/15.
 */
public class Http {

    private static HttpDriver driver = new DefaultDriver();

    public static void installDriver(HttpDriver driver) {
        Http.driver = driver;
    }

    public static HttpRequest url(URL url) {
        return driver.url(url);
    }

    public static HttpRequest url(String url) throws MalformedURLException {
        return url(new URL(url));
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

    public static HttpRequest get(String url) throws MalformedURLException {
        return url(url).method("GET");
    }

    public static HttpRequest post(String url) throws MalformedURLException {
        return url(url).method("POST");
    }

    public static HttpRequest put(String url) throws MalformedURLException {
        return url(url).method("PUT");
    }

    public static HttpRequest update(String url) throws MalformedURLException {
        return url(url).method("UPDATE");
    }

    public static HttpRequest delete(String url) throws MalformedURLException {
        return url(url).method("DELETE");
    }

    public static HttpRequest patch(String url) throws MalformedURLException {
        return url(url).method("PATCH");
    }

    public static HttpServer server(int port) throws IOException {
        return driver.server(port);
    }

}
