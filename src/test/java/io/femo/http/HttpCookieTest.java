package io.femo.http;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by felix on 1/19/16.
 */
public class HttpCookieTest {

    private HttpCookie httpCookie;

    @Before
    public void setUp() throws Exception {
        httpCookie = new HttpCookie("Session", "abcd1234");
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals("Cookie Name", "Session", httpCookie.name());
        assertEquals("Cookie Value", "abcd1234", httpCookie.value());
    }

    @Test
    public void testSetName() throws Exception {
        httpCookie.name("X-Session");
        assertEquals("Cookie Name", "X-Session", httpCookie.name());
    }

    @Test
    public void testSetValue() throws Exception {
        httpCookie.value("abcd");
        assertEquals("Cookie Value", "abcd", httpCookie.value());
    }
}