package org.apache.catalina.realm;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/SecretKeyCredentialHandler.class */
public class SecretKeyCredentialHandler extends DigestCredentialHandlerBase {
    private static final Log log = LogFactory.getLog((Class<?>) SecretKeyCredentialHandler.class);
    public static final String DEFAULT_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int DEFAULT_KEY_LENGTH = 160;
    public static final int DEFAULT_ITERATIONS = 20000;
    private SecretKeyFactory secretKeyFactory;
    private int keyLength = 160;

    public SecretKeyCredentialHandler() throws NoSuchAlgorithmException {
        setAlgorithm(DEFAULT_ALGORITHM);
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public String getAlgorithm() {
        return this.secretKeyFactory.getAlgorithm();
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        this.secretKeyFactory = secretKeyFactory;
    }

    public int getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    @Override // org.apache.catalina.CredentialHandler
    public boolean matches(String inputCredentials, String storedCredentials) {
        return matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        return mutate(inputCredentials, salt, iterations, getKeyLength());
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected String mutate(String inputCredentials, byte[] salt, int iterations, int keyLength) {
        try {
            KeySpec spec = new PBEKeySpec(inputCredentials.toCharArray(), salt, iterations, keyLength);
            return HexUtils.toHexString(this.secretKeyFactory.generateSecret(spec).getEncoded());
        } catch (IllegalArgumentException | InvalidKeySpecException e) {
            log.warn(sm.getString("pbeCredentialHandler.invalidKeySpec"), e);
            return null;
        }
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected int getDefaultIterations() {
        return 20000;
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected Log getLog() {
        return log;
    }
}
