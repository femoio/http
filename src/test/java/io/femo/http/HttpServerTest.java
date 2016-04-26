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
    public TestRule timeout = new DisableOnDebug(new Timeout(10, SECONDS));

    @BeforeClass
    public static void setUp() throws Exception {
        Http.installDriver(new DefaultDriver());
        httpServer = Http.server(8080)
                .get("/", ((request, response) -> {
                    response.entity("Did it!");
                    return true;
                }))
                .post("/", (request, response) -> {
                    response.entity(request.entityBytes());
                    return true;
                })
                .after((request, response) -> {
                    System.out.printf("%-10s %s - %s byte(s)\n", request.method(), request.path(),
                            response.hasHeader("Content-Length") ? response.header("Content-Length").value() : " --");
                }).start();
        while (!httpServer.ready());
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {

        }
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

    @Test
    public void testPost() throws Exception {
        HttpResponse response = Http.post("http://localhost:8080/").entity("This is test!").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("This is test!", response.responseString());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        httpServer.stop();
    }
}
