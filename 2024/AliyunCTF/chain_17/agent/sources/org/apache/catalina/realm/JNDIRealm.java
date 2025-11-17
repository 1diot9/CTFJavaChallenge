package org.apache.catalina.realm;

import ch.qos.logback.core.testUtil.CoreTestConstants;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.login.LoginContext;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/JNDIRealm.class */
public class JNDIRealm extends RealmBase {
    public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
    protected String alternateURL;
    private String sslSocketFactoryClassName;
    private String cipherSuites;
    private String hostNameVerifierClassName;
    private String sslProtocol;
    protected JNDIConnection singleConnection;
    protected String authentication = null;
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    protected String derefAliases = null;
    protected String protocol = null;
    protected boolean adCompat = false;
    protected String referrals = null;
    protected String userBase = "";
    protected String userSearch = null;
    private boolean userSearchAsUser = false;
    protected boolean userSubtree = false;
    protected String userPassword = null;
    protected String userRoleAttribute = null;
    protected String[] userPatternArray = null;
    protected String userPattern = null;
    protected String roleBase = "";
    protected String userRoleName = null;
    protected String roleName = null;
    protected String roleSearch = null;
    protected boolean roleSubtree = false;
    protected boolean roleNested = false;
    protected boolean roleSearchAsUser = false;
    protected int connectionAttempt = 0;
    protected String commonRole = null;
    protected String connectionTimeout = "5000";
    protected String readTimeout = "5000";
    protected long sizeLimit = 0;
    protected int timeLimit = 0;
    protected boolean useDelegatedCredential = true;
    protected String spnegoDelegationQop = "auth-conf";
    private boolean useStartTls = false;
    private StartTlsResponse tls = null;
    private String[] cipherSuitesArray = null;
    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory sslSocketFactory = null;
    private boolean forceDnHexEscape = false;
    protected final Lock singleConnectionLock = new ReentrantLock();
    protected SynchronizedStack<JNDIConnection> connectionPool = null;
    protected int connectionPoolSize = 1;
    protected boolean useContextClassLoader = true;

    public boolean getForceDnHexEscape() {
        return this.forceDnHexEscape;
    }

    public void setForceDnHexEscape(boolean forceDnHexEscape) {
        this.forceDnHexEscape = forceDnHexEscape;
    }

