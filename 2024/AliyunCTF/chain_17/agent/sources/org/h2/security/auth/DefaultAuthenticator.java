package org.h2.security.auth;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.h2.api.CredentialsValidator;
import org.h2.api.UserToRolesMapper;
import org.h2.engine.Database;
import org.h2.engine.Right;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.engine.User;
import org.h2.engine.UserBuilder;
import org.h2.message.Trace;
import org.h2.security.auth.impl.AssignRealmNameRole;
import org.h2.security.auth.impl.JaasCredentialsValidator;
import org.h2.util.StringUtils;
import org.xml.sax.SAXException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/DefaultAuthenticator.class */
public class DefaultAuthenticator implements Authenticator {
    public static final String DEFAULT_REALMNAME = "H2";
    private Map<String, CredentialsValidator> realms = new HashMap();
    private List<UserToRolesMapper> userToRolesMappers = new ArrayList();
    private boolean allowUserRegistration;
    private boolean persistUsers;
    private boolean createMissingRoles;
    private boolean skipDefaultInitialization;
    private boolean initialized;
    private static DefaultAuthenticator instance;

    /* JADX INFO: Access modifiers changed from: protected */
    public static final DefaultAuthenticator getInstance() {
        if (instance == null) {
            instance = new DefaultAuthenticator();
        }
        return instance;
    }

    public DefaultAuthenticator() {
    }

    public DefaultAuthenticator(boolean z) {
        this.skipDefaultInitialization = z;
    }

    public boolean isPersistUsers() {
        return this.persistUsers;
    }

    public void setPersistUsers(boolean z) {
        this.persistUsers = z;
    }

    public boolean isAllowUserRegistration() {
        return this.allowUserRegistration;
    }

    public void setAllowUserRegistration(boolean z) {
        this.allowUserRegistration = z;
    }

    public boolean isCreateMissingRoles() {
        return this.createMissingRoles;
    }

    public void setCreateMissingRoles(boolean z) {
        this.createMissingRoles = z;
    }

    public void addRealm(String str, CredentialsValidator credentialsValidator) {
        this.realms.put(StringUtils.toUpperEnglish(str), credentialsValidator);
    }

    public List<UserToRolesMapper> getUserToRolesMappers() {
        return this.userToRolesMappers;
    }

    public void setUserToRolesMappers(UserToRolesMapper... userToRolesMapperArr) {
        ArrayList arrayList = new ArrayList();
        for (UserToRolesMapper userToRolesMapper : userToRolesMapperArr) {
            arrayList.add(userToRolesMapper);
        }
        this.userToRolesMappers = arrayList;
    }

    @Override // org.h2.security.auth.Authenticator
    public void init(Database database) throws AuthConfigException {
        if (this.skipDefaultInitialization || this.initialized) {
            return;
        }
        synchronized (this) {
            if (this.initialized) {
                return;
            }
            Trace trace = database.getTrace(2);
            URL url = null;
            try {
                String str = SysProperties.AUTH_CONFIG_FILE;
                if (str != null) {
                    if (trace.isDebugEnabled()) {
                        trace.debug("DefaultAuthenticator.config: configuration read from system property h2auth.configurationfile={0}", str);
                    }
                    url = new URL(str);
                }
                if (url == null) {
                    if (trace.isDebugEnabled()) {
                        trace.debug("DefaultAuthenticator.config: default configuration");
                    }
                    defaultConfiguration();
                } else {
                    configureFromUrl(url);
                }
                this.initialized = true;
            } catch (Exception e) {
                trace.error(e, "DefaultAuthenticator.config: an error occurred during configuration from {0} ", null);
                throw new AuthConfigException("Failed to configure authentication from " + ((Object) null), e);
            }
        }
    }

    private void defaultConfiguration() {
        this.createMissingRoles = false;
        this.allowUserRegistration = true;
        this.realms = new HashMap();
        JaasCredentialsValidator jaasCredentialsValidator = new JaasCredentialsValidator();
        jaasCredentialsValidator.configure(new ConfigProperties());
        this.realms.put(DEFAULT_REALMNAME, jaasCredentialsValidator);
        AssignRealmNameRole assignRealmNameRole = new AssignRealmNameRole();
        assignRealmNameRole.configure(new ConfigProperties());
        this.userToRolesMappers.add(assignRealmNameRole);
    }

    public void configureFromUrl(URL url) throws AuthenticationException, SAXException, IOException, ParserConfigurationException {
        configureFrom(H2AuthConfigXml.parseFrom(url));
    }

