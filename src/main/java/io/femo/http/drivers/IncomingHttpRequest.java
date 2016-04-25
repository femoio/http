package io.femo.http.drivers;

import io.femo.http.HttpCookie;
import io.femo.http.HttpHeader;
import io.femo.http.HttpRequest;
import io.femo.http.StatusCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by felix on 2/25/16.
 */
public class IncomingHttpRequest extends DefaultHttpRequest {

    private String path;

    private IncomingHttpRequest() {
        super(null);
    }

    @Override
    public String path() {
        return path;
    }


    public static HttpRequest readFromStream(InputStream inputStream) throws IOException {
        /*BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        String requestLine = readLine(bufferedInputStream);
        IncomingHttpRequest request = new IncomingHttpRequest();
        String[] parts =
        statusLine = readLine(bufferedInputStream);
        while (statusLine != null) {
            if(statusLine.equals(""))
                break;
            String name;
            String value;
            name = statusLine.substring(0, statusLine.indexOf(":")).trim();
            value = statusLine.substring(statusLine.indexOf(":") + 1).trim();
            if(name.equals("Cookie")) {
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
        return response;*/
        return null;
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
