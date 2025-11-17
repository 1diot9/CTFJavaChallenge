package ch.qos.logback.core.net;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/LoginAuthenticator.class */
public class LoginAuthenticator extends Authenticator {
    String username;
    String password;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoginAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}
