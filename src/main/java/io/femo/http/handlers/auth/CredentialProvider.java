package io.femo.http.handlers.auth;

/**
 * Created by felix on 6/13/16.
 */
public interface CredentialProvider {

    Credentials findByUsername(String username);

    class Credentials {

        private String username;
        private String password;

        public Credentials() {
        }

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
