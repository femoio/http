package io.femo.http.handlers.auth;

import io.femo.http.HttpRequest;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created by felix on 6/13/16.
 */
public class DigestStrategy implements Strategy {

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

    private String realm;
    private NonceManager nonceManager;
    private CredentialProvider credentialProvider;

    public DigestStrategy(String realm, CredentialProvider credentialProvider, NonceManager nonceManager) {
        this.realm = realm;
        this.nonceManager = nonceManager;
        this.credentialProvider = credentialProvider;
    }

    @Override
    public boolean authenticate(HttpRequest request) {
        if (!request.hasHeader("Authorization")) {
            return false;
        }
        String authorization = request.header("Authorization").value();
        if (!authorization.startsWith(name())) {
            return false;
        }
        String authData = authorization.substring(authorization.indexOf(" ") + 1);
        Properties properties = readData(authData);
        String user = removeQuotes(properties.getProperty("username"));
        CredentialProvider.Credentials credentials = credentialProvider.findByUsername(user);
        if(credentials == null) {
            return false;
        }
        String nonce = removeQuotes(properties.getProperty("nonce"));
        String nc = properties.getProperty("nc");
        if(!nonceManager.verifyAndUpdate(nonce, nc)) {
            return false;
        }
        String ha1 = md5(user + ":" +
                removeQuotes(properties.getProperty("realm")) + ":" +
                credentials.getPassword());
        String ha2 = md5(request.method() + ":" + request.path());
        String response = md5(ha1 + ":" + removeQuotes(properties.getProperty("nonce"))
                + ":" +  properties.getProperty("nc") + ":" + removeQuotes(properties.getProperty("cnonce")) +
                ":" + properties.getProperty("qop") + ":" + ha2);
        return response.equals(removeQuotes(properties.getProperty("response")));
    }

    @Override
    public String name() {
        return "Digest";
    }

    @Override
    public String realm() {
        return realm;
    }

    @Override
    public String authenticateHeader() {
        String nonce;
        return name() +
                " realm=\"" +
                realm +
                "\", qop=\"auth\", nonce=\"" +
                (nonce = nonceManager.generateNew()) +
                "\", opaque=\"" +
                nonceManager.getOpaque(nonce) +
                "\"";
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
