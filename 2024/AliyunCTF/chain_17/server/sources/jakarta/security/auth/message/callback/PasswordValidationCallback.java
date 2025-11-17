package jakarta.security.auth.message.callback;

import java.util.Arrays;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PasswordValidationCallback.class */
public class PasswordValidationCallback implements Callback {
    private final Subject subject;
    private final String username;
    private char[] password;
    private boolean result;

    public PasswordValidationCallback(Subject subject, String username, char[] password) {
        this.subject = subject;
        this.username = username;
        this.password = password;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public String getUsername() {
        return this.username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void clearPassword() {
        Arrays.fill(this.password, (char) 0);
        this.password = new char[0];
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return this.result;
    }
}
