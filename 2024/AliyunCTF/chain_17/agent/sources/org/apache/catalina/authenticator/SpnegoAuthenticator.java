package org.apache.catalina.authenticator;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.compat.JreVendor;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SpnegoAuthenticator.class */
public class SpnegoAuthenticator extends AuthenticatorBase {
    private static final String AUTH_HEADER_VALUE_NEGOTIATE = "Negotiate";
    private final Log log = LogFactory.getLog((Class<?>) SpnegoAuthenticator.class);
    private String loginConfigName = Constants.DEFAULT_LOGIN_MODULE_NAME;
    private boolean storeDelegatedCredential = true;
    private Pattern noKeepAliveUserAgents = null;
    private boolean applyJava8u40Fix = true;

    public String getLoginConfigName() {
        return this.loginConfigName;
    }

    public void setLoginConfigName(String loginConfigName) {
        this.loginConfigName = loginConfigName;
    }

    public boolean isStoreDelegatedCredential() {
        return this.storeDelegatedCredential;
    }

    public void setStoreDelegatedCredential(boolean storeDelegatedCredential) {
        this.storeDelegatedCredential = storeDelegatedCredential;
    }

    public String getNoKeepAliveUserAgents() {
        Pattern p = this.noKeepAliveUserAgents;
        if (p == null) {
            return null;
        }
        return p.pattern();
    }

    public void setNoKeepAliveUserAgents(String noKeepAliveUserAgents) {
        if (noKeepAliveUserAgents == null || noKeepAliveUserAgents.length() == 0) {
            this.noKeepAliveUserAgents = null;
        } else {
            this.noKeepAliveUserAgents = Pattern.compile(noKeepAliveUserAgents);
        }
    }

    public boolean getApplyJava8u40Fix() {
        return this.applyJava8u40Fix;
    }

