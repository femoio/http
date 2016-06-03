package io.femo.http.drivers;

import io.femo.http.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by felix on 9/11/15.
 */
public class DefaultHttpResponse extends HttpResponse {

    private static Logger log = LoggerFactory.getLogger("HTTP");

    private StatusCode statusCode;
    private Map<String, HttpHeader> headers;
    private Map<String, HttpCookie> cookies;
    private byte[] entity;

    private InputStream entityStream;

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
        header("Content-Length", String.valueOf(entity.length));
        if(!hasHeader("Content-Type")) {
            header("Content-Type", "text/plain");
        }
        if(statusCode == null) {
            statusCode = StatusCode.OK;
        }
        this.entity = entity;
        return this;
    }

    @Override
    public HttpResponse entity(InputStream inputStream) {
        if(!hasHeader("Content-Type")) {
            header("Content-Type", "text/plain");
        }
        if(statusCode == null) {
            statusCode = StatusCode.OK;
        }
        this.entityStream = inputStream;
        return this;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public HttpResponse header(String name, String value) {
        this.headers.put(name, new HttpHeader(name, value));
        return this;
    }

    @Override
    public HttpCookie cookie(String name) {
        return cookies.get(name);
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }

    @Override
    public HttpResponse cookie(String name, String value) {
        this.cookies.put(name, new HttpCookie(name, value));
        return this;
    }

    @Override
    public Collection<HttpCookie> cookies() {
        return cookies.values();
    }

    @Override
    public void print(OutputStream outputStream) {
        PrintStream stream = new PrintStream(outputStream);
        if(statusCode == null) {
            statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        }
        stream.printf("HTTP/1.1 %03d %s\r\n", statusCode.status(), statusCode.statusMessage());
        for (HttpHeader header : headers.values()) {
            stream.printf("%s: %s\r\n", header.name(), header.value());
        }
        for (HttpCookie cookie : cookies.values()) {
            stream.printf("Set-Cookie: %s\r\n", cookie.value());
        }
        stream.print("\r\n");
        if(entityStream != null) {
            byte[] buffer = new byte[1024];
            int read;
            try {
                while ((read = entityStream.read(buffer, 0, 1024)) > 0) {
                    stream.write(buffer, 0, read);
                }
                entityStream.close();
            } catch (IOException e) {
                log.error("Could not read entity stream", e);
            }
        } else if (entity != null && entity.length > 0) {
            stream.write(entity, 0, entity.length);
            stream.print("\r\n");
        }
        stream.print("\r\n");
    }

    public String statusLine() {
        return status().status() + " " + status().statusMessage();
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

    public static DefaultHttpResponse read(InputStream inputStream, OutputStream pipe) throws IOException {
        InputBuffer inputBuffer = new InputBuffer(inputStream);
        String statusLine = inputBuffer.readUntil((byte) '\r', 1);
        DefaultHttpResponse response = new DefaultHttpResponse();
        response.statusCode = StatusCode.constructFromHttpStatusLine(statusLine);
        statusLine = inputBuffer.readUntil((byte) '\r', 1);
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
            statusLine = readLine(inputStream);
        }
        if(response.hasHeader("Content-Length")) {
            int length = response.header("Content-Length").asInt();
            if(pipe != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                inputBuffer.pipe(length, pipe, byteArrayOutputStream);
                pipe.close();
                response.entity(byteArrayOutputStream.toByteArray());
            } else {
                response.entity(inputBuffer.get(length));
            }
        } else {
            log.debug("No content-length received. Treating entity as non existent!");
            /*byte buffer[] = new byte[1024];
            int read;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((read = inputStream.read(buffer)) != 0) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            response.entity = byteArrayOutputStream.toByteArray();*/
        }
        return response;
    }

    @NotNull
    private static String readLine(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        log.debug("Reading line for HTTP head");
        int read;
        try {
            while (/*isAvailable(inputStream) && */(read = inputStream.read()) != 0) {
                if(read == '\r') {
                    read = inputStream.read();
                    if(read == '\n') {
                        break;
                    } else {
                        continue;
                    }
                }
                if(read == '\n') {
                    log.warn("Received possibly malformed HTTP Response");
                    break;
                }
                byteArrayOutputStream.write(read);
            }
        } catch (IOException e) {
            log.warn("Exception while reading line from input", e);
        }
        log.debug("Read: " + byteArrayOutputStream.toString());
        return byteArrayOutputStream.toString().trim();
    }

    private static boolean isAvailable(InputStream inputStream) throws IOException {
        try {
            int timeout = 0;
            while (inputStream.available() == 0) {
                Thread.sleep(10);
                timeout++;
                if(timeout == 300) {
                    log.warn("Reached timeout of 3000 ms");
                    return false;
                }
            }
            if(timeout != 0)
                log.debug("Time until bytes available: {}", timeout * 10);
        } catch (InterruptedException e) {
            log.warn("Error while waiting for stream to become ready!", e);
            return false;
        }
        return true;
    }
}
