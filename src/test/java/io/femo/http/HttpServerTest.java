package io.femo.http;

import io.femo.http.drivers.DefaultDriver;
import org.junit.*;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import static com.jayway.awaitility.Awaitility.*;
import static com.jayway.awaitility.Duration.*;
import static java.util.concurrent.TimeUnit.*;

/**
 * Created by felix on 4/25/16.
 */
public class HttpServerTest {

    private static HttpServer httpServer;

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(10,SECONDS));

    @BeforeClass
    public static void setUp() throws Exception {
        Http.installDriver(new DefaultDriver());
        httpServer = Http.server(8080)
                .use((request, response) -> {System.out.println(request.method().toUpperCase() + " " + request.path()); return false;})
                .get("/", ((request, response) -> {
                    response.entity("Did it!");
                    return true;
                })).start();
        await().atMost(4, SECONDS).until(httpServer::ready);
    }

    @Test
    public void testGet() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("7", response.header("Content-Length").value());
        assertEquals("Did it!", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        httpServer.stop();
    }
}
