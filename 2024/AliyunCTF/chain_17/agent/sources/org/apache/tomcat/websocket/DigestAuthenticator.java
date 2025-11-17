package org.apache.tomcat.websocket;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/DigestAuthenticator.class */
public class DigestAuthenticator extends Authenticator {
    public static final String schemeName = "digest";
    private static volatile SecureRandom cnonceGenerator;
    private int nonceCount = 0;
    private long cNonce;
    private static final StringManager sm = StringManager.getManager((Class<?>) DigestAuthenticator.class);
    private static final Object cnonceGeneratorLock = new Object();

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getAuthorization(String requestUri, String authenticateHeader, String userName, String userPassword, String userRealm) throws AuthenticationException {
        validateUsername(userName);
        validatePassword(userPassword);
        Map<String, String> parameterMap = parseAuthenticateHeader(authenticateHeader);
        String realm = parameterMap.get("realm");
        validateRealm(userRealm, realm);
        String nonce = parameterMap.get("nonce");
        String messageQop = parameterMap.get("qop");
        String algorithm = parameterMap.get("algorithm") == null ? "MD5" : parameterMap.get("algorithm");
        String opaque = parameterMap.get("opaque");
        StringBuilder challenge = new StringBuilder();
        if (!messageQop.isEmpty()) {
            if (cnonceGenerator == null) {
                synchronized (cnonceGeneratorLock) {
                    if (cnonceGenerator == null) {
                        cnonceGenerator = new SecureRandom();
                    }
                }
            }
            this.cNonce = cnonceGenerator.nextLong();
            this.nonceCount++;
        }
        challenge.append("Digest ");
        challenge.append("username =\"" + userName + "\",");
        challenge.append("realm=\"" + realm + "\",");
        challenge.append("nonce=\"" + nonce + "\",");
        challenge.append("uri=\"" + requestUri + "\",");
        try {
            challenge.append("response=\"" + calculateRequestDigest(requestUri, userName, userPassword, realm, nonce, messageQop, algorithm) + "\",");
            challenge.append("algorithm=" + algorithm + ",");
            challenge.append("opaque=\"" + opaque + "\",");
            if (!messageQop.isEmpty()) {
                challenge.append("qop=\"" + messageQop + "\"");
                challenge.append(",cnonce=\"" + this.cNonce + "\",");
                challenge.append("nc=" + String.format("%08X", Integer.valueOf(this.nonceCount)));
            }
            return challenge.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException(sm.getString("digestAuthenticator.algorithm", e.getMessage()));
        }
    }

    private String calculateRequestDigest(String requestUri, String userName, String password, String realm, String nonce, String qop, String algorithm) throws NoSuchAlgorithmException {
        String A1;
        boolean session = false;
        if (algorithm.endsWith("-sess")) {
            algorithm = algorithm.substring(0, algorithm.length() - 5);
            session = true;
        }
        StringBuilder preDigest = new StringBuilder();
        if (session) {
            A1 = encode(algorithm, userName + ":" + realm + ":" + password) + ":" + nonce + ":" + this.cNonce;
        } else {
            A1 = userName + ":" + realm + ":" + password;
        }
        String A2 = "GET:" + requestUri;
        preDigest.append(encode(algorithm, A1));
        preDigest.append(':');
        preDigest.append(nonce);
        if (qop.toLowerCase().contains(ResourceRef.AUTH)) {
            preDigest.append(':');
            preDigest.append(String.format("%08X", Integer.valueOf(this.nonceCount)));
            preDigest.append(':');
            preDigest.append(String.valueOf(this.cNonce));
            preDigest.append(':');
            preDigest.append(qop);
        }
        preDigest.append(':');
        preDigest.append(encode(algorithm, A2));
        return encode(algorithm, preDigest.toString());
    }

    private String encode(String algorithm, String value) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = value.getBytes(StandardCharsets.ISO_8859_1);
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] thedigest = md.digest(bytesOfMessage);
        return HexUtils.toHexString(thedigest);
    }

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getSchemeName() {
        return schemeName;
    }
}
