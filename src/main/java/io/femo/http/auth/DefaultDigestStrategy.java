package io.femo.http.auth;

import io.femo.http.Authentication;
import io.femo.http.HttpException;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by felix on 6/21/16.
 */
public class DefaultDigestStrategy implements Authentication {

    private Supplier<String> username;
    private Supplier<String> password;

    public DefaultDigestStrategy(Supplier<String> username, Supplier<String> password) {
        this.username = username;
        this.password = password;
        this.nc = new AtomicInteger(1);
    }

    public DefaultDigestStrategy(String username, String password) {
        this(() -> username, () -> password);
    }

    private ThreadLocal<MessageDigest> digest = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private String nonce;
    private AtomicInteger nc;
    private String realm;
    private String opaque;

    private String basePath;
    private String host;

    @Override
    public boolean isInitialized() {
        return nonce != null && realm != null && opaque != null && nc != null;
    }

    @Override
    public boolean matches(HttpRequest request) {
        return host.equalsIgnoreCase(request.header("Host").value()) && request.path().startsWith(basePath);
    }

    @Override
    public boolean supportsMulti() {
        return false;
    }

    @Override
    public boolean supportsDirect() {
        return false;
    }

    @Override
    public void init(HttpResponse response) {
        Properties data = readData(response.header("WWW-Authenticate").value().substring("Digest".length()));
        if(!data.containsKey("nonce") && !data.containsKey("opaque") && !data.containsKey("realm")) {
            throw new HttpException(response.request(), "Not all required fields for HTTP Digest Authentication are present.");
        }
        this.nonce = removeQuotes(data.getProperty("nonce"));
        this.realm = removeQuotes(data.getProperty("realm"));
        this.opaque = removeQuotes(data.getProperty("opaque"));
        this.host = response.request().header("Host").value();
        this.basePath = response.request().path();
    }

    @Override
    public String strategy() {
        return "Digest";
    }

    @Override
    public void authenticate(HttpRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        int nc = this.nc.getAndIncrement();
        String cnonce = "0a4f113b"; //UUID.randomUUID().toString();

        stringBuilder.append("Digest username=\"")
                .append(username.get())
                .append("\", realm=\"")
                .append(realm)
                .append("\", nonce=\"")
                .append(nonce)
                .append("\", uri=\"")
                .append(request.path())
                .append("\", qop=auth, nc=")
                .append(String.format("%08x", nc))
                .append(", cnonce=\"")
                .append(cnonce)
                .append("\", response=\"");

        String ha1 = md5(username.get() + ":" + realm + ":" + password.get());
        String ha2 = md5(request.method() + ":" + request.path());
        String response = md5(ha1 + ":" + nonce
                + ":" +  String.format("%08x", nc) + ":" + cnonce +
                ":auth:" + ha2);

        stringBuilder.append(response)
                .append("\", opaque=\"")
                .append(opaque)
                .append("\"");

        request.header("Authorization", stringBuilder.toString());
    }

    private Properties readData(String authData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(authData.replace(",", "\n").getBytes());
        Properties properties = new Properties();
        try {
            properties.load(byteArrayInputStream);
        } catch (IOException ignored) {
        }
        return properties;
    }

    @NotNull
    private String md5(String data) {
        MessageDigest digest = this.digest.get();
        digest.reset();
        byte[] res = digest.digest(data.getBytes());
        return DatatypeConverter.printHexBinary(res).toLowerCase();
    }

    private String removeQuotes(String string) {
        while (string.startsWith("\"")) {
            string = string.substring(1);
        }
        while (string.endsWith("\"")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }
}
