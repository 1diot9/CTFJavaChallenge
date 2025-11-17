package org.apache.catalina.realm;

import jakarta.servlet.annotation.ServletSecurity;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/RealmBase.class */
public abstract class RealmBase extends LifecycleMBeanBase implements Realm {
    protected static final String USER_ATTRIBUTES_DELIMITER = ",";
    protected static final String USER_ATTRIBUTES_WILDCARD = "*";
    private CredentialHandler credentialHandler;
    protected static final StringManager sm;
    protected String x509UsernameRetrieverClassName;
    protected X509UsernameRetriever x509UsernameRetriever;
    private static final Log log = LogFactory.getLog((Class<?>) RealmBase.class);
    private static final List<Class<? extends DigestCredentialHandlerBase>> credentialHandlerClasses = new ArrayList();
    protected Container container = null;
    protected Log containerLog = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected boolean validate = true;
    protected AllRolesMode allRolesMode = AllRolesMode.STRICT_MODE;
    protected boolean stripRealmForGss = true;
    private int transportGuaranteeRedirectStatus = 302;
    protected String userAttributes = null;
    protected List<String> userAttributesList = null;
    protected String realmPath = "/realm0";

    protected abstract String getPassword(String str);

    protected abstract Principal getPrincipal(String str);

    static {
        credentialHandlerClasses.add(MessageDigestCredentialHandler.class);
        credentialHandlerClasses.add(SecretKeyCredentialHandler.class);
        sm = StringManager.getManager((Class<?>) RealmBase.class);
    }

    public int getTransportGuaranteeRedirectStatus() {
        return this.transportGuaranteeRedirectStatus;
    }

    public void setTransportGuaranteeRedirectStatus(int transportGuaranteeRedirectStatus) {
        this.transportGuaranteeRedirectStatus = transportGuaranteeRedirectStatus;
    }

    @Override // org.apache.catalina.Realm
    public CredentialHandler getCredentialHandler() {
        return this.credentialHandler;
    }

    public void setCredentialHandler(CredentialHandler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }

