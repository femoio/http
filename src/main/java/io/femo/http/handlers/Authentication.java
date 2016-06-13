package io.femo.http.handlers;

import io.femo.http.*;
import io.femo.http.handlers.auth.*;
import org.jetbrains.annotations.Contract;

/**
 * Created by felix on 6/13/16.
 */
public class Authentication implements HttpMiddleware {

    private Strategy strategy;

    public Authentication(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        if(!strategy.authenticate(request)) {
            response.header("WWW-Authenticate", strategy.authenticateHeader());
            throw new HttpHandleException(StatusCode.UNAUTHORIZED, "The requested resource has restricted access!");
        }
    }

    @Contract("_, _ -> !null")
    public static Authentication basic(String realm, CredentialProvider credentialProvider) {
        return new Authentication(new BasicStrategy(realm, credentialProvider));
    }

    @Contract("_, _ -> !null")
    public static Authentication digest(String realm, CredentialProvider credentialProvider) {
        return new Authentication(new DigestStrategy(realm, credentialProvider, new SimpleNonceManager()));
    }

    @Contract("_, _, _ -> !null")
    public static Authentication digest(String realm, CredentialProvider credentialProvider, NonceManager nonceManager) {
        return new Authentication(new DigestStrategy(realm, credentialProvider, nonceManager));
    }
}
