package io.femo.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.femo.http.drivers.DefaultDriver;
import io.femo.http.events.*;
import org.junit.*;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by felix on 1/19/16.
 */
public class HttpTest {

    private static JsonParser parser;

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(20, TimeUnit.SECONDS));

    @BeforeClass
    public static void setUp() throws Exception {
        parser = new JsonParser();
        Http.installDriver(new DefaultDriver());
        System.out.println("Using " + TestConstants.HTTP.HOST);
    }

    @Test
    public void testHttpGet() throws Exception {
        HttpResponse response = Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/get")).response();
        assertEquals("Status", 200, response.status().status());
        assertNotNull("Response String", response.responseString());
    }

    @Test
    public void testHttpGetWithParameters() throws Exception {
        HttpResponse response = Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/get?param=2")).response();
        assertEquals("Status", 200, response.status().status());
        JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
        JsonObject args = content.getAsJsonObject("args");
        JsonElement param = args.get("param");
        assertNotNull("Param", param);
        assertEquals("Param Value", "2", param.getAsString());
    }

    @Test
    public void testHttpStatusParsing() throws Exception {
        HttpResponse response = Http.get("http://" + TestConstants.HTTP.HOST + "/status/200").response();
        assertEquals("HTTP OK Statuscode", 200, response.statusCode());
        assertEquals("HTTP OK Status", "OK", response.status().statusMessage());
        response = Http.get("http://" + TestConstants.HTTP.HOST + "/status/404").response();
        assertEquals("HTTP Not Found Statuscode", 404, response.statusCode());
        assertEquals("HTTP Not Found Status", "NOT FOUND", response.status().statusMessage());
        response = Http.get("http://" + TestConstants.HTTP.HOST + "/status/500").response();
        assertEquals("HTTP Internal Server Error Statuscode", 500, response.statusCode());
        assertEquals("HTTP Internal Server Error Status", "INTERNAL SERVER ERROR", response.status().statusMessage());
    }

    @Test
    public void testHttpPost() throws Exception {
        HttpResponse response = Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/post")).method("POST").response();
        assertEquals("Status", 200, response.status().status());
        assertNotNull("Response String", response.responseString());
    }

    @Test
    public void testHttpPostWithArguments() throws Exception {
        HttpResponse response = Http.post("http://" + TestConstants.HTTP.HOST + "/post").data("param", "2").response();
        assertEquals("Status", 200, response.statusCode());
        JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
        JsonObject form = content.getAsJsonObject("form");
        JsonElement param = form.get("param");
        assertNotNull("Form Param", param);
        assertEquals("Form Param Value", "2", param.getAsString());
    }

    @Test
    public void testHttpPostWithData() throws Exception {
        HttpResponse response = Http.post("http://" + TestConstants.HTTP.HOST + "/post").entity("Value is 2").contentType("text/plain").response();
        assertEquals("Status", 200, response.statusCode());
        JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
        JsonElement data = content.get("data");
        assertNotNull("Data", data);
        assertEquals("Data Value", "Value is 2", data.getAsString());
    }

    @Test
    public void testGetWithCookies() throws Exception {
        HttpResponse response = Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/cookies")).cookie("Session", "abcd1234").response();
        JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
        JsonObject cookies = content.getAsJsonObject("cookies");
        JsonElement cookie = cookies.get("Session");
        assertNotNull("Cookies", cookie);
        assertEquals("Cookie Value", "abcd1234", cookie.getAsString());
    }

    @Test
    public void testBasicAuthentication() throws Exception {
        HttpResponse response = Http.get("http://" + TestConstants.HTTP.HOST + "/basic-auth/test/test").basicAuth("test", "test").response();
        assertNotNull(response);
        assertEquals("Status", 200, response.statusCode());
    }

    @Test
    @Ignore("httpbin does not support digest authentication with the implemented approach")
    public void testDigestAuthentication() throws Exception {
        HttpResponse response = Http.get("http://" + TestConstants.HTTP.HOST + "/digest-auth/auth/test/test").using(Authentication.digest("test", "test")).response();
        assertNotNull(response);
        assertEquals("Status", 200, response.statusCode());
    }

    @Test
    public void testEvents() throws Exception {
        Http.get("http://" + TestConstants.HTTP.HOST + "/get").event(HttpEventType.ALL, new HttpEventHandler() {
            @Override
            public void handle(HttpEvent event) {
                if(event.eventType() == HttpEventType.SENT) {
                    HttpSentEvent sentEvent = (HttpSentEvent) event;
                    System.out.println(sentEvent.request().requestLine());
                } else if (event.eventType() == HttpEventType.RECEIVED) {
                    HttpReceivedEvent receivedEvent = (HttpReceivedEvent) event;
                    System.out.println(receivedEvent.request().requestLine() + " - " + receivedEvent.response().statusLine());
                } else if (event.eventType() == HttpEventType.HANDLED) {
                    HttpHandledEvent handledEvent = (HttpHandledEvent) event;
                    System.out.println(handledEvent.request().requestLine() + " - " + handledEvent.response().statusLine() + " - " + (handledEvent.callbackExecuted() ? "HANDLED" : "NOT HANDLED"));
                } else {
                    System.out.println(event.eventType());
                }
            }
        }).execute();
    }
}