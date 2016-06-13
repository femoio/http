package io.femo.http.handlers.auth;

/**
 * Created by felix on 6/13/16.
 */
public interface NonceManager {
    String generateNew();

    String generateOpaque();

    boolean verifyAndUpdate(String nonce, String nc);
}
