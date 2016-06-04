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

    public int asInt() {
        return Integer.parseInt(value());
    }

    public boolean equals(Object o) {
        if(String.valueOf(o).equals(value)) {
            return true;
        }
        if(o != null && o instanceof HttpHeader) {
            if(this == o) {
                return true;
            } else if (this.name.equals(((HttpHeader) o).name) && this.value.equals(((HttpHeader) o).value)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return value();
    }
}
