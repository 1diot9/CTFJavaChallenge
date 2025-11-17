package jakarta.security.auth.message.callback;

import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/SecretKeyCallback.class */
public class SecretKeyCallback implements Callback {
    private final Request request;
    private SecretKey key;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/SecretKeyCallback$Request.class */
    public interface Request {
    }

    public SecretKeyCallback(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public SecretKey getKey() {
        return this.key;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/SecretKeyCallback$AliasRequest.class */
    public static class AliasRequest implements Request {
        private final String alias;

        public AliasRequest(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return this.alias;
        }
    }
}
