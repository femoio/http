package io.femo.http;

/**
 * Created by felix on 6/3/16.
 */
public final class TestConstants {

    public static final class HTTP {

        public static final String HOST = System.getenv("HTTPBIN_HOST") == null ? "httpbin.org" : System.getenv("HTTPBIN_HOST");
    }
}
