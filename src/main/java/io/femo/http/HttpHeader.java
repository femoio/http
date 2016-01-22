package io.femo.http;

/**
 * Created by felix on 9/10/15.
 */
public class HttpHeader {

    private String name;
    private String value;

    public HttpHeader(String name, String value) {
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
