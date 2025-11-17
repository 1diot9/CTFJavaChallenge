package org.apache.catalina.realm;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/MessageDigestCredentialHandler.class */
public class MessageDigestCredentialHandler extends DigestCredentialHandlerBase {
    private static final Log log = LogFactory.getLog((Class<?>) MessageDigestCredentialHandler.class);
    public static final int DEFAULT_ITERATIONS = 1;
    private Charset encoding = StandardCharsets.UTF_8;
    private String algorithm = null;

    public String getEncoding() {
        return this.encoding.name();
    }

    public void setEncoding(String encodingName) {
        if (encodingName == null) {
            this.encoding = StandardCharsets.UTF_8;
            return;
        }
        try {
            this.encoding = B2CConverter.getCharset(encodingName);
        } catch (UnsupportedEncodingException e) {
            log.error(sm.getString("mdCredentialHandler.unknownEncoding", encodingName, this.encoding.name()));
        }
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        ConcurrentMessageDigest.init(algorithm);
        this.algorithm = algorithm;
    }

    /* JADX WARN: Type inference failed for: r1v17, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r1v3, types: [byte[], byte[][]] */
    @Override // org.apache.catalina.CredentialHandler
    public boolean matches(String inputCredentials, String storedCredentials) {
        if (inputCredentials == null || storedCredentials == null) {
            return false;
        }
        if (getAlgorithm() == null) {
            return DigestCredentialHandlerBase.equals(inputCredentials, storedCredentials, false);
        }
        if (storedCredentials.startsWith("{MD5}") || storedCredentials.startsWith("{SHA}")) {
            String base64ServerDigest = storedCredentials.substring(5);
            String base64UserDigest = Base64.encodeBase64String(ConcurrentMessageDigest.digest(getAlgorithm(), new byte[]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1)}));
            return DigestCredentialHandlerBase.equals(base64UserDigest, base64ServerDigest, false);
        }
        if (storedCredentials.startsWith("{SSHA}")) {
            String serverDigestPlusSalt = storedCredentials.substring(6);
            byte[] serverDigestPlusSaltBytes = Base64.decodeBase64(serverDigestPlusSalt);
            byte[] serverDigestBytes = new byte[20];
            System.arraycopy(serverDigestPlusSaltBytes, 0, serverDigestBytes, 0, 20);
            int saltLength = serverDigestPlusSaltBytes.length - 20;
            byte[] serverSaltBytes = new byte[saltLength];
            System.arraycopy(serverDigestPlusSaltBytes, 20, serverSaltBytes, 0, saltLength);
            byte[] userDigestBytes = ConcurrentMessageDigest.digest(getAlgorithm(), new byte[]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1), serverSaltBytes});
            return Arrays.equals(userDigestBytes, serverDigestBytes);
        }
        if (storedCredentials.indexOf(36) > -1) {
            return matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
        }
        String userDigest = mutate(inputCredentials, null, 1);
        if (userDigest == null) {
            return false;
        }
        return storedCredentials.equalsIgnoreCase(userDigest);
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r2v3, types: [byte[], byte[][]] */
    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        byte[] userDigest;
        if (this.algorithm == null) {
            return inputCredentials;
        }
        byte[] inputCredentialbytes = inputCredentials.getBytes(this.encoding);
        if (salt == null) {
            userDigest = ConcurrentMessageDigest.digest(this.algorithm, iterations, new byte[]{inputCredentialbytes});
        } else {
            userDigest = ConcurrentMessageDigest.digest(this.algorithm, iterations, new byte[]{salt, inputCredentialbytes});
        }
        return HexUtils.toHexString(userDigest);
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected int getDefaultIterations() {
        return 1;
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected Log getLog() {
        return log;
    }
}