    public void setApplyJava8u40Fix(boolean applyJava8u40Fix) {
        this.applyJava8u40Fix = applyJava8u40Fix;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return Constants.SPNEGO_METHOD;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        String krb5Conf = System.getProperty(Constants.KRB5_CONF_PROPERTY);
        if (krb5Conf == null) {
            File krb5ConfFile = new File(this.container.getCatalinaBase(), Constants.DEFAULT_KRB5_CONF);
            System.setProperty(Constants.KRB5_CONF_PROPERTY, krb5ConfFile.getAbsolutePath());
        }
        String jaasConf = System.getProperty(Constants.JAAS_CONF_PROPERTY);
        if (jaasConf == null) {
            File jaasConfFile = new File(this.container.getCatalinaBase(), Constants.DEFAULT_JAAS_CONF);
            System.setProperty(Constants.JAAS_CONF_PROPERTY, jaasConfFile.getAbsolutePath());
        }
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        MessageBytes ua;
        if (checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.noAuthHeader"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        authorization.toBytes();
        ByteChunk authorizationBC = authorization.getByteChunk();
        if (!authorizationBC.startsWithIgnoreCase("negotiate ", 0)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("spnegoAuthenticator.authHeaderNotNego"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        authorizationBC.setOffset(authorizationBC.getOffset() + 10);
        byte[] decoded = Base64.decodeBase64(authorizationBC.getBuffer(), authorizationBC.getOffset(), authorizationBC.getLength());
        if (getApplyJava8u40Fix()) {
            SpnegoTokenFixer.fix(decoded);
        }
        if (decoded.length == 0) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("spnegoAuthenticator.authHeaderNoToken"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        LoginContext lc = null;
        GSSContext gssContext = null;
        try {
            try {
                try {
                    lc = new LoginContext(getLoginConfigName());
                    lc.login();
                    Subject subject = lc.getSubject();
                    GSSManager manager = GSSManager.getInstance();
                    int credentialLifetime = JreVendor.IS_IBM_JVM ? Integer.MAX_VALUE : 0;
                    int i = credentialLifetime;
                    PrivilegedExceptionAction<GSSCredential> action = () -> {
                        return manager.createCredential((GSSName) null, i, new Oid("1.3.6.1.5.5.2"), 2);
                    };
                    GSSContext gssContext2 = manager.createContext((GSSCredential) Subject.doAs(subject, action));
                    byte[] outToken = (byte[]) Subject.doAs(lc.getSubject(), new AcceptAction(gssContext2, decoded));
                    if (outToken == null) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(sm.getString("spnegoAuthenticator.ticketValidateFail"));
                        }
                        response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
                        response.sendError(401);
                        if (gssContext2 != null) {
                            try {
                                gssContext2.dispose();
                            } catch (GSSException e) {
                            }
                        }
                        if (lc != null) {
                            try {
                                lc.logout();
                            } catch (LoginException e2) {
                            }
                        }
                        return false;
                    }
                    Principal principal = (Principal) Subject.doAs(subject, new AuthenticateAction(this.context.getRealm(), gssContext2, this.storeDelegatedCredential));
                    if (gssContext2 != null) {
                        try {
                            gssContext2.dispose();
                        } catch (GSSException e3) {
                        }
                    }
                    if (lc != null) {
                        try {
                            lc.logout();
                        } catch (LoginException e4) {
                        }
                    }
                    response.setHeader("WWW-Authenticate", "Negotiate " + Base64.encodeBase64String(outToken));
                    if (principal == null) {
                        response.sendError(401);
                        return false;
                    }
                    register(request, response, principal, Constants.SPNEGO_METHOD, principal.getName(), null);
                    Pattern p = this.noKeepAliveUserAgents;
                    if (p == null || (ua = request.getCoyoteRequest().getMimeHeaders().getValue("user-agent")) == null || !p.matcher(ua.toString()).matches()) {
                        return true;
                    }
                    response.setHeader("Connection", org.apache.coyote.http11.Constants.CLOSE);
                    return true;
                } catch (GSSException e5) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("spnegoAuthenticator.ticketValidateFail"), e5);
                    }
                    response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
                    response.sendError(401);
                    if (0 != 0) {
                        try {
                            gssContext.dispose();
                        } catch (GSSException e6) {
                        }
                    }
                    if (0 != 0) {
                        try {
                            lc.logout();
                        } catch (LoginException e7) {
                        }
                    }
                    return false;
                } catch (PrivilegedActionException e8) {
                    Throwable cause = e8.getCause();
                    if (!(cause instanceof GSSException)) {
                        this.log.error(sm.getString("spnegoAuthenticator.serviceLoginFail"), e8);
                    } else if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("spnegoAuthenticator.serviceLoginFail"), e8);
                    }
                    response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
                    response.sendError(401);
                    if (0 != 0) {
                        try {
                            gssContext.dispose();
                        } catch (GSSException e9) {
                        }
                    }
                    if (0 != 0) {
                        try {
                            lc.logout();
                        } catch (LoginException e10) {
                        }
                    }
                    return false;
                }
            } catch (LoginException e11) {
                this.log.error(sm.getString("spnegoAuthenticator.serviceLoginFail"), e11);
                response.sendError(500);
                if (0 != 0) {
                    try {
                        gssContext.dispose();
                    } catch (GSSException e12) {
                    }
                }
                if (lc != null) {
                    try {
                        lc.logout();
                    } catch (LoginException e13) {
                    }
                }
                return false;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    gssContext.dispose();
                } catch (GSSException e14) {
                }
            }
            if (0 != 0) {
                try {
                    lc.logout();
                } catch (LoginException e15) {
                }
            }
            throw th;
        }
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean isPreemptiveAuthPossible(Request request) {
        MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("negotiate ", 0);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SpnegoAuthenticator$AcceptAction.class */
    public static class AcceptAction implements PrivilegedExceptionAction<byte[]> {
        GSSContext gssContext;
        byte[] decoded;

        public AcceptAction(GSSContext context, byte[] decodedToken) {
            this.gssContext = context;
            this.decoded = decodedToken;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public byte[] run() throws GSSException {
            return this.gssContext.acceptSecContext(this.decoded, 0, this.decoded.length);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SpnegoAuthenticator$AuthenticateAction.class */
    public static class AuthenticateAction implements PrivilegedAction<Principal> {
        private final Realm realm;
        private final GSSContext gssContext;
        private final boolean storeDelegatedCredential;

        public AuthenticateAction(Realm realm, GSSContext gssContext, boolean storeDelegatedCredential) {
            this.realm = realm;
            this.gssContext = gssContext;
            this.storeDelegatedCredential = storeDelegatedCredential;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Principal run() {
            return this.realm.authenticate(this.gssContext, this.storeDelegatedCredential);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SpnegoAuthenticator$SpnegoTokenFixer.class */
    public static class SpnegoTokenFixer {
        private final byte[] token;
        private int pos = 0;

        public static void fix(byte[] token) {
            SpnegoTokenFixer fixer = new SpnegoTokenFixer(token);
            fixer.fix();
        }

        private SpnegoTokenFixer(byte[] token) {
            this.token = token;
        }

        private void fix() {
            if (!tag(96) || !length() || !oid("1.3.6.1.5.5.2") || !tag(160) || !length() || !tag(48) || !length() || !tag(160)) {
                return;
            }
            lengthAsInt();
            if (!tag(48)) {
                return;
            }
            int mechTypesLen = lengthAsInt();
            int mechTypesStart = this.pos;
            LinkedHashMap<String, int[]> mechTypeEntries = new LinkedHashMap<>();
            while (this.pos < mechTypesStart + mechTypesLen) {
                String key = oidAsString();
                int[] value = {this.pos, this.pos - value[0]};
                mechTypeEntries.put(key, value);
            }
            byte[] replacement = new byte[mechTypesLen];
            int replacementPos = 0;
            int[] first = mechTypeEntries.remove("1.2.840.113554.1.2.2");
            if (first != null) {
                System.arraycopy(this.token, first[0], replacement, 0, first[1]);
                replacementPos = 0 + first[1];
            }
            for (int[] markers : mechTypeEntries.values()) {
                System.arraycopy(this.token, markers[0], replacement, replacementPos, markers[1]);
                replacementPos += markers[1];
            }
            System.arraycopy(replacement, 0, this.token, mechTypesStart, mechTypesLen);
        }

        private boolean tag(int expected) {
            byte[] bArr = this.token;
            int i = this.pos;
            this.pos = i + 1;
            return (bArr[i] & 255) == expected;
        }

        private boolean length() {
            int len = lengthAsInt();
            return this.pos + len == this.token.length;
        }

        private int lengthAsInt() {
            byte[] bArr = this.token;
            int i = this.pos;
            this.pos = i + 1;
            int len = bArr[i] & 255;
            if (len > 127) {
                int bytes = len - 128;
                len = 0;
                for (int i2 = 0; i2 < bytes; i2++) {
                    byte[] bArr2 = this.token;
                    int i3 = this.pos;
                    this.pos = i3 + 1;
                    len = (len << 8) + (bArr2[i3] & 255);
                }
            }
            return len;
        }

        private boolean oid(String expected) {
            return expected.equals(oidAsString());
        }

        private String oidAsString() {
            if (!tag(6)) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            int len = lengthAsInt();
            byte[] bArr = this.token;
            int i = this.pos;
            this.pos = i + 1;
            int v = bArr[i] & 255;
            int c2 = v % 40;
            int c1 = (v - c2) / 40;
            result.append(c1);
            result.append('.');
            result.append(c2);
            int c = 0;
            boolean write = false;
            for (int i2 = 1; i2 < len; i2++) {
                byte[] bArr2 = this.token;
                int i3 = this.pos;
                this.pos = i3 + 1;
                int b = bArr2[i3] & 255;
                if (b > 127) {
                    b -= 128;
                } else {
                    write = true;
                }
                c = (c << 7) + b;
                if (write) {
                    result.append('.');
                    result.append(c);
                    c = 0;
                    write = false;
                }
            }
            return result.toString();
        }
    }
}
