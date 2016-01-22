package io.femo.http;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by felix on 9/10/15.
 */
public class StatusCodeTest {

    @Test
    public void testIndex() throws Exception {
        assertEquals("Status OK", 200, StatusCode.OK.status());
    }

    @Test
    public void testFind() throws Exception {
        assertEquals("Status OK", 200, StatusCode.find(200).status());
        assertEquals("Status Not Found", 404, StatusCode.find(404).status());
    }

    @Test
    public void testConstructHttpStatus() throws Exception {
        StatusCode statusCode = StatusCode.constructFromHttpStatusLine("HTTP/1.1 200 OK");
        assertEquals("Status code", 200, statusCode.status());
        assertEquals("Status Message", "OK", statusCode.statusMessage());
    }
}