package io.femo.http.drivers;

import io.femo.http.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by felix on 2/25/16.
 */
public class IncomingHttpRequest extends DefaultHttpRequest {

    private String path;

    public IncomingHttpRequest() {
        super();
    }

    @Override
    public String path() {
        return path;
    }

    public IncomingHttpRequest path(String path) {
        this.path = path;
        return this;
    }

}