    public String getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getContextFactory() {
        return this.contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    public String getDerefAliases() {
        return this.derefAliases;
    }

    public void setDerefAliases(String derefAliases) {
        this.derefAliases = derefAliases;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean getAdCompat() {
        return this.adCompat;
    }

    public void setAdCompat(boolean adCompat) {
        this.adCompat = adCompat;
    }

    public String getReferrals() {
        return this.referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public String getUserBase() {
        return this.userBase;
    }

    public void setUserBase(String userBase) {
        this.userBase = userBase;
    }

    public String getUserSearch() {
        return this.userSearch;
    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
        this.singleConnection = create();
    }

    public boolean isUserSearchAsUser() {
        return this.userSearchAsUser;
    }

    public void setUserSearchAsUser(boolean userSearchAsUser) {
        this.userSearchAsUser = userSearchAsUser;
    }

    public boolean getUserSubtree() {
        return this.userSubtree;
    }

    public void setUserSubtree(boolean userSubtree) {
        this.userSubtree = userSubtree;
    }

    public String getUserRoleName() {
        return this.userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public String getRoleBase() {
        return this.roleBase;
    }

    public void setRoleBase(String roleBase) {
        this.roleBase = roleBase;
        this.singleConnection = create();
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleSearch() {
        return this.roleSearch;
    }

    public void setRoleSearch(String roleSearch) {
        this.roleSearch = roleSearch;
        this.singleConnection = create();
    }

    public boolean isRoleSearchAsUser() {
        return this.roleSearchAsUser;
    }

    public void setRoleSearchAsUser(boolean roleSearchAsUser) {
        this.roleSearchAsUser = roleSearchAsUser;
    }

    public boolean getRoleSubtree() {
        return this.roleSubtree;
    }

    public void setRoleSubtree(boolean roleSubtree) {
        this.roleSubtree = roleSubtree;
    }

    public boolean getRoleNested() {
        return this.roleNested;
    }

    public void setRoleNested(boolean roleNested) {
        this.roleNested = roleNested;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRoleAttribute() {
        return this.userRoleAttribute;
    }

    public void setUserRoleAttribute(String userRoleAttribute) {
        this.userRoleAttribute = userRoleAttribute;
    }

    public String getUserPattern() {
        return this.userPattern;
    }

    public void setUserPattern(String userPattern) {
        this.userPattern = userPattern;
        if (userPattern == null) {
            this.userPatternArray = null;
        } else {
            this.userPatternArray = parseUserPatternString(userPattern);
            this.singleConnection = create();
        }
    }

    public String getAlternateURL() {
        return this.alternateURL;
    }

    public void setAlternateURL(String alternateURL) {
        this.alternateURL = alternateURL;
    }

    public String getCommonRole() {
        return this.commonRole;
    }

    public void setCommonRole(String commonRole) {
        this.commonRole = commonRole;
    }

    public String getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(String timeout) {
        this.connectionTimeout = timeout;
    }

    public String getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(String timeout) {
        this.readTimeout = timeout;
    }

    public long getSizeLimit() {
        return this.sizeLimit;
    }

    public void setSizeLimit(long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isUseDelegatedCredential() {
        return this.useDelegatedCredential;
    }

    public void setUseDelegatedCredential(boolean useDelegatedCredential) {
        this.useDelegatedCredential = useDelegatedCredential;
    }

    public String getSpnegoDelegationQop() {
        return this.spnegoDelegationQop;
    }

    public void setSpnegoDelegationQop(String spnegoDelegationQop) {
        this.spnegoDelegationQop = spnegoDelegationQop;
    }

    public boolean getUseStartTls() {
        return this.useStartTls;
    }

    public void setUseStartTls(boolean useStartTls) {
        this.useStartTls = useStartTls;
    }

    private String[] getCipherSuitesArray() {
        if (this.cipherSuites == null || this.cipherSuitesArray != null) {
            return this.cipherSuitesArray;
        }
        if (this.cipherSuites.trim().isEmpty()) {
            this.containerLog.warn(sm.getString("jndiRealm.emptyCipherSuites"));
            this.cipherSuitesArray = null;
        } else {
            this.cipherSuitesArray = this.cipherSuites.trim().split("\\s*,\\s*");
            this.containerLog.debug(sm.getString("jndiRealm.cipherSuites", Arrays.toString(this.cipherSuitesArray)));
        }
        return this.cipherSuitesArray;
    }

    public void setCipherSuites(String suites) {
        this.cipherSuites = suites;
    }

    public int getConnectionPoolSize() {
        return this.connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getHostnameVerifierClassName() {
        if (this.hostnameVerifier == null) {
            return "";
        }
        return this.hostnameVerifier.getClass().getCanonicalName();
    }

    public void setHostnameVerifierClassName(String verifierClassName) {
        if (verifierClassName != null) {
            this.hostNameVerifierClassName = verifierClassName.trim();
        } else {
            this.hostNameVerifierClassName = null;
        }
    }

    public HostnameVerifier getHostnameVerifier() {
        if (this.hostnameVerifier != null) {
            return this.hostnameVerifier;
        }
        if (this.hostNameVerifierClassName == null || this.hostNameVerifierClassName.equals("")) {
            return null;
        }
        try {
            Object o = constructInstance(this.hostNameVerifierClassName);
            if (o instanceof HostnameVerifier) {
                this.hostnameVerifier = (HostnameVerifier) o;
                return this.hostnameVerifier;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", this.hostNameVerifierClassName));
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", this.hostNameVerifierClassName), e);
        }
    }

    public void setSslSocketFactoryClassName(String factoryClassName) {
        this.sslSocketFactoryClassName = factoryClassName;
    }

    public void setSslProtocol(String protocol) {
        this.sslProtocol = protocol;
    }

    private String[] getSupportedSslProtocols() {
        try {
            SSLContext sslContext = SSLContext.getDefault();
            return sslContext.getSupportedSSLParameters().getProtocols();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(sm.getString("jndiRealm.exception"), e);
        }
    }

    private Object constructInstance(String className) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    public void setUseContextClassLoader(boolean useContext) {
        this.useContextClassLoader = useContext;
    }

    public boolean isUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        Principal principal;
        ClassLoader ocl = null;
        Thread currentThread = null;
        JNDIConnection connection = null;
        try {
            try {
                if (!isUseContextClassLoader()) {
                    currentThread = Thread.currentThread();
                    ocl = currentThread.getContextClassLoader();
                    currentThread.setContextClassLoader(getClass().getClassLoader());
                }
                connection = get();
                try {
                    principal = authenticate(connection, username, credentials);
                } catch (NullPointerException | NamingException e) {
                    this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
                    close(connection);
                    closePooledConnections();
                    connection = get();
                    principal = authenticate(connection, username, credentials);
                }
                release(connection);
                Principal principal2 = principal;
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return principal2;
            } catch (Exception e2) {
                this.containerLog.error(sm.getString("jndiRealm.exception"), e2);
                close(connection);
                closePooledConnections();
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug("Returning null principal.");
                }
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return null;
            }
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    public Principal authenticate(JNDIConnection connection, String username, String credentials) throws NamingException {
        if (username == null || username.equals("") || credentials == null || credentials.equals("")) {
            if (!this.containerLog.isDebugEnabled()) {
                return null;
            }
            this.containerLog.debug("username null or empty: returning null principal.");
            return null;
        }
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            if (this.userPatternArray != null) {
                for (int curUserPattern = 0; curUserPattern < this.userPatternArray.length; curUserPattern++) {
                    User user = getUser(connection, username, credentials, curUserPattern);
                    if (user != null) {
                        try {
                            if (checkCredentials(connection.context, user, credentials)) {
                                List<String> roles = getRoles(connection, user);
                                if (this.containerLog.isDebugEnabled()) {
                                    this.containerLog.debug("Found roles: " + (roles == null ? "" : roles.toString()));
                                }
                                GenericPrincipal genericPrincipal = new GenericPrincipal(username, roles);
                                if (currentThread != null) {
                                    currentThread.setContextClassLoader(ocl);
                                }
                                return genericPrincipal;
                            }
                        } catch (InvalidNameException e) {
                            this.containerLog.warn(sm.getString("jndiRealm.exception"), e);
                        }
                    }
                }
                return null;
            }
            User user2 = getUser(connection, username, credentials);
            if (user2 == null) {
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return null;
            }
            if (!checkCredentials(connection.context, user2, credentials)) {
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return null;
            }
            List<String> roles2 = getRoles(connection, user2);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found roles: " + (roles2 == null ? "" : roles2.toString()));
            }
            GenericPrincipal genericPrincipal2 = new GenericPrincipal(username, roles2);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return genericPrincipal2;
        } finally {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            Principal authenticate = super.authenticate(username);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return authenticate;
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            Principal authenticate = super.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realm, digestA2, algorithm);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return authenticate;
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(X509Certificate[] certs) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            Principal authenticate = super.authenticate(certs);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return authenticate;
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(GSSContext gssContext, boolean storeCred) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            Principal authenticate = super.authenticate(gssContext, storeCred);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return authenticate;
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(getClass().getClassLoader());
            }
            Principal authenticate = super.authenticate(gssName, gssCredential);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return authenticate;
        } catch (Throwable th) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    protected User getUser(JNDIConnection connection, String username) throws NamingException {
        return getUser(connection, username, null, -1);
    }

    protected User getUser(JNDIConnection connection, String username, String credentials) throws NamingException {
        return getUser(connection, username, credentials, -1);
    }

    protected User getUser(JNDIConnection connection, String username, String credentials, int curUserPattern) throws NamingException {
        User user;
        List<String> list = new ArrayList<>();
        if (this.userPassword != null) {
            list.add(this.userPassword);
        }
        if (this.userRoleName != null) {
            list.add(this.userRoleName);
        }
        if (this.userRoleAttribute != null) {
            list.add(this.userRoleAttribute);
        }
        String[] attrIds = (String[]) list.toArray(new String[0]);
        if (this.userPatternArray != null && curUserPattern >= 0) {
            user = getUserByPattern(connection, username, credentials, attrIds, curUserPattern);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found user by pattern [" + user + "]");
            }
        } else {
            boolean thisUserSearchAsUser = isUserSearchAsUser();
            if (thisUserSearchAsUser) {
                try {
                    userCredentialsAdd(connection.context, username, credentials);
                } catch (Throwable th) {
                    if (thisUserSearchAsUser) {
                        userCredentialsRemove(connection.context);
                    }
                    throw th;
                }
            }
            user = getUserBySearch(connection, username, attrIds);
            if (thisUserSearchAsUser) {
                userCredentialsRemove(connection.context);
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found user by search [" + user + "]");
            }
        }
        if (this.userPassword == null && credentials != null && user != null) {
            return new User(user.getUserName(), user.getDN(), credentials, user.getRoles(), user.getUserRoleId());
        }
        return user;
    }

    protected User getUserByPattern(DirContext context, String username, String[] attrIds, String dn) throws NamingException {
        if (attrIds == null || attrIds.length == 0) {
            return new User(username, dn, null, null, null);
        }
        try {
            Attributes attrs = context.getAttributes(dn, attrIds);
            if (attrs == null) {
                return null;
            }
            String password = null;
            if (this.userPassword != null) {
                password = getAttributeValue(this.userPassword, attrs);
            }
            String userRoleAttrValue = null;
            if (this.userRoleAttribute != null) {
                userRoleAttrValue = getAttributeValue(this.userRoleAttribute, attrs);
            }
            ArrayList<String> roles = null;
            if (this.userRoleName != null) {
                roles = addAttributeValues(this.userRoleName, attrs, null);
            }
            return new User(username, dn, password, roles, userRoleAttrValue);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    protected User getUserByPattern(JNDIConnection connection, String username, String credentials, String[] attrIds, int curUserPattern) throws NamingException {
        User user;
        if (username == null || this.userPatternArray[curUserPattern] == null) {
            return null;
        }
        String dn = connection.userPatternFormatArray[curUserPattern].format(new String[]{doAttributeValueEscaping(username)});
        try {
            user = getUserByPattern(connection.context, username, attrIds, dn);
        } catch (NamingException e) {
            try {
                userCredentialsAdd(connection.context, dn, credentials);
                user = getUserByPattern(connection.context, username, attrIds, dn);
                userCredentialsRemove(connection.context);
            } catch (Throwable th) {
                userCredentialsRemove(connection.context);
                throw th;
            }
        } catch (NameNotFoundException e2) {
            return null;
        }
        return user;
    }

    protected User getUserBySearch(JNDIConnection connection, String username, String[] attrIds) throws NamingException {
        if (username == null || connection.userSearchFormat == null) {
            return null;
        }
        String filter = connection.userSearchFormat.format(new String[]{doFilterEscaping(username)});
        SearchControls constraints = new SearchControls();
        if (this.userSubtree) {
            constraints.setSearchScope(2);
        } else {
            constraints.setSearchScope(1);
        }
        constraints.setCountLimit(this.sizeLimit);
        constraints.setTimeLimit(this.timeLimit);
        if (attrIds == null) {
            attrIds = new String[0];
        }
        constraints.setReturningAttributes(attrIds);
        NamingEnumeration<SearchResult> results = connection.context.search(this.userBase, filter, constraints);
        if (results != null) {
            try {
                try {
                    if (results.hasMore()) {
                        SearchResult result = (SearchResult) results.next();
                        try {
                        } catch (PartialResultException ex) {
                            if (!this.adCompat) {
                                throw ex;
                            }
                        }
                        if (results.hasMore()) {
                            if (this.containerLog.isInfoEnabled()) {
                                this.containerLog.info(sm.getString("jndiRealm.multipleEntries", username));
                            }
                            if (results != null) {
                                results.close();
                            }
                            return null;
                        }
                        String dn = getDistinguishedName(connection.context, this.userBase, result);
                        if (this.containerLog.isTraceEnabled()) {
                            this.containerLog.trace("  entry found for " + username + " with dn " + dn);
                        }
                        Attributes attrs = result.getAttributes();
                        if (attrs == null) {
                            if (results != null) {
                                results.close();
                            }
                            return null;
                        }
                        String password = null;
                        if (this.userPassword != null) {
                            password = getAttributeValue(this.userPassword, attrs);
                        }
                        String userRoleAttrValue = null;
                        if (this.userRoleAttribute != null) {
                            userRoleAttrValue = getAttributeValue(this.userRoleAttribute, attrs);
                        }
                        ArrayList<String> roles = null;
                        if (this.userRoleName != null) {
                            roles = addAttributeValues(this.userRoleName, attrs, null);
                        }
                        User user = new User(username, dn, password, roles, userRoleAttrValue);
                        if (results != null) {
                            results.close();
                        }
                        return user;
                    }
                } catch (PartialResultException ex2) {
                    if (!this.adCompat) {
                        throw ex2;
                    }
                    if (results != null) {
                        results.close();
                    }
                    return null;
                }
            } catch (Throwable th) {
                if (results != null) {
                    results.close();
                }
                throw th;
            }
        }
        if (results != null) {
            results.close();
        }
        return null;
    }

    protected boolean checkCredentials(DirContext context, User user, String credentials) throws NamingException {
        boolean validated;
        if (this.userPassword == null) {
            validated = bindAsUser(context, user, credentials);
        } else {
            validated = compareCredentials(context, user, credentials);
        }
        if (this.containerLog.isTraceEnabled()) {
            if (validated) {
                this.containerLog.trace(sm.getString("jndiRealm.authenticateSuccess", user.getUserName()));
            } else {
                this.containerLog.trace(sm.getString("jndiRealm.authenticateFailure", user.getUserName()));
            }
        }
        return validated;
    }

    protected boolean compareCredentials(DirContext context, User info, String credentials) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  validating credentials");
        }
        if (info == null || credentials == null) {
            return false;
        }
        String password = info.getPassword();
        return getCredentialHandler().matches(credentials, password);
    }

    protected boolean bindAsUser(DirContext context, User user, String credentials) throws NamingException {
        String dn;
        if (credentials == null || user == null || (dn = user.getDN()) == null) {
            return false;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  validating credentials by binding as the user");
        }
        userCredentialsAdd(context, dn, credentials);
        boolean validated = false;
        try {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  binding as " + dn);
            }
            context.getAttributes("", (String[]) null);
            validated = true;
        } catch (AuthenticationException e) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  bind attempt failed");
            }
        }
        userCredentialsRemove(context);
        return validated;
    }

    private void userCredentialsAdd(DirContext context, String dn, String credentials) throws NamingException {
        context.addToEnvironment("java.naming.security.principal", dn);
        context.addToEnvironment("java.naming.security.credentials", credentials);
    }

    private void userCredentialsRemove(DirContext context) throws NamingException {
        if (this.connectionName != null) {
            context.addToEnvironment("java.naming.security.principal", this.connectionName);
        } else {
            context.removeFromEnvironment("java.naming.security.principal");
        }
        if (this.connectionPassword != null) {
            context.addToEnvironment("java.naming.security.credentials", this.connectionPassword);
        } else {
            context.removeFromEnvironment("java.naming.security.credentials");
        }
    }

    protected List<String> getRoles(JNDIConnection connection, User user) throws NamingException {
        String base;
        if (user == null) {
            return null;
        }
        String dn = user.getDN();
        String username = user.getUserName();
        String userRoleId = user.getUserRoleId();
        if (dn == null || username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  getRoles(" + dn + ")");
        }
        List<String> list = new ArrayList<>();
        List<String> userRoles = user.getRoles();
        if (userRoles != null) {
            list.addAll(userRoles);
        }
        if (this.commonRole != null) {
            list.add(this.commonRole);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  Found " + list.size() + " user internal roles");
            this.containerLog.trace("  Found user internal roles " + list.toString());
        }
        if (connection.roleFormat == null || this.roleName == null) {
            return list;
        }
        String filter = connection.roleFormat.format(new String[]{doFilterEscaping(dn), doFilterEscaping(doAttributeValueEscaping(username)), doFilterEscaping(doAttributeValueEscaping(userRoleId))});
        SearchControls controls = new SearchControls();
        if (this.roleSubtree) {
            controls.setSearchScope(2);
        } else {
            controls.setSearchScope(1);
        }
        controls.setReturningAttributes(new String[]{this.roleName});
        if (connection.roleBaseFormat != null) {
            NameParser np = connection.context.getNameParser("");
            Name name = np.parse(dn);
            String[] nameParts = new String[name.size()];
            for (int i = 0; i < name.size(); i++) {
                nameParts[i] = convertToHexEscape(name.get(i));
            }
            base = connection.roleBaseFormat.format(nameParts);
        } else {
            base = "";
        }
        NamingEnumeration<SearchResult> results = searchAsUser(connection.context, user, base, filter, controls, isRoleSearchAsUser());
        if (results == null) {
            return list;
        }
        Map<String, String> groupMap = new HashMap<>();
        while (results.hasMore()) {
            try {
                try {
                    SearchResult result = (SearchResult) results.next();
                    Attributes attrs = result.getAttributes();
                    if (attrs != null) {
                        String dname = getDistinguishedName(connection.context, base, result);
                        String name2 = getAttributeValue(this.roleName, attrs);
                        if (name2 != null && dname != null) {
                            groupMap.put(dname, name2);
                        }
                    }
                } catch (PartialResultException ex) {
                    if (!this.adCompat) {
                        throw ex;
                    }
                    results.close();
                }
            } catch (Throwable th) {
                results.close();
                throw th;
            }
        }
        results.close();
        if (this.containerLog.isTraceEnabled()) {
            Set<Map.Entry<String, String>> entries = groupMap.entrySet();
            this.containerLog.trace("  Found " + entries.size() + " direct roles");
            for (Map.Entry<String, String> entry : entries) {
                this.containerLog.trace("  Found direct role " + entry.getKey() + " -> " + entry.getValue());
            }
        }
        if (getRoleNested()) {
            Map<String, String> hashMap = new HashMap<>(groupMap);
            loop3: while (true) {
                Map<String, String> newGroups = hashMap;
                if (newGroups.isEmpty()) {
                    break;
                }
                Map<String, String> newThisRound = new HashMap<>();
                for (Map.Entry<String, String> group : newGroups.entrySet()) {
                    String filter2 = connection.roleFormat.format(new String[]{doFilterEscaping(group.getKey()), doFilterEscaping(doAttributeValueEscaping(group.getValue())), doFilterEscaping(doAttributeValueEscaping(group.getValue()))});
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace("Perform a nested group search with base " + this.roleBase + " and filter " + filter2);
                    }
                    NamingEnumeration<SearchResult> results2 = searchAsUser(connection.context, user, base, filter2, controls, isRoleSearchAsUser());
                    while (results2.hasMore()) {
                        try {
                            try {
                                SearchResult result2 = (SearchResult) results2.next();
                                Attributes attrs2 = result2.getAttributes();
                                if (attrs2 != null) {
                                    String dname2 = getDistinguishedName(connection.context, this.roleBase, result2);
                                    String name3 = getAttributeValue(this.roleName, attrs2);
                                    if (name3 != null && dname2 != null && !groupMap.keySet().contains(dname2)) {
                                        groupMap.put(dname2, name3);
                                        newThisRound.put(dname2, name3);
                                        if (this.containerLog.isTraceEnabled()) {
                                            this.containerLog.trace("  Found nested role " + dname2 + " -> " + name3);
                                        }
                                    }
                                }
                            } catch (PartialResultException ex2) {
                                if (!this.adCompat) {
                                    throw ex2;
                                }
                                results2.close();
                            }
                        } catch (Throwable th2) {
                            results2.close();
                            throw th2;
                        }
                    }
                    results2.close();
                }
                hashMap = newThisRound;
            }
        }
        list.addAll(groupMap.values());
        return list;
    }

    private NamingEnumeration<SearchResult> searchAsUser(DirContext context, User user, String base, String filter, SearchControls controls, boolean searchAsUser) throws NamingException {
        if (searchAsUser) {
            try {
                userCredentialsAdd(context, user.getDN(), user.getPassword());
            } catch (Throwable th) {
                if (searchAsUser) {
                    userCredentialsRemove(context);
                }
                throw th;
            }
        }
        NamingEnumeration<SearchResult> results = context.search(base, filter, controls);
        if (searchAsUser) {
            userCredentialsRemove(context);
        }
        return results;
    }

    private String getAttributeValue(String attrId, Attributes attrs) throws NamingException {
        Attribute attr;
        Object value;
        String valueString;
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  retrieving attribute " + attrId);
        }
        if (attrId == null || attrs == null || (attr = attrs.get(attrId)) == null || (value = attr.get()) == null) {
            return null;
        }
        if (value instanceof byte[]) {
            valueString = new String((byte[]) value);
        } else {
            valueString = value.toString();
        }
        return valueString;
    }

