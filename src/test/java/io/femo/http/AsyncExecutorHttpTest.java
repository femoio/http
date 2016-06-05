package io.femo.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.femo.http.drivers.AsynchronousDriver;
import org.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by felix on 2/9/16.
 */
public class AsyncExecutorHttpTest {

    private static JsonParser parser;

    @BeforeClass
    public static void setUp() throws Exception {
        parser = new JsonParser();
        Http.installDriver(new AsynchronousDriver(5));
    }

    @Test
    public void testHttpGet() throws Exception {
        final Waiter waiter = new Waiter();
        Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/get")).execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("Status", 200, response.status().status());
                assertNotNull("Response String", response.responseString());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testHttpGetWithParameters() throws Exception {
        final Waiter waiter = new Waiter();
        Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/get?param=2")).execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("Status", 200, response.status().status());
                JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
                JsonObject args = content.getAsJsonObject("args");
                JsonElement param = args.get("param");
                assertNotNull("Param", param);
                assertEquals("Param Value", "2", param.getAsString());
                waiter.resume();
            }
        });
        try {
            waiter.await(3000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testHttpStatusParsing() throws Exception {
        final Waiter waiter1 = new Waiter();
        Http.get("http://" + TestConstants.HTTP.HOST + "/status/200").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("HTTP OK Statuscode", 200, response.statusCode());
                assertEquals("HTTP OK Status", "OK", response.status().statusMessage());
                waiter1.resume();
            }
        });
        final Waiter waiter2 = new Waiter();
        Http.get("http://" + TestConstants.HTTP.HOST + "/status/404").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("HTTP Not Found Statuscode", 404, response.statusCode());
                assertEquals("HTTP Not Found Status", "NOT FOUND", response.status().statusMessage());
                waiter2.resume();
            }
        });
        final Waiter waiter3 = new Waiter();
        Http.get("http://" + TestConstants.HTTP.HOST + "/status/500").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("HTTP Internal Server Error Statuscode", 500, response.statusCode());
                assertEquals("HTTP Internal Server Error Status", "INTERNAL SERVER ERROR", response.status().statusMessage());
                waiter3.resume();
            }
        });
        try {
            waiter1.await(3000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        try {
            waiter2.await(3000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        try {
            waiter3.await(3000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testHttpPost() throws Exception {
        final Waiter waiter = new Waiter();
        Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/post")).method("POST").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("Status", 200, response.status().status());
                assertNotNull("Response String", response.responseString());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testHttpPostWithArguments() throws Exception {
        final Waiter waiter = new Waiter();
        Http.post("http://" + TestConstants.HTTP.HOST + "/post").data("param", "2").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("Status", 200, response.statusCode());
                JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
                JsonObject form = content.getAsJsonObject("form");
                JsonElement param = form.get("param");
                assertNotNull("Form Param", param);
                assertEquals("Form Param Value", "2", param.getAsString());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testHttpPostWithData() throws Exception {
        final Waiter waiter = new Waiter();
        Http.post("http://" + TestConstants.HTTP.HOST + "/post").entity("Value is 2").contentType("text/plain").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertEquals("Status", 200, response.statusCode());
                JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
                JsonElement data = content.get("data");
                assertNotNull("Data", data);
                assertEquals("Data Value", "Value is 2", data.getAsString());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testGetWithCookies() throws Exception {
        final Waiter waiter = new Waiter();
        Http.get(new URL("http://" + TestConstants.HTTP.HOST + "/cookies")).cookie("Session", "abcd1234").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                JsonObject content = parser.parse(response.responseString()).getAsJsonObject();
                JsonObject cookies = content.getAsJsonObject("cookies");
                JsonElement cookie = cookies.get("Session");
                assertNotNull("Cookies", cookie);
                assertEquals("Cookie Value", "abcd1234", cookie.getAsString());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testBasicAuthentication() throws Exception {
        final Waiter waiter = new Waiter();
        Http.get("http://" + TestConstants.HTTP.HOST + "/basic-auth/test/test").basicAuth("test", "test").execute(new HttpResponseCallback() {
            @Override
            public void receivedResponse(HttpResponse response) {
                assertNotNull(response);
                assertEquals("Status", 200, response.statusCode());
                waiter.resume();
            }
        });
        try {
            waiter.await(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