    private void configureFrom(H2AuthConfig h2AuthConfig) throws AuthenticationException {
        this.allowUserRegistration = h2AuthConfig.isAllowUserRegistration();
        this.createMissingRoles = h2AuthConfig.isCreateMissingRoles();
        HashMap hashMap = new HashMap();
        for (RealmConfig realmConfig : h2AuthConfig.getRealms()) {
            String name = realmConfig.getName();
            if (name == null) {
                throw new AuthenticationException("Missing realm name");
            }
            String upperCase = name.toUpperCase();
            try {
                CredentialsValidator credentialsValidator = (CredentialsValidator) Class.forName(realmConfig.getValidatorClass()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                credentialsValidator.configure(new ConfigProperties(realmConfig.getProperties()));
                if (hashMap.putIfAbsent(realmConfig.getName().toUpperCase(), credentialsValidator) != null) {
                    throw new AuthenticationException("Duplicate realm " + realmConfig.getName());
                }
            } catch (Exception e) {
                throw new AuthenticationException("invalid validator class fo realm " + upperCase, e);
            }
        }
        this.realms = hashMap;
        ArrayList arrayList = new ArrayList();
        for (UserToRolesMapperConfig userToRolesMapperConfig : h2AuthConfig.getUserToRolesMappers()) {
            try {
                UserToRolesMapper userToRolesMapper = (UserToRolesMapper) Class.forName(userToRolesMapperConfig.getClassName()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                userToRolesMapper.configure(new ConfigProperties(userToRolesMapperConfig.getProperties()));
                arrayList.add(userToRolesMapper);
            } catch (Exception e2) {
                throw new AuthenticationException("Invalid class in UserToRolesMapperConfig", e2);
            }
        }
        this.userToRolesMappers = arrayList;
    }

    private boolean updateRoles(AuthenticationInfo authenticationInfo, User user, Database database) throws AuthenticationException {
        boolean z = false;
        HashSet<String> hashSet = new HashSet();
        Iterator<UserToRolesMapper> it = this.userToRolesMappers.iterator();
        while (it.hasNext()) {
            Collection<String> mapUserToRoles = it.next().mapUserToRoles(authenticationInfo);
            if (mapUserToRoles != null && !mapUserToRoles.isEmpty()) {
                hashSet.addAll(mapUserToRoles);
            }
        }
        for (String str : hashSet) {
            if (str != null && !str.isEmpty()) {
                Role findRole = database.findRole(str);
                if (findRole == null && isCreateMissingRoles()) {
                    SessionLocal systemSession = database.getSystemSession();
                    systemSession.lock();
                    try {
                        findRole = new Role(database, database.allocateObjectId(), str, false);
                        database.addDatabaseObject(database.getSystemSession(), findRole);
                        database.getSystemSession().commit(false);
                        z = true;
                        systemSession.unlock();
                    } catch (Throwable th) {
                        systemSession.unlock();
                        throw th;
                    }
                }
                if (findRole != null && user.getRightForRole(findRole) == null) {
                    Right right = new Right(database, -1, user, findRole);
                    right.setTemporary(true);
                    user.grantRole(findRole, right);
                }
            }
        }
        return z;
    }

    @Override // org.h2.security.auth.Authenticator
    public final User authenticate(AuthenticationInfo authenticationInfo, Database database) throws AuthenticationException {
        String fullyQualifiedName = authenticationInfo.getFullyQualifiedName();
        User findUser = database.findUser(fullyQualifiedName);
        if (findUser == null && !isAllowUserRegistration()) {
            throw new AuthenticationException("User " + fullyQualifiedName + " not found in db");
        }
        CredentialsValidator credentialsValidator = this.realms.get(authenticationInfo.getRealm());
        if (credentialsValidator == null) {
            throw new AuthenticationException("realm " + authenticationInfo.getRealm() + " not configured");
        }
        try {
            if (!credentialsValidator.validateCredentials(authenticationInfo)) {
                return null;
            }
            if (findUser == null) {
                SessionLocal systemSession = database.getSystemSession();
                systemSession.lock();
                try {
                    findUser = UserBuilder.buildUser(authenticationInfo, database, isPersistUsers());
                    database.addDatabaseObject(database.getSystemSession(), findUser);
                    database.getSystemSession().commit(false);
                    systemSession.unlock();
                } catch (Throwable th) {
                    systemSession.unlock();
                    throw th;
                }
            }
            findUser.revokeTemporaryRightsOnRoles();
            updateRoles(authenticationInfo, findUser, database);
            return findUser;
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}
