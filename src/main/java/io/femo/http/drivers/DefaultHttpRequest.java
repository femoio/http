package io.femo.http.drivers;

import io.femo.http.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by felix on 9/11/15.
 */
public class DefaultHttpRequest extends HttpRequest {

    private String method;
    private Map<String, HttpCookie> cookies;
    private Map<String, HttpHeader> headers;
    private byte[] entity;
    private URL url;
    private HttpResponse response;
    private Map<String, byte[]> data;

    public DefaultHttpRequest(URL url) {
        this.url = url;
        this.cookies = new HashMap<>();
        this.headers = new HashMap<>();
        header("Connection", "close");
        header("User-Agent", "FeMoIO HTTP/0.1");
        header("Host", url.getHost());
    }

    @Override
    public HttpRequest method(String method) {
        this.method = method;
        return this;
    }

    @Override
    public HttpRequest cookie(String name, String value) {
        cookies.put(name, new HttpCookie(name, value));
        return this;
    }

    @Override
    public HttpRequest header(String name, String value) {
        headers.put(name, new HttpHeader(name, value));
        return this;
    }

    @Override
    public HttpRequest entity(byte[] entity) {
        header("Content-Length", entity.length + "");
        this.entity = entity;
        return this;
    }

    @Override
    public HttpRequest entity(String entity) {
        return entity(entity.getBytes());
    }

    @Override
    public HttpRequest entity(Object entity) {
        return entity(String.valueOf(entity));
    }

    @Override
    public HttpRequest basicAuth(String username, String password) {
        String auth = username + ":" + password;
        header("Authorization", "Basic " + DatatypeConverter.printBase64Binary(auth.getBytes()));
        return this;
    }

    @Override
    public HttpRequest execute(HttpResponseCallback callback) {
        int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        DefaultHttpResponse response;
        try {
            Socket socket = new Socket(url.getHost(), port);
            PrintStream output = new PrintStream(socket.getOutputStream());
            print(output);
            response = DefaultHttpResponse.read(socket.getInputStream());
            socket.close();
        } catch (IOException e) {
            throw new HttpException(this, e);
        }
        if(callback != null)
            callback.receivedResponse(response);
        this.response = response;
        return this;
    }

    @Override
    public HttpRequest transport(Transport transport) {
        return this;
    }

    @Override
    public HttpRequest version(HttpVersion version) {
        return this;
    }

    @Override
    public HttpRequest print(PrintStream output) {
        if(data != null) {
            if(isHeader("Content-Type")) {
                String contentType = headers.get("Content-Type").value();
                if(contentType.equals("application/x-www-form-urlencoded")) {
                    writeUrlFormEncoded();
                }
            } else {
                header("Content-Type", "application/x-www-form-urlencoded");
                writeUrlFormEncoded();
            }
        }
        output.printf("%s %s %s\n", method.toUpperCase(), url.getPath() + (url.getQuery() != null ? "?" + url.getQuery() : ""), "HTTP/1.1");
        for (HttpHeader header : headers.values()) {
            output.printf("%s: %s\n", header.name(), header.value());
        }
        if(cookies.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (HttpCookie cookie : cookies.values()) {
                builder.append(cookie.name());
                builder.append("=");
                builder.append(cookie.value());
                builder.append(";");
            }
            output.printf("%s: %s\n", "Cookie", builder.toString());
            output.println();
        }
        if(entity != null) {
            output.println();
            output.write(entity, 0, entity.length);
            output.println();
        }
        output.println();
        output.println();
        return this;
    }

    private void writeUrlFormEncoded() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                stringBuilder.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(new String(data.get(key)));
            } catch (UnsupportedEncodingException e) {
                throw new HttpException(this, e);
            }
            if(iterator.hasNext()) {
                stringBuilder.append("&");
            }
        }
        entity(stringBuilder.toString());
    }

    @Override
    public HttpRequest data(String key, String value) {
        if(data == null)
            data = new HashMap<>();
        data.put(key, value.getBytes());
        return this;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public HttpCookie[] cookies() {
        return new HttpCookie[0];
    }

    @Override
    public HttpHeader[] headers() {
        return new HttpHeader[0];
    }

    public boolean isHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public byte[] entityBytes() {
        return new byte[0];
    }

    @Override
    public String entityString() {
        return null;
    }

    @Override
    public boolean checkAuth(String username, String password) {
        return false;
    }

    @Override
    public HttpResponse response() {
        if(response == null)
                execute();
        return response;
    }

    protected void response(HttpResponse response) {
        this.response = response;
    }

    public URL url() {
        return url;
    }
}