    private ArrayList<String> addAttributeValues(String attrId, Attributes attrs, ArrayList<String> values) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  retrieving values for attribute " + attrId);
        }
        if (attrId == null || attrs == null) {
            return values;
        }
        if (values == null) {
            values = new ArrayList<>();
        }
        Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return values;
        }
        NamingEnumeration<?> e = attr.getAll();
        while (e.hasMore()) {
            try {
                try {
                    String value = (String) e.next();
                    values.add(value);
                } catch (PartialResultException ex) {
                    if (!this.adCompat) {
                        throw ex;
                    }
                    e.close();
                }
            } catch (Throwable th) {
                e.close();
                throw th;
            }
        }
        e.close();
        return values;
    }

    protected void close(JNDIConnection connection) {
        if (connection == null || connection.context == null) {
            if (this.connectionPool == null) {
                this.singleConnectionLock.unlock();
                return;
            }
            return;
        }
        if (this.tls != null) {
            try {
                this.tls.close();
            } catch (IOException e) {
                this.containerLog.error(sm.getString("jndiRealm.tlsClose"), e);
            }
        }
        try {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Closing directory context");
            }
            connection.context.close();
        } catch (NamingException e2) {
            this.containerLog.error(sm.getString("jndiRealm.close"), e2);
        }
        connection.context = null;
        if (this.connectionPool == null) {
            this.singleConnectionLock.unlock();
        }
    }

    protected void closePooledConnections() {
        if (this.connectionPool != null) {
            synchronized (this.connectionPool) {
                while (true) {
                    JNDIConnection connection = this.connectionPool.pop();
                    if (connection != null) {
                        close(connection);
                    }
                }
            }
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        User user;
        String userPassword = getUserPassword();
        if (userPassword == null || userPassword.isEmpty()) {
            return null;
        }
        JNDIConnection connection = null;
        try {
            connection = get();
            try {
                user = getUser(connection, username, null);
            } catch (NullPointerException | NamingException e) {
                this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
                close(connection);
                closePooledConnections();
                connection = get();
                user = getUser(connection, username, null);
            }
            release(connection);
            if (user == null) {
                return null;
            }
            return user.getPassword();
        } catch (Exception e2) {
            this.containerLog.error(sm.getString("jndiRealm.exception"), e2);
            close(connection);
            closePooledConnections();
            return null;
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        return getPrincipal(username, (GSSCredential) null);
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(GSSName gssName, GSSCredential gssCredential) {
        int i;
        String name = gssName.toString();
        if (isStripRealmForGss() && (i = name.indexOf(64)) > 0) {
            name = name.substring(0, i);
        }
        return getPrincipal(name, gssCredential);
    }

    protected Principal getPrincipal(String username, GSSCredential gssCredential) {
        Principal principal;
        JNDIConnection connection = null;
        try {
            connection = get();
            try {
                principal = getPrincipal(connection, username, gssCredential);
            } catch (NamingException e) {
                this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
                close(connection);
                closePooledConnections();
                connection = get();
                principal = getPrincipal(connection, username, gssCredential);
            }
            release(connection);
            return principal;
        } catch (Exception e2) {
            this.containerLog.error(sm.getString("jndiRealm.exception"), e2);
            close(connection);
            closePooledConnections();
            return null;
        }
    }

    protected Principal getPrincipal(JNDIConnection connection, String username, GSSCredential gssCredential) throws NamingException {
        List<String> roles = null;
        Hashtable<?, ?> preservedEnvironment = null;
        DirContext context = connection.context;
        if (gssCredential != null) {
            try {
                if (isUseDelegatedCredential()) {
                    preservedEnvironment = context.getEnvironment();
                    context.addToEnvironment("java.naming.security.authentication", "GSSAPI");
                    context.addToEnvironment("javax.security.sasl.server.authentication", "true");
                    context.addToEnvironment("javax.security.sasl.qop", this.spnegoDelegationQop);
                }
            } finally {
                if (gssCredential != null && isUseDelegatedCredential()) {
                    restoreEnvironmentParameter(context, "java.naming.security.authentication", preservedEnvironment);
                    restoreEnvironmentParameter(context, "javax.security.sasl.server.authentication", preservedEnvironment);
                    restoreEnvironmentParameter(context, "javax.security.sasl.qop", preservedEnvironment);
                }
            }
        }
        User user = getUser(connection, username);
        if (user != null) {
            roles = getRoles(connection, user);
        }
        if (user != null) {
            return new GenericPrincipal(user.getUserName(), roles, (Principal) null, (LoginContext) null, gssCredential, (Map<String, Object>) null);
        }
        return null;
    }

    private void restoreEnvironmentParameter(DirContext context, String parameterName, Hashtable<?, ?> preservedEnvironment) {
        try {
            context.removeFromEnvironment(parameterName);
            if (preservedEnvironment != null && preservedEnvironment.containsKey(parameterName)) {
                context.addToEnvironment(parameterName, preservedEnvironment.get(parameterName));
            }
        } catch (NamingException e) {
        }
    }

    protected JNDIConnection get() throws NamingException {
        JNDIConnection connection;
        if (this.connectionPool != null) {
            connection = this.connectionPool.pop();
            if (connection == null) {
                connection = create();
            }
        } else {
            this.singleConnectionLock.lock();
            if (this.singleConnection == null) {
                this.singleConnection = create();
            }
            connection = this.singleConnection;
        }
        if (connection.context == null) {
            open(connection);
        }
        return connection;
    }

    protected void release(JNDIConnection connection) {
        if (this.connectionPool != null) {
            if (connection != null && !this.connectionPool.push(connection)) {
                close(connection);
                return;
            }
            return;
        }
        this.singleConnectionLock.unlock();
    }

    protected JNDIConnection create() {
        return new JNDIConnection(this.userSearch, this.userPatternArray, this.roleBase, this.roleSearch);
    }

    protected void open(JNDIConnection connection) throws NamingException {
        try {
            connection.context = createDirContext(getDirectoryContextEnvironment());
        } catch (Exception e) {
            if (this.alternateURL == null || this.alternateURL.length() == 0) {
                throw e;
            }
            this.connectionAttempt = 1;
            this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
            connection.context = createDirContext(getDirectoryContextEnvironment());
        } finally {
            this.connectionAttempt = 0;
        }
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        return (this.connectionPool == null && this.singleConnection.context == null) ? false : true;
    }

    private DirContext createDirContext(Hashtable<String, String> env) throws NamingException {
        if (this.useStartTls) {
            return createTlsDirContext(env);
        }
        return new InitialDirContext(env);
    }

    private SSLSocketFactory getSSLSocketFactory() {
        SSLSocketFactory result;
        if (this.sslSocketFactory != null) {
            return this.sslSocketFactory;
        }
        if (this.sslSocketFactoryClassName != null && !this.sslSocketFactoryClassName.trim().equals("")) {
            result = createSSLSocketFactoryFromClassName(this.sslSocketFactoryClassName);
        } else {
            result = createSSLContextFactoryFromProtocol(this.sslProtocol);
        }
        this.sslSocketFactory = result;
        return result;
    }

    private SSLSocketFactory createSSLSocketFactoryFromClassName(String className) {
        try {
            Object o = constructInstance(className);
            if (o instanceof SSLSocketFactory) {
                return this.sslSocketFactory;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", className));
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", className), e);
        }
    }

    private SSLSocketFactory createSSLContextFactoryFromProtocol(String protocol) {
        SSLContext sslContext;
        try {
            if (protocol != null) {
                sslContext = SSLContext.getInstance(protocol);
                sslContext.init(null, null, null);
            } else {
                sslContext = SSLContext.getDefault();
            }
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            List<String> allowedProtocols = Arrays.asList(getSupportedSslProtocols());
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslProtocol", protocol, allowedProtocols), e);
        }
    }

    private DirContext createTlsDirContext(Hashtable<String, String> env) throws NamingException {
        Map<String, Object> savedEnv = new HashMap<>();
        for (String key : Arrays.asList("java.naming.security.authentication", "java.naming.security.credentials", "java.naming.security.principal", "java.naming.security.protocol")) {
            Object entry = env.remove(key);
            if (entry != null) {
                savedEnv.put(key, entry);
            }
        }
        LdapContext result = null;
        try {
            result = new InitialLdapContext(env, (Control[]) null);
            this.tls = result.extendedOperation(new StartTlsRequest());
            if (getHostnameVerifier() != null) {
                this.tls.setHostnameVerifier(getHostnameVerifier());
            }
            if (getCipherSuitesArray() != null) {
                this.tls.setEnabledCipherSuites(getCipherSuitesArray());
            }
            try {
                SSLSession negotiate = this.tls.negotiate(getSSLSocketFactory());
                this.containerLog.debug(sm.getString("jndiRealm.negotiatedTls", negotiate.getProtocol()));
                if (result != null) {
                    for (Map.Entry<String, Object> savedEntry : savedEnv.entrySet()) {
                        result.addToEnvironment(savedEntry.getKey(), savedEntry.getValue());
                    }
                }
                return result;
            } catch (IOException e) {
                throw new NamingException(e.getMessage());
            }
        } catch (Throwable th) {
            if (result != null) {
                for (Map.Entry<String, Object> savedEntry2 : savedEnv.entrySet()) {
                    result.addToEnvironment(savedEntry2.getKey(), savedEntry2.getValue());
                }
            }
            throw th;
        }
    }

    protected Hashtable<String, String> getDirectoryContextEnvironment() {
        Hashtable<String, String> env = new Hashtable<>();
        if (this.containerLog.isDebugEnabled() && this.connectionAttempt == 0) {
            this.containerLog.debug("Connecting to URL " + this.connectionURL);
        } else if (this.containerLog.isDebugEnabled() && this.connectionAttempt > 0) {
            this.containerLog.debug("Connecting to URL " + this.alternateURL);
        }
        env.put(CoreTestConstants.JAVA_NAMING_FACTORY_INITIAL, this.contextFactory);
        if (this.connectionName != null) {
            env.put("java.naming.security.principal", this.connectionName);
        }
        if (this.connectionPassword != null) {
            env.put("java.naming.security.credentials", this.connectionPassword);
        }
        if (this.connectionURL != null && this.connectionAttempt == 0) {
            env.put("java.naming.provider.url", this.connectionURL);
        } else if (this.alternateURL != null && this.connectionAttempt > 0) {
            env.put("java.naming.provider.url", this.alternateURL);
        }
        if (this.authentication != null) {
            env.put("java.naming.security.authentication", this.authentication);
        }
        if (this.protocol != null) {
            env.put("java.naming.security.protocol", this.protocol);
        }
        if (this.referrals != null) {
            env.put("java.naming.referral", this.referrals);
        }
        if (this.derefAliases != null) {
            env.put(DEREF_ALIASES, this.derefAliases);
        }
        if (this.connectionTimeout != null) {
            env.put("com.sun.jndi.ldap.connect.timeout", this.connectionTimeout);
        }
        if (this.readTimeout != null) {
            env.put("com.sun.jndi.ldap.read.timeout", this.readTimeout);
        }
        return env;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        if (this.connectionPoolSize != 1) {
            this.connectionPool = new SynchronizedStack<>(128, this.connectionPoolSize);
        }
        ClassLoader ocl = null;
        Thread currentThread = null;
        JNDIConnection connection = null;
        try {
            try {
                if (!isUseContextClassLoader()) {
                    currentThread = Thread.currentThread();
                    ocl = currentThread.getContextClassLoader();
                    currentThread.setContextClassLoader(getClass().getClassLoader());
                }
                connection = get();
                release(connection);
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
            } catch (NamingException e) {
                this.containerLog.error(sm.getString("jndiRealm.open"), e);
                release(connection);
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
            }
            super.startInternal();
        } catch (Throwable th) {
            release(connection);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.connectionPool == null) {
            this.singleConnectionLock.lock();
            close(this.singleConnection);
        } else {
            closePooledConnections();
            this.connectionPool = null;
        }
    }

    protected String[] parseUserPatternString(String userPatternString) {
        int endParenLoc;
        if (userPatternString != null) {
            List<String> pathList = new ArrayList<>();
            int startParenLoc = userPatternString.indexOf(40);
            if (startParenLoc == -1) {
                return new String[]{userPatternString};
            }
            while (startParenLoc > -1) {
                while (true) {
                    if (userPatternString.charAt(startParenLoc + 1) == '|' || (startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\')) {
                        startParenLoc = userPatternString.indexOf(40, startParenLoc + 1);
                    }
                }
                int indexOf = userPatternString.indexOf(41, startParenLoc + 1);
                while (true) {
                    endParenLoc = indexOf;
                    if (userPatternString.charAt(endParenLoc - 1) == '\\') {
                        indexOf = userPatternString.indexOf(41, endParenLoc + 1);
                    }
                }
                String nextPathPart = userPatternString.substring(startParenLoc + 1, endParenLoc);
                pathList.add(nextPathPart);
                int startingPoint = endParenLoc + 1;
                startParenLoc = userPatternString.indexOf(40, startingPoint);
            }
            return (String[]) pathList.toArray(new String[0]);
        }
        return null;
    }

    protected String doFilterEscaping(String inString) {
        if (inString == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            switch (c) {
                case 0:
                    buf.append("\\00");
                    break;
                case '(':
                    buf.append("\\28");
                    break;
                case ')':
                    buf.append("\\29");
                    break;
                case '*':
                    buf.append("\\2a");
                    break;
                case '\\':
                    buf.append("\\5c");
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        return buf.toString();
    }

    protected String getDistinguishedName(DirContext context, String base, SearchResult result) throws NamingException {
        Name name;
        String resultName = result.getName();
        if (result.isRelative()) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  search returned relative name: " + resultName);
            }
            NameParser parser = context.getNameParser("");
            Name contextName = parser.parse(context.getNameInNamespace());
            Name baseName = parser.parse(base);
            Name entryName = parser.parse(new CompositeName(resultName).get(0));
            Name name2 = contextName.addAll(baseName);
            name = name2.addAll(entryName);
        } else {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  search returned absolute name: " + resultName);
            }
            try {
                NameParser parser2 = context.getNameParser("");
                URI userNameUri = new URI(resultName);
                String pathComponent = userNameUri.getPath();
                if (pathComponent.length() < 1) {
                    throw new InvalidNameException(sm.getString("jndiRealm.invalidName", resultName));
                }
                name = parser2.parse(pathComponent.substring(1));
            } catch (URISyntaxException e) {
                throw new InvalidNameException(sm.getString("jndiRealm.invalidName", resultName));
            }
        }
        if (getForceDnHexEscape()) {
            return convertToHexEscape(name.toString());
        }
        return name.toString();
    }

    protected String doAttributeValueEscaping(String input) {
        if (input == null) {
            return null;
        }
        int len = input.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            switch (c) {
                case 0:
                    result.append("\\00");
                    break;
                case ' ':
                    if (i == 0 || i == len - 1) {
                        result.append("\\20");
                        break;
                    } else {
                        result.append(c);
                        break;
                    }
                    break;
                case '\"':
                    result.append("\\22");
                    break;
                case '#':
                    if (i == 0) {
                        result.append("\\23");
                        break;
                    } else {
                        result.append(c);
                        break;
                    }
                case '+':
                    result.append("\\2B");
                    break;
                case ',':
                    result.append("\\2C");
                    break;
                case ';':
                    result.append("\\3B");
                    break;
                case '<':
                    result.append("\\3C");
                    break;
                case '>':
                    result.append("\\3E");
                    break;
                case '\\':
                    result.append("\\5C");
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return result.toString();
    }

    protected static String convertToHexEscape(String input) {
        if (input.indexOf(92) == -1) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length() + 6);
        boolean previousSlash = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (previousSlash) {
                switch (c) {
                    case ' ':
                        result.append("\\20");
                        break;
                    case '\"':
                        result.append("\\22");
                        break;
                    case '#':
                        result.append("\\23");
                        break;
                    case '+':
                        result.append("\\2B");
                        break;
                    case ',':
                        result.append("\\2C");
                        break;
                    case ';':
                        result.append("\\3B");
                        break;
                    case '<':
                        result.append("\\3C");
                        break;
                    case '=':
                        result.append("\\3D");
                        break;
                    case '>':
                        result.append("\\3E");
                        break;
                    case '\\':
                        result.append("\\5C");
                        break;
                    default:
                        result.append('\\');
                        result.append(c);
                        break;
                }
                previousSlash = false;
            } else if (c == '\\') {
                previousSlash = true;
            } else {
                result.append(c);
            }
        }
        if (previousSlash) {
            result.append('\\');
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/JNDIRealm$User.class */
    public static class User {
        private final String username;
        private final String dn;
        private final String password;
        private final List<String> roles;
        private final String userRoleId;

        public User(String username, String dn, String password, List<String> roles, String userRoleId) {
            this.username = username;
            this.dn = dn;
            this.password = password;
            if (roles == null) {
                this.roles = Collections.emptyList();
            } else {
                this.roles = Collections.unmodifiableList(roles);
            }
            this.userRoleId = userRoleId;
        }

        public String getUserName() {
            return this.username;
        }

        public String getDN() {
            return this.dn;
        }

        public String getPassword() {
            return this.password;
        }

        public List<String> getRoles() {
            return this.roles;
        }

        public String getUserRoleId() {
            return this.userRoleId;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/JNDIRealm$JNDIConnection.class */
    public static class JNDIConnection {
        public final MessageFormat userSearchFormat;
        public final MessageFormat[] userPatternFormatArray;
        public final MessageFormat roleBaseFormat;
        public final MessageFormat roleFormat;
        public volatile DirContext context = null;

        public JNDIConnection(String userSearch, String[] userPatternArray, String roleBase, String roleSearch) {
            if (userSearch == null) {
                this.userSearchFormat = null;
            } else {
                this.userSearchFormat = new MessageFormat(userSearch);
            }
            if (userPatternArray == null) {
                this.userPatternFormatArray = null;
            } else {
                int len = userPatternArray.length;
                this.userPatternFormatArray = new MessageFormat[len];
                for (int i = 0; i < len; i++) {
                    this.userPatternFormatArray[i] = new MessageFormat(userPatternArray[i]);
                }
            }
            if (roleBase == null) {
                this.roleBaseFormat = null;
            } else {
                this.roleBaseFormat = new MessageFormat(roleBase);
            }
            if (roleSearch == null) {
                this.roleFormat = null;
            } else {
                this.roleFormat = new MessageFormat(roleSearch);
            }
        }
    }
}