    @Override // org.apache.catalina.Contained
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }

    public String getAllRolesMode() {
        return this.allRolesMode.toString();
    }

    public void setAllRolesMode(String allRolesMode) {
        this.allRolesMode = AllRolesMode.toMode(allRolesMode);
    }

    public boolean getValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getX509UsernameRetrieverClassName() {
        return this.x509UsernameRetrieverClassName;
    }

    public void setX509UsernameRetrieverClassName(String className) {
        this.x509UsernameRetrieverClassName = className;
    }

    public boolean isStripRealmForGss() {
        return this.stripRealmForGss;
    }

    public void setStripRealmForGss(boolean stripRealmForGss) {
        this.stripRealmForGss = stripRealmForGss;
    }

    public String getUserAttributes() {
        return this.userAttributes;
    }

    public void setUserAttributes(String userAttributes) {
        this.userAttributes = userAttributes;
    }

    @Override // org.apache.catalina.Realm
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public Principal authenticate(String username) {
        if (username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("realmBase.authenticateSuccess", username));
        }
        return getPrincipal(username);
    }

    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
                return null;
            }
            return null;
        }
        String serverCredentials = getPassword(username);
        if (serverCredentials == null) {
            getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, serverCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateSuccess", username));
            }
            return getPrincipal(username);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
            return null;
        }
        return null;
    }

    @Override // org.apache.catalina.Realm
    @Deprecated
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2) {
        return authenticate(username, clientDigest, nonce, nc, cnonce, qop, realm, digestA2, "MD5");
    }

    /* JADX WARN: Type inference failed for: r1v7, types: [byte[], byte[][]] */
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        String serverDigestValue;
        String digestA1 = getDigest(username, realm, algorithm);
        if (digestA1 == null) {
            return null;
        }
        String digestA12 = digestA1.toLowerCase(Locale.ENGLISH);
        if (qop == null) {
            serverDigestValue = digestA12 + ":" + nonce + ":" + digestA2;
        } else {
            serverDigestValue = digestA12 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + digestA2;
        }
        try {
            byte[] valueBytes = serverDigestValue.getBytes(getDigestCharset());
            String serverDigest = HexUtils.toHexString(ConcurrentMessageDigest.digest(algorithm, new byte[]{valueBytes}));
            if (log.isDebugEnabled()) {
                log.debug("Digest : " + clientDigest + " Username:" + username + " ClientDigest:" + clientDigest + " nonce:" + nonce + " nc:" + nc + " cnonce:" + cnonce + " qop:" + qop + " realm:" + realm + "digestA2:" + digestA2 + " Server digest:" + serverDigest);
            }
            if (serverDigest.equals(clientDigest)) {
                return getPrincipal(username);
            }
            return null;
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(sm.getString("realmBase.invalidDigestEncoding", getDigestEncoding()), uee);
        }
    }

    public Principal authenticate(X509Certificate[] certs) {
        if (certs == null || certs.length < 1) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Authenticating client certificate chain");
        }
        if (this.validate) {
            for (X509Certificate cert : certs) {
                if (log.isDebugEnabled()) {
                    log.debug(" Checking validity for '" + cert.getSubjectX500Principal().toString() + "'");
                }
                try {
                    cert.checkValidity();
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("  Validity exception", e);
                        return null;
                    }
                    return null;
                }
            }
        }
        return getPrincipal(certs[0]);
    }

    public Principal authenticate(GSSContext gssContext, boolean storeCred) {
        if (gssContext.isEstablished()) {
            GSSName gssName = null;
            try {
                gssName = gssContext.getSrcName();
            } catch (GSSException e) {
                log.warn(sm.getString("realmBase.gssNameFail"), e);
            }
            if (gssName != null) {
                GSSCredential gssCredential = null;
                if (storeCred) {
                    if (gssContext.getCredDelegState()) {
                        try {
                            gssCredential = gssContext.getDelegCred();
                        } catch (GSSException e2) {
                            log.warn(sm.getString("realmBase.delegatedCredentialFail", gssName), e2);
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("realmBase.credentialNotDelegated", gssName));
                    }
                }
                return getPrincipal(gssName, gssCredential);
            }
            return null;
        }
        log.error(sm.getString("realmBase.gssContextNotEstablished"));
        return null;
    }

    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        if (gssName == null) {
            return null;
        }
        return getPrincipal(gssName, gssCredential);
    }

    public void backgroundProcess() {
    }

    @Override // org.apache.catalina.Realm
    public SecurityConstraint[] findSecurityConstraints(Request request, Context context) {
        ArrayList<SecurityConstraint> results = null;
        SecurityConstraint[] constraints = context.findConstraints();
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug("  No applicable constraints defined");
                return null;
            }
            return null;
        }
        String uri = request.getRequestPathMB().toString();
        if (uri == null || uri.length() == 0) {
            uri = "/";
        }
        String method = request.getMethod();
        boolean found = false;
        for (int i = 0; i < constraints.length; i++) {
            SecurityCollection[] collections = constraints[i].findCollections();
            if (collections != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method));
                }
                for (SecurityCollection securityCollection : collections) {
                    String[] patterns = securityCollection.findPatterns();
                    if (patterns != null) {
                        for (String pattern : patterns) {
                            if (uri.equals(pattern) || (pattern.length() == 0 && uri.equals("/"))) {
                                found = true;
                                if (securityCollection.findMethod(method)) {
                                    if (results == null) {
                                        results = new ArrayList<>();
                                    }
                                    results.add(constraints[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        int longest = -1;
        for (int i2 = 0; i2 < constraints.length; i2++) {
            SecurityCollection[] collection = constraints[i2].findCollections();
            if (collection != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i2] + "' against " + method + " " + uri + " --> " + constraints[i2].included(uri, method));
                }
                for (SecurityCollection securityCollection2 : collection) {
                    String[] patterns2 = securityCollection2.findPatterns();
                    if (patterns2 != null) {
                        boolean matched = false;
                        int length = -1;
                        for (String pattern2 : patterns2) {
                            if (pattern2.startsWith("/") && pattern2.endsWith("/*") && pattern2.length() >= longest) {
                                if (pattern2.length() == 2) {
                                    matched = true;
                                    length = pattern2.length();
                                } else if (pattern2.regionMatches(0, uri, 0, pattern2.length() - 1) || (pattern2.length() - 2 == uri.length() && pattern2.regionMatches(0, uri, 0, pattern2.length() - 2))) {
                                    matched = true;
                                    length = pattern2.length();
                                }
                            }
                        }
                        if (matched) {
                            if (length > longest) {
                                found = false;
                                if (results != null) {
                                    results.clear();
                                }
                                longest = length;
                            }
                            if (securityCollection2.findMethod(method)) {
                                found = true;
                                if (results == null) {
                                    results = new ArrayList<>();
                                }
                                results.add(constraints[i2]);
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        for (int i3 = 0; i3 < constraints.length; i3++) {
            SecurityCollection[] collection2 = constraints[i3].findCollections();
            if (collection2 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i3] + "' against " + method + " " + uri + " --> " + constraints[i3].included(uri, method));
                }
                boolean matched2 = false;
                int pos = -1;
                for (int j = 0; j < collection2.length; j++) {
                    String[] patterns3 = collection2[j].findPatterns();
                    if (patterns3 != null) {
                        for (int k = 0; k < patterns3.length && !matched2; k++) {
                            String pattern3 = patterns3[k];
                            if (pattern3.startsWith("*.")) {
                                int slash = uri.lastIndexOf(47);
                                int dot = uri.lastIndexOf(46);
                                if (slash >= 0 && dot > slash && dot != uri.length() - 1 && uri.length() - dot == pattern3.length() - 1 && pattern3.regionMatches(1, uri, dot, uri.length() - dot)) {
                                    matched2 = true;
                                    pos = j;
                                }
                            }
                        }
                    }
                }
                if (matched2) {
                    found = true;
                    if (collection2[pos].findMethod(method)) {
                        if (results == null) {
                            results = new ArrayList<>();
                        }
                        results.add(constraints[i3]);
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        for (int i4 = 0; i4 < constraints.length; i4++) {
            SecurityCollection[] collection3 = constraints[i4].findCollections();
            if (collection3 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i4] + "' against " + method + " " + uri + " --> " + constraints[i4].included(uri, method));
                }
                for (SecurityCollection securityCollection3 : collection3) {
                    String[] patterns4 = securityCollection3.findPatterns();
                    if (patterns4 != null) {
                        boolean matched3 = false;
                        int length2 = patterns4.length;
                        int i5 = 0;
                        while (true) {
                            if (i5 >= length2) {
                                break;
                            }
                            if (!patterns4[i5].equals("/")) {
                                i5++;
                            } else {
                                matched3 = true;
                                break;
                            }
                        }
                        if (matched3) {
                            if (results == null) {
                                results = new ArrayList<>();
                            }
                            results.add(constraints[i4]);
                        }
                    }
                }
            }
        }
        if (results == null && log.isDebugEnabled()) {
            log.debug("  No applicable constraint located");
        }
        return resultsToArray(results);
    }

    private SecurityConstraint[] resultsToArray(ArrayList<SecurityConstraint> results) {
        if (results == null || results.size() == 0) {
            return null;
        }
        return (SecurityConstraint[]) results.toArray(new SecurityConstraint[0]);
    }

    @Override // org.apache.catalina.Realm
    public boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] constraints, Context context) throws IOException {
        String[] roles;
        if (constraints == null || constraints.length == 0) {
            return true;
        }
        Principal principal = request.getPrincipal();
        boolean status = false;
        boolean denyfromall = false;
        int length = constraints.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            SecurityConstraint constraint = constraints[i];
            if (constraint.getAllRoles()) {
                roles = request.getContext().findSecurityRoles();
            } else {
                roles = constraint.findAuthRoles();
            }
            if (roles == null) {
                roles = new String[0];
            }
            if (log.isDebugEnabled()) {
                log.debug("  Checking roles " + principal);
            }
            if (constraint.getAuthenticatedUsers() && principal != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Passing all authenticated users");
                }
                status = true;
            } else if (roles.length == 0 && !constraint.getAllRoles() && !constraint.getAuthenticatedUsers()) {
                if (constraint.getAuthConstraint()) {
                    if (log.isDebugEnabled()) {
                        log.debug("No roles");
                    }
                    status = false;
                    denyfromall = true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Passing all access");
                    }
                    status = true;
                }
            } else if (principal == null) {
                if (log.isDebugEnabled()) {
                    log.debug("  No user authenticated, cannot grant access");
                }
            } else {
                for (String role : roles) {
                    if (hasRole(request.getWrapper(), principal, role)) {
                        status = true;
                        if (log.isDebugEnabled()) {
                            log.debug("Role found:  " + role);
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug("No role found:  " + role);
                    }
                }
            }
            i++;
        }
        if (!denyfromall && this.allRolesMode != AllRolesMode.STRICT_MODE && !status && principal != null) {
            if (log.isDebugEnabled()) {
                log.debug("Checking for all roles mode: " + this.allRolesMode);
            }
            int length2 = constraints.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length2) {
                    break;
                }
                if (constraints[i2].getAllRoles()) {
                    if (this.allRolesMode == AllRolesMode.AUTH_ONLY_MODE) {
                        if (log.isDebugEnabled()) {
                            log.debug("Granting access for role-name=*, auth-only");
                        }
                        status = true;
                    } else {
                        String[] roles2 = request.getContext().findSecurityRoles();
                        if (roles2 == null) {
                            roles2 = new String[0];
                        }
                        if (roles2.length == 0 && this.allRolesMode == AllRolesMode.STRICT_AUTH_ONLY_MODE) {
                            if (log.isDebugEnabled()) {
                                log.debug("Granting access for role-name=*, strict auth-only");
                            }
                            status = true;
                        }
                    }
                }
                i2++;
            }
        }
        if (!status) {
            response.sendError(403, sm.getString("realmBase.forbidden"));
        }
        return status;
    }

    public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
        String realRole;
        if (wrapper != null && (realRole = wrapper.findSecurityReference(role)) != null) {
            role = realRole;
        }
        if (principal == null || role == null) {
            return false;
        }
        boolean result = hasRoleInternal(principal, role);
        if (log.isDebugEnabled()) {
            String name = principal.getName();
            if (result) {
                log.debug(sm.getString("realmBase.hasRoleSuccess", name, role));
            } else {
                log.debug(sm.getString("realmBase.hasRoleFailure", name, role));
            }
        }
        return result;
    }

    protected List<String> parseUserAttributes(String userAttributes) {
        if (userAttributes == null) {
            return null;
        }
        List<String> attrs = new ArrayList<>();
        for (String str : userAttributes.split(",")) {
            String name = str.trim();
            if (name.length() != 0) {
                if (name.equals("*")) {
                    return Collections.singletonList("*");
                }
                if (!attrs.contains(name)) {
                    attrs.add(name);
                }
            }
        }
        if (attrs.size() > 0) {
            return attrs;
        }
        return null;
    }

    protected boolean hasRoleInternal(Principal principal, String role) {
        if (!(principal instanceof GenericPrincipal)) {
            return false;
        }
        GenericPrincipal gp = (GenericPrincipal) principal;
        return gp.hasRole(role);
    }

    @Override // org.apache.catalina.Realm
    public boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] constraints) throws IOException {
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug("  No applicable security constraint defined");
                return true;
            }
            return true;
        }
        for (SecurityConstraint constraint : constraints) {
            String userConstraint = constraint.getUserConstraint();
            if (userConstraint == null) {
                if (log.isDebugEnabled()) {
                    log.debug("  No applicable user data constraint defined");
                    return true;
                }
                return true;
            }
            if (userConstraint.equals(ServletSecurity.TransportGuarantee.NONE.name())) {
                if (log.isDebugEnabled()) {
                    log.debug("  User data constraint has no restrictions");
                    return true;
                }
                return true;
            }
        }
        if (request.getRequest().isSecure()) {
            if (log.isDebugEnabled()) {
                log.debug("  User data constraint already satisfied");
                return true;
            }
            return true;
        }
        int redirectPort = request.getConnector().getRedirectPortWithOffset();
        if (redirectPort <= 0) {
            if (log.isDebugEnabled()) {
                log.debug("  SSL redirect is disabled");
            }
            response.sendError(403, request.getRequestURI());
            return false;
        }
        StringBuilder file = new StringBuilder();
        String host = request.getServerName();
        file.append("https").append("://").append(host);
        if (redirectPort != 443) {
            file.append(':').append(redirectPort);
        }
        file.append(request.getRequestURI());
        String requestedSessionId = request.getRequestedSessionId();
        if (requestedSessionId != null && request.isRequestedSessionIdFromURL()) {
            file.append(';');
            file.append(SessionConfig.getSessionUriParamName(request.getContext()));
            file.append('=');
            file.append(requestedSessionId);
        }
        String queryString = request.getQueryString();
        if (queryString != null) {
            file.append('?');
            file.append(queryString);
        }
        if (log.isDebugEnabled()) {
            log.debug("  Redirecting to " + file.toString());
        }
        response.sendRedirect(file.toString(), this.transportGuaranteeRedirectStatus);
        return false;
    }

    @Override // org.apache.catalina.Realm
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.container != null) {
            this.containerLog = this.container.getLogger();
        }
        this.x509UsernameRetriever = createUsernameRetriever(this.x509UsernameRetrieverClassName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        if (this.credentialHandler == null) {
            this.credentialHandler = new MessageDigestCredentialHandler();
        }
        if (this.userAttributes != null) {
            this.userAttributesList = parseUserAttributes(this.userAttributes);
        }
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasMessageDigest(String algorithm) {
        String realmAlgorithm;
        CredentialHandler ch2 = this.credentialHandler;
        if ((ch2 instanceof MessageDigestCredentialHandler) && (realmAlgorithm = ((MessageDigestCredentialHandler) ch2).getAlgorithm()) != null) {
            if (realmAlgorithm.equals(algorithm)) {
                return true;
            }
            log.debug(sm.getString("relamBase.digestMismatch", algorithm, realmAlgorithm));
            return false;
        }
        return false;
    }

    @Deprecated
    protected String getDigest(String username, String realmName) {
        return getDigest(username, realmName, "MD5");
    }

    /* JADX WARN: Type inference failed for: r1v6, types: [byte[], byte[][]] */
    protected String getDigest(String username, String realmName, String algorithm) {
        if (hasMessageDigest(algorithm)) {
            return getPassword(username);
        }
        String digestValue = username + ":" + realmName + ":" + getPassword(username);
        try {
            byte[] valueBytes = digestValue.getBytes(getDigestCharset());
            return HexUtils.toHexString(ConcurrentMessageDigest.digest(algorithm, new byte[]{valueBytes}));
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(sm.getString("realmBase.invalidDigestEncoding", getDigestEncoding()), uee);
        }
    }

    private String getDigestEncoding() {
        CredentialHandler ch2 = this.credentialHandler;
        if (ch2 instanceof MessageDigestCredentialHandler) {
            return ((MessageDigestCredentialHandler) ch2).getEncoding();
        }
        return null;
    }

    private Charset getDigestCharset() throws UnsupportedEncodingException {
        String charset = getDigestEncoding();
        if (charset == null) {
            return StandardCharsets.ISO_8859_1;
        }
        return B2CConverter.getCharset(charset);
    }

    protected Principal getPrincipal(X509Certificate usercert) {
        String username = this.x509UsernameRetriever.getUsername(usercert);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("realmBase.gotX509Username", username));
        }
        return getPrincipal(username);
    }

    protected Principal getPrincipal(GSSName gssName, GSSCredential gssCredential) {
        int i;
        String name = gssName.toString();
        if (isStripRealmForGss() && (i = name.indexOf(64)) > 0) {
            name = name.substring(0, i);
        }
        Principal p = getPrincipal(name);
        if (p instanceof GenericPrincipal) {
            ((GenericPrincipal) p).setGssCredential(gssCredential);
        }
        return p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Server getServer() {
        Service s;
        Container c = this.container;
        if (c instanceof Context) {
            c = c.getParent();
        }
        if (c instanceof Host) {
            c = c.getParent();
        }
        if ((c instanceof Engine) && (s = ((Engine) c).getService()) != null) {
            return s.getServer();
        }
        return null;
    }

    public static void main(String[] args) {
        int saltLength = -1;
        int iterations = -1;
        int keyLength = -1;
        String encoding = Charset.defaultCharset().name();
        String algorithm = null;
        String handlerClassName = null;
        if (args.length == 0) {
            usage();
            return;
        }
        int argIndex = 0;
        while (args.length > argIndex + 2 && args[argIndex].length() == 2 && args[argIndex].charAt(0) == '-') {
            switch (args[argIndex].charAt(1)) {
                case 'a':
                    algorithm = args[argIndex + 1];
                    break;
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'j':
                case 'l':
                case Opcodes.LDIV /* 109 */:
                case Opcodes.FDIV /* 110 */:
                case Opcodes.DDIV /* 111 */:
                case 'p':
                case Opcodes.LREM /* 113 */:
                case Opcodes.FREM /* 114 */:
                default:
                    usage();
                    return;
                case 'e':
                    encoding = args[argIndex + 1];
                    break;
                case 'h':
                    handlerClassName = args[argIndex + 1];
                    break;
                case 'i':
                    iterations = Integer.parseInt(args[argIndex + 1]);
                    break;
                case Opcodes.DMUL /* 107 */:
                    keyLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                case 's':
                    saltLength = Integer.parseInt(args[argIndex + 1]);
                    break;
            }
            argIndex += 2;
        }
        if (algorithm == null && handlerClassName == null) {
            algorithm = "SHA-512";
        }
        CredentialHandler handler = null;
        if (handlerClassName == null) {
            for (Class<? extends DigestCredentialHandlerBase> clazz : credentialHandlerClasses) {
                try {
                    handler = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    if (IntrospectionUtils.setProperty(handler, "algorithm", algorithm)) {
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                Class<?> clazz2 = Class.forName(handlerClassName);
                handler = (DigestCredentialHandlerBase) clazz2.getConstructor(new Class[0]).newInstance(new Object[0]);
                IntrospectionUtils.setProperty(handler, "algorithm", algorithm);
            } catch (ReflectiveOperationException e2) {
                throw new RuntimeException(e2);
            }
        }
        if (handler == null) {
            throw new RuntimeException(new NoSuchAlgorithmException(algorithm));
        }
        IntrospectionUtils.setProperty(handler, "encoding", encoding);
        if (iterations > 0) {
            IntrospectionUtils.setProperty(handler, "iterations", Integer.toString(iterations));
        }
        if (saltLength > -1) {
            IntrospectionUtils.setProperty(handler, "saltLength", Integer.toString(saltLength));
        }
        if (keyLength > 0) {
            IntrospectionUtils.setProperty(handler, "keyLength", Integer.toString(keyLength));
        }
        while (argIndex < args.length) {
            String credential = args[argIndex];
            System.out.print(credential + ":");
            System.out.println(handler.mutate(credential));
            argIndex++;
        }
    }

    private static void usage() {
        System.out.println("Usage: RealmBase [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>");
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getObjectNameKeyProperties() {
        return "type=Realm" + getRealmSuffix() + this.container.getMBeanKeyProperties();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getDomainInternal() {
        return this.container.getDomain();
    }

    public String getRealmPath() {
        return this.realmPath;
    }

    public void setRealmPath(String theRealmPath) {
        this.realmPath = theRealmPath;
    }

    protected String getRealmSuffix() {
        return ",realmPath=" + getRealmPath();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/RealmBase$AllRolesMode.class */
    protected static class AllRolesMode {
        private final String name;
        public static final AllRolesMode STRICT_MODE = new AllRolesMode("strict");
        public static final AllRolesMode AUTH_ONLY_MODE = new AllRolesMode("authOnly");
        public static final AllRolesMode STRICT_AUTH_ONLY_MODE = new AllRolesMode("strictAuthOnly");

        static AllRolesMode toMode(String name) {
            AllRolesMode mode;
            if (name.equalsIgnoreCase(STRICT_MODE.name)) {
                mode = STRICT_MODE;
            } else if (name.equalsIgnoreCase(AUTH_ONLY_MODE.name)) {
                mode = AUTH_ONLY_MODE;
            } else if (name.equalsIgnoreCase(STRICT_AUTH_ONLY_MODE.name)) {
                mode = STRICT_AUTH_ONLY_MODE;
            } else {
                throw new IllegalStateException(RealmBase.sm.getString("realmBase.unknownAllRolesMode", name));
            }
            return mode;
        }

        private AllRolesMode(String name) {
            this.name = name;
        }

        public boolean equals(Object o) {
            boolean equals = false;
            if (o instanceof AllRolesMode) {
                AllRolesMode mode = (AllRolesMode) o;
                equals = this.name.equals(mode.name);
            }
            return equals;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }
    }

    private static X509UsernameRetriever createUsernameRetriever(String className) throws LifecycleException {
        if (null == className || className.trim().isEmpty()) {
            return new X509SubjectDnRetriever();
        }
        try {
            return (X509UsernameRetriever) Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ClassCastException e) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.ClassCastException", className), e);
        } catch (ReflectiveOperationException e2) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.newInstance", className), e2);
        }
    }
}
