package io.femo.http.handlers;

import io.femo.http.*;
import io.femo.http.Http;
import io.femo.http.helper.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by felix on 6/6/16.
 */
public abstract class FileHandler implements HttpHandler {

    private boolean caching;
    private String mimeType;

    private FileHandler(boolean caching, String mimeType) {
        this.caching = caching;
        this.mimeType = mimeType;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        if(Objects.equals(request.method(), Http.GET)) {
            if(!(caching && HttpCacheControl.cacheControl(request, response, 3600, etag()))) {
                response.header("Content-Type", mimeType).entity(fileContent());
            }
            return true;
        } else {
            response.status(StatusCode.METHOD_NOT_ALLOWED)
                    .entity("Method " + request.method().toUpperCase() + " not allowed")
                    .header("Accept", Http.GET);
            return false;
        }
    }

    protected abstract byte[] fileContent() throws HttpHandleException;
    protected abstract String etag() throws HttpHandleException;

    private static class BufferedFileHandler extends FileHandler {

        private File source;
        private byte[] buffer;
        private long lastModified = -1;
        private String etag;

        protected BufferedFileHandler(boolean caching, File source, String mimeType) {
            super(caching, mimeType);
            this.source = source;
        }

        @Override
        protected byte[] fileContent() throws HttpHandleException {
            validateBuffer();
            return buffer;
        }

        @Override
        protected String etag() throws HttpHandleException {
            validateBuffer();
            return etag;
        }

        private void validateBuffer() throws HttpHandleException {
            if(!source.exists()) {
                throw new HttpHandleException(StatusCode.NOT_FOUND, "The server could not find the resource you were looking for");
            }
            if(lastModified < source.lastModified()) {
                lastModified = source.lastModified();
                this.etag = Long.toHexString(lastModified);
                if(source.length() > Integer.MAX_VALUE) {
                    throw new RuntimeException("Resource " + source.getName() + " can not be buffered because it exceeds the maximum buffer size!");
                }
                try {
                    buffer = FileUtils.readFileToByteArray(source);
                } catch (IOException e) {
                    throw new HttpHandleException(StatusCode.INTERNAL_SERVER_ERROR, "Server is not capable to create the requested resource", e);
                }
            }
        }
    }

    public static class ResourceFileHandler extends FileHandler {

        private String resourceName;
        private byte[] buffer;
        private String etag;

        protected ResourceFileHandler(boolean caching, String mimeType, String resourceName) {
            super(caching, mimeType);
            this.resourceName = resourceName;
        }

        @Override
        protected byte[] fileContent() throws HttpHandleException {
            validateBuffer();
            return buffer;
        }

        @Override
        protected String etag() throws HttpHandleException {
            validateBuffer();
            return etag;
        }

        private void validateBuffer() throws HttpHandleException {
            if(buffer == null) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                    DigestInputStream digestInputStream = new DigestInputStream(getClass().getResourceAsStream(resourceName), messageDigest);
                    buffer = IOUtils.toByteArray(digestInputStream);
                    etag = HttpHelper.context().base64().encodeToString(messageDigest.digest());
                } catch (IOException | NoSuchAlgorithmException e) {
                    throw new HttpHandleException(StatusCode.INTERNAL_SERVER_ERROR, "Server is not capable to create the requested resource", e);
                }
            }
        }
    }

    @Contract("_, _, _ -> !null")
    public static FileHandler buffered(File source, boolean caching, String mimeType) {
        return new BufferedFileHandler(caching, source, mimeType);
    }

    @Contract("_, _, _ -> !null")
    public static FileHandler resource(String resourceName, boolean caching, String mimeType) {
        return new ResourceFileHandler(caching, mimeType, resourceName);
    }
}
