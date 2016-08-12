package io.femo.http.middleware;

import io.femo.http.Http;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.drivers.DefaultHttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by felix on 6/9/16.
 */
public class EnvironmentReplacerMiddlewareTest {

    @Test
    public void testReplaceHost() throws Exception {
        HttpRequest request = Http.get("http://localhost:8080/").header("Host", "localhost:8080");
        HttpResponse response = new DefaultHttpResponse().header("Content-Type", "text/html");
        EnvironmentReplacerMiddleware environmentReplacerMiddleware = new EnvironmentReplacerMiddleware();
        response.entity("this is ${{req.host}}!");
        environmentReplacerMiddleware.handle(request, response);
        assertEquals("this is localhost:8080!", response.responseString());
    }
}