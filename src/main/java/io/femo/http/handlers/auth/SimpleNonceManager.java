package io.femo.http.handlers.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by felix on 6/13/16.
 */
public class SimpleNonceManager implements NonceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    public SimpleNonceManager () {
        LOGGER.warn("You are using an unsafe basic implementation of NonceManager. Please use a database backed one in production environments for security reasons.");
    }

    @Override
    public String generateNew() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getOpaque(String nonce) {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean verifyAndUpdate(String nonce, String nc) {
        return true;
    }
}
