package jakarta.security.auth.message.callback;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import javax.security.auth.callback.Callback;
import javax.security.auth.x500.X500Principal;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback.class */
public class PrivateKeyCallback implements Callback {
    private final Request request;
    private Certificate[] chain;
    private PrivateKey key;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback$Request.class */
    public interface Request {
    }

    public PrivateKeyCallback(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setKey(PrivateKey key, Certificate[] chain) {
        this.key = key;
        this.chain = chain;
    }

    public PrivateKey getKey() {
        return this.key;
    }

    public Certificate[] getChain() {
        return this.chain;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback$AliasRequest.class */
    public static class AliasRequest implements Request {
        private final String alias;

        public AliasRequest(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return this.alias;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback$DigestRequest.class */
    public static class DigestRequest implements Request {
        private final byte[] digest;
        private final String algorithm;

        public DigestRequest(byte[] digest, String algorithm) {
            this.digest = digest;
            this.algorithm = algorithm;
        }

        public byte[] getDigest() {
            return this.digest;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback$SubjectKeyIDRequest.class */
    public static class SubjectKeyIDRequest implements Request {
        private final byte[] subjectKeyID;

        public SubjectKeyIDRequest(byte[] subjectKeyID) {
            this.subjectKeyID = subjectKeyID;
        }

        public byte[] getSubjectKeyID() {
            return this.subjectKeyID;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/PrivateKeyCallback$IssuerSerialNumRequest.class */
    public static class IssuerSerialNumRequest implements Request {
        private final X500Principal issuer;
        private final BigInteger serialNum;

        public IssuerSerialNumRequest(X500Principal issuer, BigInteger serialNum) {
            this.issuer = issuer;
            this.serialNum = serialNum;
        }

        public X500Principal getIssuer() {
            return this.issuer;
        }

        public BigInteger getSerialNum() {
            return this.serialNum;
        }
    }
}
