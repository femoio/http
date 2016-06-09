package io.femo.http.transport;

import io.femo.http.*;
import io.femo.http.drivers.DefaultHttpResponse;
import io.femo.http.drivers.IncomingHttpRequest;
import io.femo.http.drivers.InputBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by felix on 6/9/16.
 */
public class Http11Transport implements io.femo.http.HttpTransport {

    private static Logger log = LoggerFactory.getLogger("HTTP");

    @Override
    public void write(HttpRequest httpRequest, OutputStream outputStream) {
        PrintStream output = new PrintStream(outputStream);
        httpRequest.prepareEntity();
        if(httpRequest.url() == null && httpRequest.path() != null) {
            output.printf("%s %s %s\r\n", httpRequest.method().toUpperCase(), httpRequest.path(), "HTTP/1.1");
        } else {
            output.printf("%s %s %s\r\n", httpRequest.method().toUpperCase(), httpRequest.url().getPath() + (httpRequest.url().getQuery() != null ? "?" + httpRequest.url().getQuery() : ""), "HTTP/1.1");
        }
        for (HttpHeader header : httpRequest.headers()) {
            output.printf("%s: %s\r\n", header.name(), header.value());
        }
        if(httpRequest.cookies().size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (HttpCookie cookie : httpRequest.cookies()) {
                builder.append(cookie.name());
                builder.append("=");
                builder.append(cookie.value());
                builder.append(";");
            }
            output.printf("%s: %s\r\n", "Cookie", builder.toString());
            output.print("\r\n");
        }
        if(httpRequest.entityBytes() != null) {
            output.print("\r\n");
            output.write(httpRequest.entityBytes(), 0, httpRequest.entityBytes().length);
            output.print("\r\n");
        }
        output.print("\r\n");
    }



    @Override
    public void write(HttpResponse httpResponse, OutputStream outputStream, InputStream entityStream) {
        PrintStream stream = new PrintStream(outputStream);
        if(httpResponse.status() == null) {
            httpResponse.status(StatusCode.INTERNAL_SERVER_ERROR);
        }
        stream.printf("HTTP/1.1 %03d %s\r\n", httpResponse.statusCode(), httpResponse.status().statusMessage());
        for (HttpHeader header : httpResponse.headers()) {
            stream.printf("%s: %s\r\n", header.name(), header.value());
        }
        for (HttpCookie cookie : httpResponse.cookies()) {
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
        } else if (httpResponse.responseBytes() != null && httpResponse.responseBytes().length > 0) {
            stream.write(httpResponse.responseBytes(), 0, httpResponse.responseBytes().length);
            stream.print("\r\n");
        }
    }

    @Override
    public HttpRequest readRequest(InputStream inputStream) throws IOException {
        InputBuffer inputBuffer = new InputBuffer(inputStream);
        String statusLine = inputBuffer.readUntil((byte) '\r', 1);
        IncomingHttpRequest request = new IncomingHttpRequest();
        parseRequestLine(statusLine, request);
        statusLine = inputBuffer.readUntil((byte) '\r', 1);
        while (!statusLine.equals("")) {
            String name;
            String value;
            name = statusLine.substring(0, statusLine.indexOf(":")).trim();
            value = statusLine.substring(statusLine.indexOf(":") + 1).trim();
            if(name.equals("Cookie")) {
                String cname, cvalue;
                cname = value.substring(0, value.indexOf("="));
                cvalue = value.substring(value.indexOf("=") + 1, !value.contains(";") ? value.length(): value.indexOf(";"));
                request.cookie(cname, cvalue);
            } else {
                request.header(name, value);
            }
            statusLine = inputBuffer.readUntil((byte) '\r', 1);
        }
        if(!request.method().equalsIgnoreCase(Http.GET) && request.hasHeader("Content-Length")) {
            int length = Integer.parseInt(request.header("Content-Length").value());
            if(length == 0) {
                return request;
            }
            request.entity(inputBuffer.get(length));
        }
        return request;
    }

    private static void parseRequestLine(String line, IncomingHttpRequest httpRequest) {
        String method = line.substring(0, line.indexOf(" ")).trim();
        String path = line.substring(line.indexOf(" ") + 1, line.indexOf(" ", line.indexOf(" ") + 1)).trim();
        String version = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1).trim();
        httpRequest.method(method);
        httpRequest.path(path);
        if(version.equals("HTTP/1.1")) {
            httpRequest.version(HttpVersion.HTTP_11);
        } else if (version.equals("HTTP/1.0")) {
            httpRequest.version(HttpVersion.HTTP_1);
        }
    }

    @Override
    public HttpResponse readResponse(InputStream inputStream, OutputStream pipe) throws IOException {
        InputBuffer inputBuffer = new InputBuffer(inputStream);
        String statusLine = inputBuffer.readUntil((byte) '\r', 1);
        DefaultHttpResponse response = new DefaultHttpResponse();
        response.status(StatusCode.constructFromHttpStatusLine(statusLine));
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
                response.cookie(cname, cvalue);
            } else {
                response.header(name, value);
            }
            statusLine = inputBuffer.readUntil((byte) '\r', 1);
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
        }
        return response;
    }
}
