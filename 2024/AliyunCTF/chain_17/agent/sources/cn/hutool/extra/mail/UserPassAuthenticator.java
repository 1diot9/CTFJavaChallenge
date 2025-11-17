package cn.hutool.extra.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/UserPassAuthenticator.class */
public class UserPassAuthenticator extends Authenticator {
    private final String user;
    private final String pass;

    public UserPassAuthenticator(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.user, this.pass);
    }
}
