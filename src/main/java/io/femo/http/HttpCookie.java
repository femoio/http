package io.femo.http;

/**
 * Created by felix on 9/10/15.
 */
public class HttpCookie {

    private String name;
    private String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String value() {
        return value;
    }

    public void value(String value) {
        this.value = value;
    }
}
