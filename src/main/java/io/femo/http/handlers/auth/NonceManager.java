package io.femo.http.handlers.auth;

/**
 * Created by felix on 6/13/16.
 */
public interface NonceManager {
    String generateNew();

    String getOpaque(String nonce);

    boolean verifyAndUpdate(String nonce, String nc);
}
