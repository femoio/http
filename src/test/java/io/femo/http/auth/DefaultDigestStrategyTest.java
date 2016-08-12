package io.femo.http.auth;

import io.femo.http.Http;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.drivers.DefaultHttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by felix on 6/26/16.
 */
public class DefaultDigestStrategyTest {

    @Test
    public void testExample() throws Exception {
        HttpRequest httpRequest = Http.get("http://www.nowhere.org/dir/index.html");
        HttpResponse httpResponse = new DefaultHttpResponse();
        httpResponse.header("WWW-Authenticate", "Digest " +
                "realm=\"testrealm@host.com\", " +
                "qop=\"auth,auth-int\", " +
                "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", " +
                "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");
        httpResponse.status(401);
        httpResponse.request(httpRequest);
        DefaultDigestStrategy defaultDigestStrategy = new DefaultDigestStrategy("Mufasa", "Circle Of Life");
        defaultDigestStrategy.init(httpResponse);
        assertTrue(defaultDigestStrategy.isInitialized());
        assertTrue(defaultDigestStrategy.matches(httpRequest));
        defaultDigestStrategy.authenticate(httpRequest);
        assertEquals(httpRequest.header("Authorization").value(), "Digest username=\"Mufasa\", " +
                "realm=\"testrealm@host.com\", " +
                "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", " +
                "uri=\"/dir/index.html\", " +
                "qop=auth, " +
                "nc=00000001, " +
                "cnonce=\"0a4f113b\", " +
                "response=\"6629fae49393a05397450978507c4ef1\", " +
                "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");
    }
}