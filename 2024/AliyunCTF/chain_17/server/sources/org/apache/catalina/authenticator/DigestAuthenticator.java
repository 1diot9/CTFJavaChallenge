package org.apache.catalina.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.parser.Authorization;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/DigestAuthenticator.class */
public class DigestAuthenticator extends AuthenticatorBase {
    protected static final String QOP = "auth";
    private static final String NONCE_DIGEST = "SHA-256";
    protected Map<String, NonceInfo> nonces;
    protected String opaque;
    private static final AuthDigest FALLBACK_DIGEST = AuthDigest.MD5;
    private static final Map<String, AuthDigest> PERMITTED_ALGORITHMS = new HashMap();
    private final Log log = LogFactory.getLog((Class<?>) DigestAuthenticator.class);
    protected long lastTimestamp = 0;
    protected final Object lastTimestampLock = new Object();
    protected int nonceCacheSize = 1000;
    protected int nonceCountWindowSize = 100;
    protected String key = null;
    protected long nonceValidity = 300000;
    protected boolean validateUri = true;
    private List<AuthDigest> algorithms = Arrays.asList(AuthDigest.SHA_256, AuthDigest.MD5);

    /*  JADX ERROR: Failed to decode insn: 0x0024: MOVE_MULTI, method: org.apache.catalina.authenticator.DigestAuthenticator.generateNonce(org.apache.catalina.connector.Request):java.lang.String
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[7]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    protected java.lang.String generateNonce(org.apache.catalina.connector.Request r8) {
        /*
            r7 = this;
            long r0 = java.lang.System.currentTimeMillis()
            r9 = r0
            r0 = r7
            java.lang.Object r0 = r0.lastTimestampLock
            r1 = r0
            r11 = r1
            monitor-enter(r0)
            r0 = r9
            r1 = r7
            long r1 = r1.lastTimestamp
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L1d
            r0 = r7
            r1 = r9
            r0.lastTimestamp = r1
            goto L29
            r0 = r7
            r1 = r0
            long r1 = r1.lastTimestamp
            r2 = 1
            long r1 = r1 + r2
            // decode failed: arraycopy: source index -1 out of bounds for object array[7]
            r0.lastTimestamp = r1
            r9 = r-1
            r0 = r11
            monitor-exit(r0)
            goto L37
            r12 = move-exception
            r0 = r11
            monitor-exit(r0)
            r0 = r12
            throw r0
            r0 = r8
            java.lang.String r0 = r0.getRemoteAddr()
            r1 = r9
            r2 = r7
            java.lang.String r2 = r2.getKey()
            java.lang.String r0 = r0 + ":" + r1 + ":" + r0
            r11 = r0
            java.lang.String r0 = "SHA-256"
            r1 = 1
            byte[] r1 = new byte[r1]
            r2 = r1
            r3 = 0
            r4 = r11
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.ISO_8859_1
            byte[] r4 = r4.getBytes(r5)
            r2[r3] = r4
            byte[] r0 = org.apache.tomcat.util.security.ConcurrentMessageDigest.digest(r0, r1)
            r12 = r0
            r0 = r9
            r1 = r12
            java.lang.String r1 = org.apache.tomcat.util.buf.HexUtils.toHexString(r1)
            java.lang.String r0 = r0 + ":" + r0
            r13 = r0
            org.apache.catalina.authenticator.DigestAuthenticator$NonceInfo r0 = new org.apache.catalina.authenticator.DigestAuthenticator$NonceInfo
            r1 = r0
            r2 = r9
            r3 = r7
            int r3 = r3.getNonceCountWindowSize()
            r1.<init>(r2, r3)
            r14 = r0
            r0 = r7
            java.util.Map<java.lang.String, org.apache.catalina.authenticator.DigestAuthenticator$NonceInfo> r0 = r0.nonces
            r1 = r0
            r15 = r1
            monitor-enter(r0)
            r0 = r7
            java.util.Map<java.lang.String, org.apache.catalina.authenticator.DigestAuthenticator$NonceInfo> r0 = r0.nonces
            r1 = r13
            r2 = r14
            java.lang.Object r0 = r0.put(r1, r2)
            r0 = r15
            monitor-exit(r0)
            goto L9d
            r16 = move-exception
            r0 = r15
            monitor-exit(r0)
            r0 = r16
            throw r0
            r0 = r13
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.authenticator.DigestAuthenticator.generateNonce(org.apache.catalina.connector.Request):java.lang.String");
    }

    static {
        for (AuthDigest authDigest : AuthDigest.values()) {
            PERMITTED_ALGORITHMS.put(authDigest.getJavaName(), authDigest);
            PERMITTED_ALGORITHMS.put(authDigest.getRfcName(), authDigest);
        }
    }

    public DigestAuthenticator() {
        setCache(false);
    }

    public int getNonceCountWindowSize() {
        return this.nonceCountWindowSize;
    }

    public void setNonceCountWindowSize(int nonceCountWindowSize) {
        this.nonceCountWindowSize = nonceCountWindowSize;
    }

    public int getNonceCacheSize() {
        return this.nonceCacheSize;
    }

    public void setNonceCacheSize(int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNonceValidity() {
        return this.nonceValidity;
    }

    public void setNonceValidity(long nonceValidity) {
        this.nonceValidity = nonceValidity;
    }

    public String getOpaque() {
        return this.opaque;
    }

    public void setOpaque(String opaque) {
        this.opaque = opaque;
    }

    public boolean isValidateUri() {
        return this.validateUri;
    }

    public void setValidateUri(boolean validateUri) {
        this.validateUri = validateUri;
    }

    public String getAlgorithms() {
        StringBuilder result = new StringBuilder();
        StringUtils.join((Iterable) this.algorithms, ',', x -> {
            return x.getRfcName();
        }, result);
        return result.toString();
    }

    public void setAlgorithms(String algorithmsString) {
        String[] algorithmsArray = algorithmsString.split(",");
        List<AuthDigest> algorithms = new ArrayList<>();
        for (String algorithm : algorithmsArray) {
            AuthDigest authDigest = PERMITTED_ALGORITHMS.get(algorithm);
            if (authDigest == null) {
                this.log.warn(sm.getString("digestAuthenticator.invalidAlgorithm", algorithmsString, algorithm));
                return;
            }
            algorithms.add(authDigest);
        }
        initAlgorithms(algorithms);
        this.algorithms = algorithms;
    }

    private void initAlgorithms(List<AuthDigest> algorithms) {
        Iterator<AuthDigest> algorithmIterator = algorithms.iterator();
        while (algorithmIterator.hasNext()) {
            AuthDigest algorithm = algorithmIterator.next();
            try {
                ConcurrentMessageDigest.init(algorithm.getJavaName());
            } catch (NoSuchAlgorithmException e) {
                this.log.warn(sm.getString("digestAuthenticator.unsupportedAlgorithm", algorithms, algorithm.getJavaName()), e);
                algorithmIterator.remove();
            }
        }
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        Principal principal = null;
        String authorization = request.getHeader("authorization");
        DigestInfo digestInfo = new DigestInfo(getOpaque(), getNonceValidity(), getKey(), this.nonces, isValidateUri());
        if (authorization != null && digestInfo.parse(request, authorization)) {
            if (digestInfo.validate(request, this.algorithms)) {
                principal = digestInfo.authenticate(this.context.getRealm());
            }
            if (principal != null && !digestInfo.isNonceStale()) {
                register(request, response, principal, HttpServletRequest.DIGEST_AUTH, digestInfo.getUsername(), null);
                return true;
            }
        }
        String nonce = generateNonce(request);
        setAuthenticateHeader(request, response, nonce, principal != null && digestInfo.isNonceStale());
        response.sendError(401);
        return false;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.DIGEST_AUTH;
    }

    protected static String removeQuotes(String quotedString, boolean quotesRequired) {
        if (quotedString.length() > 0 && quotedString.charAt(0) != '\"' && !quotesRequired) {
            return quotedString;
        }
        if (quotedString.length() > 2) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return "";
    }

    protected static String removeQuotes(String quotedString) {
        return removeQuotes(quotedString, false);
    }

    protected void setAuthenticateHeader(HttpServletRequest request, HttpServletResponse response, String nonce, boolean isNonceStale) {
        String realmName = getRealmName(this.context);
        boolean first = true;
        for (AuthDigest algorithm : this.algorithms) {
            StringBuilder authenticateHeader = new StringBuilder(200);
            authenticateHeader.append("Digest realm=\"");
            authenticateHeader.append(realmName);
            authenticateHeader.append("\", qop=\"");
            authenticateHeader.append("auth");
            authenticateHeader.append("\", nonce=\"");
            authenticateHeader.append(nonce);
            authenticateHeader.append("\", opaque=\"");
            authenticateHeader.append(getOpaque());
            authenticateHeader.append("\"");
            if (isNonceStale) {
                authenticateHeader.append(", stale=true");
            }
            authenticateHeader.append(", algorithm=");
            authenticateHeader.append(algorithm.getRfcName());
            if (first) {
                response.setHeader("WWW-Authenticate", authenticateHeader.toString());
                first = false;
            } else {
                response.addHeader("WWW-Authenticate", authenticateHeader.toString());
            }
        }
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean isPreemptiveAuthPossible(Request request) {
        MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("digest ", 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.authenticator.AuthenticatorBase, org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (getKey() == null) {
            setKey(this.sessionIdGenerator.generateSessionId());
        }
        if (getOpaque() == null) {
            setOpaque(this.sessionIdGenerator.generateSessionId());
        }
        this.nonces = new LinkedHashMap<String, NonceInfo>() { // from class: org.apache.catalina.authenticator.DigestAuthenticator.1
            private static final long serialVersionUID = 1;
            private static final long LOG_SUPPRESS_TIME = 300000;
            private long lastLog = 0;

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, NonceInfo> eldest) {
                long currentTime = System.currentTimeMillis();
                if (size() <= DigestAuthenticator.this.getNonceCacheSize()) {
                    return false;
                }
                if (this.lastLog < currentTime && currentTime - eldest.getValue().getTimestamp() < DigestAuthenticator.this.getNonceValidity()) {
                    DigestAuthenticator.this.log.warn(AuthenticatorBase.sm.getString("digestAuthenticator.cacheRemove"));
                    this.lastLog = currentTime + LOG_SUPPRESS_TIME;
                    return true;
                }
                return true;
            }
        };
        initAlgorithms(this.algorithms);
        try {
            ConcurrentMessageDigest.init(NONCE_DIGEST);
        } catch (NoSuchAlgorithmException e) {
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/DigestAuthenticator$DigestInfo.class */
    public static class DigestInfo {
        private final String opaque;
        private final long nonceValidity;
        private final String key;
        private final Map<String, NonceInfo> nonces;
        private boolean validateUri;
        private String userName = null;
        private String method = null;
        private String uri = null;
        private String response = null;
        private String nonce = null;
        private String nc = null;
        private String cnonce = null;
        private String realmName = null;
        private String qop = null;
        private String opaqueReceived = null;
        private boolean nonceStale = false;
        private AuthDigest algorithm = null;

