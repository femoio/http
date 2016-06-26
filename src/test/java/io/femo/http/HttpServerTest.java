package io.femo.http;

import io.femo.http.drivers.DefaultDriver;
import io.femo.http.handlers.Authentication;
import io.femo.http.handlers.DirectoryFileHandler;
import io.femo.http.handlers.FileHandler;
import io.femo.http.handlers.auth.CredentialProvider;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.PrintStream;

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
        HttpRouter router = Http.router()
                .get("/default", (req, res) -> {
                    res.entity("body, html {font-family: \"Arial\";}");
                    res.header("Content-Type", "text/css");
                    return true;
                });
        HttpRouter authRouter = Http.router()
                .use("/basic", Http.router()
                    .use(Authentication.basic("Basic_Authentication_Realm", username -> new CredentialProvider.Credentials("felix", "test")))
                        .get("/", (request, response) -> {
                            response.entity("Did it!");
                            return true;
                        })
                ).use("/digest", Http.router()
                        .use(Authentication.digest("Digest_Authentication_Realm", username -> new CredentialProvider.Credentials("felix", "test")))
                        .get("/", (request, response) ->  {
                            response.entity("Did it!");
                            return true;
                        })
                    );
        httpServer = Http.server(8080)
                .get("/", ((request, response) -> {
                    response.entity("Did it!");
                    return true;
                }))
                .post("/", (request, response) -> {
                    response.entity(request.entityBytes());
                    return true;
                })
                .get("/test-res", FileHandler.resource("/test.txt", true, "text/plain"))
                .get("/test-file", FileHandler.buffered(new File("test.txt"), true, "text/plain"))
                .use("/test-dir", new DirectoryFileHandler(new File("testDocs"), false, 0))
                .use("/style", router)
                .use("/auth", authRouter)
                .after((request, response) -> {
                    System.out.printf("%-10s %s - %s byte(s)\n", request.method(), request.path(),
                            response.hasHeader("Content-Length") ? response.header("Content-Length").value() : " --");
                }).start();
        PrintStream printStream = new PrintStream("test.txt");
        printStream.print("Hello World");
        printStream.close();
        File testDocs = new File("testDocs");
        if(!testDocs.exists()) {
            testDocs.mkdir();
        }
        printStream = new PrintStream("testDocs/test.txt");
        printStream.print("Hello World");
        printStream.close();
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

    @Test
    public void testStyle() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/style/default").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("text/css", response.header("Content-Type").value());
        assertEquals("body, html {font-family: \"Arial\";}", response.responseString());
    }

    @Test
    public void testResource() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/test-res").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("Hello World", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
    }

    @Test
    public void testFile() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/test-file").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("Hello World", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
    }

    @Test
    public void testDir() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/test-dir/test.txt").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("Hello World", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
    }



    @Test
    public void testBasicAuth() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/auth/basic/").basicAuth("felix", "test").response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("7", response.header("Content-Length").value());
        assertEquals("Did it!", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
        response = Http.get("http://localhost:8080/auth/basic/").response();
        assertNotNull(response);
        assertEquals(401, response.statusCode());
        assertTrue(response.hasHeader("WWW-Authenticate"));
        assertTrue(response.header("WWW-Authenticate").value().startsWith("Basic"));
    }

    @Test
    public void testDigestAuth() throws Exception {
        HttpResponse response = Http.get("http://localhost:8080/auth/digest/").using(io.femo.http.Authentication.digest("felix", "test")).response();
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("7", response.header("Content-Length").value());
        assertEquals("Did it!", response.responseString());
        assertEquals("text/plain", response.header("Content-Type").value());
        response = Http.get("http://localhost:8080/auth/digest/").response();
        assertNotNull(response);
        assertEquals(401, response.statusCode());
        assertTrue(response.hasHeader("WWW-Authenticate"));
        assertTrue(response.header("WWW-Authenticate").value().startsWith("Digest"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        httpServer.stop();
        new File("test.txt").deleteOnExit();
        FileUtils.deleteDirectory(new File("testDocs"));
    }
}
