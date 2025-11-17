package org.apache.catalina.session;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase.class */
public abstract class PersistentManagerBase extends ManagerBase implements StoreManager {
    private static final String name = "PersistentManagerBase";
    private static final String PERSISTED_LAST_ACCESSED_TIME = "org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime";
    private final Log log = LogFactory.getLog((Class<?>) PersistentManagerBase.class);
    protected Store store = null;
    protected boolean saveOnRestart = true;
    protected int maxIdleBackup = -1;
    protected int minIdleSwap = -1;
    protected int maxIdleSwap = -1;
    private final Map<String, Object> sessionSwapInLocks = new HashMap();
    private final ThreadLocal<Session> sessionToSwapIn = new ThreadLocal<>();

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreClear.class */
    private class PrivilegedStoreClear implements PrivilegedExceptionAction<Void> {
        PrivilegedStoreClear() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.clear();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreRemove.class */
    public class PrivilegedStoreRemove implements PrivilegedExceptionAction<Void> {
        private String id;

        PrivilegedStoreRemove(String id) {
            this.id = id;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.remove(this.id);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreLoad.class */
    public class PrivilegedStoreLoad implements PrivilegedExceptionAction<Session> {
        private String id;

        PrivilegedStoreLoad(String id) {
            this.id = id;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Session run() throws Exception {
            return PersistentManagerBase.this.store.load(this.id);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreSave.class */
    public class PrivilegedStoreSave implements PrivilegedExceptionAction<Void> {
        private Session session;

        PrivilegedStoreSave(Session session) {
            this.session = session;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.save(this.session);
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreKeys.class */
    private class PrivilegedStoreKeys implements PrivilegedExceptionAction<String[]> {
        PrivilegedStoreKeys() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public String[] run() throws Exception {
            return PersistentManagerBase.this.store.keys();
        }
    }

    public int getMaxIdleBackup() {
        return this.maxIdleBackup;
    }

    public void setMaxIdleBackup(int backup) {
        if (backup == this.maxIdleBackup) {
            return;
        }
        int oldBackup = this.maxIdleBackup;
        this.maxIdleBackup = backup;
        this.support.firePropertyChange("maxIdleBackup", Integer.valueOf(oldBackup), Integer.valueOf(this.maxIdleBackup));
    }

    public int getMaxIdleSwap() {
        return this.maxIdleSwap;
    }

    public void setMaxIdleSwap(int max) {
        if (max == this.maxIdleSwap) {
            return;
        }
        int oldMaxIdleSwap = this.maxIdleSwap;
        this.maxIdleSwap = max;
        this.support.firePropertyChange("maxIdleSwap", Integer.valueOf(oldMaxIdleSwap), Integer.valueOf(this.maxIdleSwap));
    }

    public int getMinIdleSwap() {
        return this.minIdleSwap;
    }

    public void setMinIdleSwap(int min) {
        if (this.minIdleSwap == min) {
            return;
        }
        int oldMinIdleSwap = this.minIdleSwap;
        this.minIdleSwap = min;
        this.support.firePropertyChange("minIdleSwap", Integer.valueOf(oldMinIdleSwap), Integer.valueOf(this.minIdleSwap));
    }

    public boolean isLoaded(String id) {
        try {
            if (super.findSession(id) != null) {
                return true;
            }
            return false;
        } catch (IOException e) {
            this.log.error(sm.getString("persistentManager.isLoadedError", id), e);
            return false;
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }

    public void setStore(Store store) {
        this.store = store;
        store.setManager(this);
    }

    @Override // org.apache.catalina.StoreManager
    public Store getStore() {
        return this.store;
    }

    public boolean getSaveOnRestart() {
        return this.saveOnRestart;
    }

    public void setSaveOnRestart(boolean saveOnRestart) {
        if (saveOnRestart == this.saveOnRestart) {
            return;
        }
        boolean oldSaveOnRestart = this.saveOnRestart;
        this.saveOnRestart = saveOnRestart;
        this.support.firePropertyChange("saveOnRestart", Boolean.valueOf(oldSaveOnRestart), Boolean.valueOf(this.saveOnRestart));
    }

    public void clearStore() {
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreClear());
                } catch (PrivilegedActionException e) {
                    this.log.error(sm.getString("persistentManager.storeClearError"), e.getException());
                }
            } else {
                this.store.clear();
            }
        } catch (IOException e2) {
            this.log.error(sm.getString("persistentManager.storeClearError"), e2);
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public void processExpires() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            Log log = this.log;
            String name2 = getName();
            int length = sessions.length;
            log.debug("Start expire sessions " + name2 + " at " + timeNow + " sessioncount " + log);
        }
        for (Session session : sessions) {
            if (!session.isValid()) {
                this.expiredSessions.incrementAndGet();
                expireHere++;
            }
        }
        processPersistenceChecks();
        if (getStore() instanceof StoreBase) {
            ((StoreBase) getStore()).processExpires();
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            Log log2 = this.log;
            log2.debug("End expire sessions " + getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + log2);
        }
        this.processingTime += timeEnd - timeNow;
    }

    public void processPersistenceChecks() {
        processMaxIdleSwaps();
        processMaxActiveSwaps();
        processMaxIdleBackups();
    }

    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.Manager
    public Session findSession(String id) throws IOException {
        Session session = super.findSession(id);
        if (session != null) {
            synchronized (session) {
                session = super.findSession(session.getIdInternal());
                if (session != null) {
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (session != null) {
            return session;
        }
        return swapIn(id);
    }

    @Override // org.apache.catalina.StoreManager
    public void removeSuper(Session session) {
        super.remove(session, false);
    }

    @Override // org.apache.catalina.Manager
    public void load() {
        String[] ids;
        this.sessions.clear();
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    ids = (String[]) AccessController.doPrivileged(new PrivilegedStoreKeys());
                } catch (PrivilegedActionException e) {
                    this.log.error(sm.getString("persistentManager.storeLoadKeysError"), e.getException());
                    return;
                }
            } else {
                ids = this.store.keys();
            }
            int n = ids.length;
            if (n == 0) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("persistentManager.loading", String.valueOf(n)));
            }
            for (String id : ids) {
                try {
                    swapIn(id);
                } catch (IOException e2) {
                    this.log.error(sm.getString("persistentManager.storeLoadError"), e2);
                }
            }
        } catch (IOException e3) {
            this.log.error(sm.getString("persistentManager.storeLoadKeysError"), e3);
        }
    }

    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.Manager
    public void remove(Session session, boolean update) {
        super.remove(session, update);
        if (this.store != null) {
            removeSession(session.getIdInternal());
        }
    }

    protected void removeSession(String id) {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreRemove(id));
                } catch (PrivilegedActionException e) {
                    this.log.error(sm.getString("persistentManager.removeError"), e.getException());
                }
            } else {
                this.store.remove(id);
            }
        } catch (IOException e2) {
            this.log.error(sm.getString("persistentManager.removeError"), e2);
        }
    }

    @Override // org.apache.catalina.Manager
    public void unload() {
        Session[] sessions;
        int n;
        if (this.store == null || (n = (sessions = findSessions()).length) == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.unloading", String.valueOf(n)));
        }
        for (Session session : sessions) {
            try {
                swapOut(session);
            } catch (IOException e) {
            }
        }
    }

    @Override // org.apache.catalina.DistributedManager
    public int getActiveSessionsFull() {
        int result = getActiveSessions();
        try {
            result += getStore().getSize();
        } catch (IOException e) {
            this.log.warn(sm.getString("persistentManager.storeSizeException"));
        }
        return result;
    }

    @Override // org.apache.catalina.DistributedManager
    public Set<String> getSessionIdsFull() {
        Set<String> sessionIds = new HashSet<>(this.sessions.keySet());
        try {
            sessionIds.addAll(Arrays.asList(getStore().keys()));
        } catch (IOException e) {
            this.log.warn(sm.getString("persistentManager.storeKeysException"));
        }
        return sessionIds;
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x0060, code lost:            if (r9.equals(r0.getId()) == false) goto L25;     */
    /* JADX WARN: Finally extract failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected org.apache.catalina.Session swapIn(java.lang.String r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 243
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.session.PersistentManagerBase.swapIn(java.lang.String):org.apache.catalina.Session");
    }

    private void reactivateLoadedSession(String id, Session session) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.swapIn", id));
        }
        session.setManager(this);
        ((StandardSession) session).tellNew();
        add(session);
        ((StandardSession) session).activate();
        session.access();
        session.endAccess();
    }

    private Session loadSessionFromStore(String id) throws IOException {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                return securedStoreLoad(id);
            }
            return this.store.load(id);
        } catch (ClassNotFoundException e) {
            String msg = sm.getString("persistentManager.deserializeError", id);
            this.log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }

    private Session securedStoreLoad(String id) throws IOException, ClassNotFoundException {
        try {
            return (Session) AccessController.doPrivileged(new PrivilegedStoreLoad(id));
        } catch (PrivilegedActionException ex) {
            Exception e = ex.getException();
            this.log.error(sm.getString("persistentManager.swapInException", id), e);
            if (e instanceof IOException) {
                throw ((IOException) e);
            }
            if (e instanceof ClassNotFoundException) {
                throw ((ClassNotFoundException) e);
            }
            return null;
        }
    }

    protected void swapOut(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        ((StandardSession) session).passivate();
        writeSession(session);
        super.remove(session, true);
        session.recycle();
    }

    protected void writeSession(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreSave(session));
                } catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    if (exception instanceof IOException) {
                        throw ((IOException) exception);
                    }
                    this.log.error(sm.getString("persistentManager.serializeError", session.getIdInternal(), exception));
                }
            } else {
                this.store.save(session);
            }
        } catch (IOException e) {
            this.log.error(sm.getString("persistentManager.serializeError", session.getIdInternal(), e));
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.store == null) {
            this.log.error("No Store configured, persistence disabled");
        } else if (this.store instanceof Lifecycle) {
            ((Lifecycle) this.store).start();
        }
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Stopping");
        }
        setState(LifecycleState.STOPPING);
        if (getStore() != null && this.saveOnRestart) {
            unload();
        } else {
            Session[] sessions = findSessions();
            for (Session value : sessions) {
                StandardSession session = (StandardSession) value;
                if (session.isValid()) {
                    session.expire();
                }
            }
        }
        if (getStore() instanceof Lifecycle) {
            ((Lifecycle) getStore()).stop();
        }
        super.stopInternal();
    }

    protected void processMaxIdleSwaps() {
        if (!getState().isAvailable() || this.maxIdleSwap < 0) {
            return;
        }
        Session[] sessions = findSessions();
        if (this.maxIdleSwap >= 0) {
            for (Session value : sessions) {
                StandardSession session = (StandardSession) value;
                synchronized (session) {
                    if (session.isValid()) {
                        int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
                        if (timeIdle >= this.maxIdleSwap && timeIdle >= this.minIdleSwap) {
                            if (session.accessCount == null || session.accessCount.get() <= 0) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug(sm.getString("persistentManager.swapMaxIdle", session.getIdInternal(), Integer.valueOf(timeIdle)));
                                }
                                try {
                                    swapOut(session);
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void processMaxActiveSwaps() {
        if (!getState().isAvailable() || this.minIdleSwap < 0 || getMaxActiveSessions() < 0) {
            return;
        }
        Session[] sessions = findSessions();
        int limit = (int) (getMaxActiveSessions() * 0.9d);
        if (limit >= sessions.length) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.tooManyActive", Integer.valueOf(sessions.length)));
        }
        int toswap = sessions.length - limit;
        for (int i = 0; i < sessions.length && toswap > 0; i++) {
            StandardSession session = (StandardSession) sessions[i];
            synchronized (session) {
                int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
                if (timeIdle >= this.minIdleSwap) {
                    if (session.accessCount == null || session.accessCount.get() <= 0) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(sm.getString("persistentManager.swapTooManyActive", session.getIdInternal(), Integer.valueOf(timeIdle)));
                        }
                        try {
                            swapOut(session);
                        } catch (IOException e) {
                        }
                        toswap--;
                    }
                }
            }
        }
    }

    protected void processMaxIdleBackups() {
        if (!getState().isAvailable() || this.maxIdleBackup < 0) {
            return;
        }
        Session[] sessions = findSessions();
        if (this.maxIdleBackup >= 0) {
            for (Session value : sessions) {
                StandardSession session = (StandardSession) value;
                synchronized (session) {
                    if (session.isValid()) {
                        long lastAccessedTime = session.getLastAccessedTimeInternal();
                        Long persistedLastAccessedTime = (Long) session.getNote(PERSISTED_LAST_ACCESSED_TIME);
                        if (persistedLastAccessedTime == null || lastAccessedTime != persistedLastAccessedTime.longValue()) {
                            int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
                            if (timeIdle >= this.maxIdleBackup) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug(sm.getString("persistentManager.backupMaxIdle", session.getIdInternal(), Integer.valueOf(timeIdle)));
                                }
                                try {
                                    writeSession(session);
                                } catch (IOException e) {
                                }
                                session.setNote(PERSISTED_LAST_ACCESSED_TIME, Long.valueOf(lastAccessedTime));
                            }
                        }
                    }
                }
            }
        }
    }
}
