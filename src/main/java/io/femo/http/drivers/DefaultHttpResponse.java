package io.femo.http.drivers;

import io.femo.http.HttpCookie;
import io.femo.http.HttpHeader;
import io.femo.http.HttpResponse;
import io.femo.http.StatusCode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by felix on 9/11/15.
 */
public class DefaultHttpResponse extends HttpResponse {

    private StatusCode statusCode;
    private Map<String, HttpHeader> headers;
    private Map<String, HttpCookie> cookies;
    private byte[] entity;

    public DefaultHttpResponse() {
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
    }

    @Override
    public HttpResponse status(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public HttpResponse entity(String entity) {
        return entity(entity.getBytes());
    }

    @Override
    public HttpResponse entity(byte[] entity) {
        this.entity = entity;
        return this;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public HttpCookie cookie(String name) {
        return cookies.get(name);
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }

    @Override
    public StatusCode status() {
        return statusCode;
    }

    @Override
    public String responseString() {
        return new String(entity);
    }

    @Override
    public byte[] responseBytes() {
        return entity;
    }

    @Override
    public HttpHeader header(String name) {
        return headers.get(name);
    }

    public static DefaultHttpResponse read(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        String statusLine = readLine(bufferedInputStream);
        DefaultHttpResponse response = new DefaultHttpResponse();
        response.statusCode = StatusCode.constructFromHttpStatusLine(statusLine);
        statusLine = readLine(bufferedInputStream);
        while (statusLine != null) {
            if(statusLine.equals(""))
                break;
            String name;
            String value;
            name = statusLine.substring(0, statusLine.indexOf(":")).trim();
            value = statusLine.substring(statusLine.indexOf(":") + 1).trim();
            if(name.equals("Set-Cookie")) {
                String cname, cvalue;
                cname = value.substring(0, value.indexOf("="));
                cvalue = value.substring(value.indexOf("=") + 1, !value.contains(";") ? value.length(): value.indexOf(";"));
                response.cookies.put(cname, new HttpCookie(cname, cvalue));
            } else {
                response.headers.put(name, new HttpHeader(name, value));
            }
            statusLine = readLine(bufferedInputStream);
        }
        if(response.hasHeader("Content-Length")) {
            int length = Integer.parseInt(response.header("Content-Length").value());
            byte[] entity = new byte[length];
            bufferedInputStream.read(entity, 0, length);
            response.entity = entity;
        } else {
            byte buffer[] = new byte[1024];
            int read;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((read = bufferedInputStream.read(buffer)) != 0) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            response.entity = byteArrayOutputStream.toByteArray();
        }
        return response;
    }

    private static String readLine(BufferedInputStream bufferedInputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = bufferedInputStream.read()) != 0 && read != '\n') {
            byteArrayOutputStream.write(read);
        }
        String line = new String(byteArrayOutputStream.toByteArray()).trim();
        return line;
    }
}