        public DigestInfo(String opaque, long nonceValidity, String key, Map<String, NonceInfo> nonces, boolean validateUri) {
            this.validateUri = true;
            this.opaque = opaque;
            this.nonceValidity = nonceValidity;
            this.key = key;
            this.nonces = nonces;
            this.validateUri = validateUri;
        }

        public String getUsername() {
            return this.userName;
        }

        public boolean parse(Request request, String authorization) {
            if (authorization == null) {
                return false;
            }
            try {
                Map<String, String> directives = Authorization.parseAuthorizationDigest(new StringReader(authorization));
                if (directives == null) {
                    return false;
                }
                this.method = request.getMethod();
                this.userName = directives.get("username");
                this.realmName = directives.get("realm");
                this.nonce = directives.get("nonce");
                this.nc = directives.get("nc");
                this.cnonce = directives.get("cnonce");
                this.qop = directives.get("qop");
                this.uri = directives.get("uri");
                this.response = directives.get("response");
                this.opaqueReceived = directives.get("opaque");
                this.algorithm = DigestAuthenticator.PERMITTED_ALGORITHMS.get(directives.get("algorithm"));
                if (this.algorithm == null) {
                    this.algorithm = DigestAuthenticator.FALLBACK_DIGEST;
                    return true;
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Deprecated
        public boolean validate(Request request) {
            List<AuthDigest> fallbackList = Arrays.asList(DigestAuthenticator.FALLBACK_DIGEST);
            return validate(request, fallbackList);
        }

        /* JADX WARN: Type inference failed for: r1v17, types: [byte[], byte[][]] */
        public boolean validate(Request request, List<AuthDigest> algorithms) {
            int i;
            NonceInfo info;
            String uriQuery;
            if (this.userName == null || this.realmName == null || this.nonce == null || this.uri == null || this.response == null) {
                return false;
            }
            if (this.validateUri) {
                String query = request.getQueryString();
                if (query == null) {
                    uriQuery = request.getRequestURI();
                } else {
                    uriQuery = request.getRequestURI() + "?" + query;
                }
                if (!this.uri.equals(uriQuery)) {
                    String host = request.getHeader("host");
                    String scheme = request.getScheme();
                    if (host != null && !uriQuery.startsWith(scheme)) {
                        if (!this.uri.equals(scheme + "://" + host + uriQuery)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            String lcRealm = AuthenticatorBase.getRealmName(request.getContext());
            if (!lcRealm.equals(this.realmName) || !this.opaque.equals(this.opaqueReceived) || (i = this.nonce.indexOf(58)) < 0 || i + 1 == this.nonce.length()) {
                return false;
            }
            try {
                long nonceTime = Long.parseLong(this.nonce.substring(0, i));
                String digestclientIpTimeKey = this.nonce.substring(i + 1);
                long currentTime = System.currentTimeMillis();
                if (currentTime - nonceTime > this.nonceValidity) {
                    this.nonceStale = true;
                    synchronized (this.nonces) {
                        this.nonces.remove(this.nonce);
                    }
                }
                String remoteAddr = request.getRemoteAddr();
                String str = this.key;
                String serverIpTimeKey = remoteAddr + ":" + nonceTime + ":" + remoteAddr;
                byte[] buffer = ConcurrentMessageDigest.digest(DigestAuthenticator.NONCE_DIGEST, new byte[]{serverIpTimeKey.getBytes(StandardCharsets.ISO_8859_1)});
                String digestServerIpTimeKey = HexUtils.toHexString(buffer);
                if (!digestServerIpTimeKey.equals(digestclientIpTimeKey)) {
                    return false;
                }
                if (this.qop != null && !"auth".equals(this.qop)) {
                    return false;
                }
                if (this.qop == null) {
                    if (this.cnonce != null || this.nc != null) {
                        return false;
                    }
                } else {
                    if (this.cnonce == null || this.nc == null || this.nc.length() < 6 || this.nc.length() > 8) {
                        return false;
                    }
                    try {
                        long count = Long.parseLong(this.nc, 16);
                        synchronized (this.nonces) {
                            info = this.nonces.get(this.nonce);
                        }
                        if (info == null) {
                            this.nonceStale = true;
                        } else if (!info.nonceCountValid(count)) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                if (!algorithms.contains(this.algorithm)) {
                    return false;
                }
                return true;
            } catch (NumberFormatException e2) {
                return false;
            }
        }

        public boolean isNonceStale() {
            return this.nonceStale;
        }

        /* JADX WARN: Type inference failed for: r1v3, types: [byte[], byte[][]] */
        public Principal authenticate(Realm realm) {
            String a2 = this.method + ":" + this.uri;
            byte[] buffer = ConcurrentMessageDigest.digest(this.algorithm.getJavaName(), new byte[]{a2.getBytes(StandardCharsets.ISO_8859_1)});
            String digestA2 = HexUtils.toHexString(buffer);
            return realm.authenticate(this.userName, this.response, this.nonce, this.nc, this.cnonce, this.qop, this.realmName, digestA2, this.algorithm.getJavaName());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/DigestAuthenticator$NonceInfo.class */
    public static class NonceInfo {
        private final long timestamp;
        private final boolean[] seen;
        private final int offset;
        private int count = 0;

        public NonceInfo(long currentTime, int seenWindowSize) {
            this.timestamp = currentTime;
            this.seen = new boolean[seenWindowSize];
            this.offset = seenWindowSize / 2;
        }

        public synchronized boolean nonceCountValid(long nonceCount) {
            if (this.count - this.offset >= nonceCount || nonceCount > (this.count - this.offset) + this.seen.length) {
                return false;
            }
            int checkIndex = (int) ((nonceCount + this.offset) % this.seen.length);
            if (this.seen[checkIndex]) {
                return false;
            }
            this.seen[checkIndex] = true;
            this.seen[this.count % this.seen.length] = false;
            this.count++;
            return true;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/DigestAuthenticator$AuthDigest.class */
    public enum AuthDigest {
        MD5("MD5", "MD5"),
        SHA_256(DigestAuthenticator.NONCE_DIGEST, DigestAuthenticator.NONCE_DIGEST),
        SHA_512_256("SHA-512/256", "SHA-512-256");

        private final String javaName;
        private final String rfcName;

        AuthDigest(String javaName, String rfcName) {
            this.javaName = javaName;
            this.rfcName = rfcName;
        }

        public String getJavaName() {
            return this.javaName;
        }

        public String getRfcName() {
            return this.rfcName;
        }
    }
}
