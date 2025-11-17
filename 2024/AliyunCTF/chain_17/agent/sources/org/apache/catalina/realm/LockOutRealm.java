package org.apache.catalina.realm;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/LockOutRealm.class */
public class LockOutRealm extends CombinedRealm {
    private static final Log log = LogFactory.getLog((Class<?>) LockOutRealm.class);
    protected int failureCount = 5;
    protected int lockOutTime = 300;
    protected int cacheSize = 1000;
    protected int cacheRemovalWarningTime = 3600;
    protected Map<String, LockRecord> failedUsers = null;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        this.failedUsers = new LinkedHashMap<String, LockRecord>(this.cacheSize, 0.75f, true) { // from class: org.apache.catalina.realm.LockOutRealm.1
            private static final long serialVersionUID = 1;

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, LockRecord> eldest) {
                if (size() > LockOutRealm.this.cacheSize) {
                    long timeInCache = (System.currentTimeMillis() - eldest.getValue().getLastFailureTime()) / 1000;
                    if (timeInCache < LockOutRealm.this.cacheRemovalWarningTime) {
                        LockOutRealm.log.warn(RealmBase.sm.getString("lockOutRealm.removeWarning", eldest.getKey(), Long.valueOf(timeInCache)));
                        return true;
                    }
                    return true;
                }
                return false;
            }
        };
        super.startInternal();
    }

    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm) {
        Principal authenticatedUser = super.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realmName, digestA2, algorithm);
        return filterLockedAccounts(username, authenticatedUser);
    }

    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        Principal authenticatedUser = super.authenticate(username, credentials);
        return filterLockedAccounts(username, authenticatedUser);
    }

    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(X509Certificate[] certs) {
        String username = null;
        if (certs != null && certs.length > 0) {
            username = certs[0].getSubjectX500Principal().toString();
        }
        Principal authenticatedUser = super.authenticate(certs);
        return filterLockedAccounts(username, authenticatedUser);
    }

    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(GSSContext gssContext, boolean storeCreds) {
        if (gssContext.isEstablished()) {
            try {
                GSSName name = gssContext.getSrcName();
                String username = name.toString();
                Principal authenticatedUser = super.authenticate(gssContext, storeCreds);
                return filterLockedAccounts(username, authenticatedUser);
            } catch (GSSException e) {
                log.warn(sm.getString("realmBase.gssNameFail"), e);
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.catalina.realm.CombinedRealm, org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        String username = gssName.toString();
        Principal authenticatedUser = super.authenticate(gssName, gssCredential);
        return filterLockedAccounts(username, authenticatedUser);
    }

    private Principal filterLockedAccounts(String username, Principal authenticatedUser) {
        if (authenticatedUser == null && isAvailable()) {
            registerAuthFailure(username);
        }
        if (isLocked(username)) {
            log.warn(sm.getString("lockOutRealm.authLockedUser", username));
            return null;
        }
        if (authenticatedUser != null) {
            registerAuthSuccess(username);
        }
        return authenticatedUser;
    }

    public void unlock(String username) {
        registerAuthSuccess(username);
    }

    public boolean isLocked(String username) {
        LockRecord lockRecord;
        synchronized (this) {
            lockRecord = this.failedUsers.get(username);
        }
        if (lockRecord != null && lockRecord.getFailures() >= this.failureCount && (System.currentTimeMillis() - lockRecord.getLastFailureTime()) / 1000 < this.lockOutTime) {
            return true;
        }
        return false;
    }

    private synchronized void registerAuthSuccess(String username) {
        this.failedUsers.remove(username);
    }

    private void registerAuthFailure(String username) {
        LockRecord lockRecord;
        synchronized (this) {
            if (!this.failedUsers.containsKey(username)) {
                lockRecord = new LockRecord();
                this.failedUsers.put(username, lockRecord);
            } else {
                lockRecord = this.failedUsers.get(username);
                if (lockRecord.getFailures() >= this.failureCount && (System.currentTimeMillis() - lockRecord.getLastFailureTime()) / 1000 > this.lockOutTime) {
                    lockRecord.setFailures(0);
                }
            }
        }
        lockRecord.registerFailure();
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getLockOutTime() {
        return this.lockOutTime;
    }

    public void setLockOutTime(int lockOutTime) {
        this.lockOutTime = lockOutTime;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheRemovalWarningTime() {
        return this.cacheRemovalWarningTime;
    }

    public void setCacheRemovalWarningTime(int cacheRemovalWarningTime) {
        this.cacheRemovalWarningTime = cacheRemovalWarningTime;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/LockOutRealm$LockRecord.class */
    public static class LockRecord {
        private final AtomicInteger failures = new AtomicInteger(0);
        private long lastFailureTime = 0;

        protected LockRecord() {
        }

        public int getFailures() {
            return this.failures.get();
        }

        public void setFailures(int theFailures) {
            this.failures.set(theFailures);
        }

        public long getLastFailureTime() {
            return this.lastFailureTime;
        }

        public void registerFailure() {
            this.failures.incrementAndGet();
            this.lastFailureTime = System.currentTimeMillis();
        }
    }
}
