package io.femo.http.handlers.auth;

import io.femo.http.Base64Driver;
import io.femo.http.HttpRequest;
import io.femo.http.helper.HttpHelper;

/**
 * Created by felix on 6/13/16.
 */
public class BasicStrategy extends AbstractStrategy {

    private CredentialProvider credentialProvider;

    public BasicStrategy(String realm, CredentialProvider credentialProvider) {
        super(realm);
        this.credentialProvider = credentialProvider;
    }

    @Override
    public boolean authenticate(HttpRequest request) {
        if (!request.hasHeader("Authorization")) {
            return false;
        }
        Base64Driver base64Driver = HttpHelper.context().base64();
        String authorization = request.header("Authorization").value();
        if (!authorization.startsWith(name())) {
            return false;
        }
        String authData = authorization.substring(authorization.indexOf(" ") + 1);
        authData = new String(base64Driver.decodeFromString(authData));
        String username = authData.substring(0, authData.indexOf(":"));
        String password = authData.substring(authData.indexOf(":") + 1);
        CredentialProvider.Credentials credentials = credentialProvider.findByUsername(username);
        return credentials != null && credentials.getPassword().equals(password);
    }

    @Override
    public String name() {
        return "Basic";
    }
}
