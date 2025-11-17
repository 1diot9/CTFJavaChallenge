package org.h2.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.store.fs.FileUtils;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.ThreadDeadlockDetector;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Engine.class */
public final class Engine {
    private static final Map<String, DatabaseHolder> DATABASES = new HashMap();
    private static volatile long WRONG_PASSWORD_DELAY = SysProperties.DELAY_WRONG_PASSWORD_MIN;
    private static boolean JMX;

    static {
        if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
            ThreadDeadlockDetector.init();
        }
    }

    private static SessionLocal openSession(ConnectionInfo connectionInfo, boolean z, boolean z2, String str) {
        DatabaseHolder databaseHolder;
        Database database;
        String str2;
        String name = connectionInfo.getName();
        connectionInfo.removeProperty("NO_UPGRADE", false);
        boolean property = connectionInfo.getProperty("OPEN_NEW", false);
        boolean z3 = false;
        User user = null;
        if (!connectionInfo.isUnnamedInMemory()) {
            synchronized (DATABASES) {
                databaseHolder = DATABASES.computeIfAbsent(name, str3 -> {
                    return new DatabaseHolder();
                });
            }
        } else {
            databaseHolder = new DatabaseHolder();
        }
        synchronized (databaseHolder) {
            database = databaseHolder.database;
            if (database == null || property) {
                if (connectionInfo.isPersistent()) {
                    if (connectionInfo.getProperty("MV_STORE") == null) {
                        str2 = name + Constants.SUFFIX_MV_FILE;
                        if (!FileUtils.exists(str2)) {
                            throwNotFound(z, z2, name);
                            String str4 = name + Constants.SUFFIX_OLD_DATABASE_FILE;
                            if (FileUtils.exists(str4)) {
                                throw DbException.getFileVersionError(str4);
                            }
                            str2 = null;
                        }
                    } else {
                        str2 = name + Constants.SUFFIX_MV_FILE;
                        if (!FileUtils.exists(str2)) {
                            throwNotFound(z, z2, name);
                            str2 = null;
                        }
                    }
                    if (str2 != null && !FileUtils.canWrite(str2)) {
                        connectionInfo.setProperty("ACCESS_MODE_DATA", "r");
                    }
                } else {
                    throwNotFound(z, z2, name);
                }
                database = new Database(connectionInfo, str);
                z3 = true;
                boolean z4 = false;
                Iterator<RightOwner> it = database.getAllUsersAndRoles().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (it.next() instanceof User) {
                        z4 = true;
                        break;
                    }
                }
                if (!z4) {
                    user = new User(database, database.allocateObjectId(), connectionInfo.getUserName(), false);
                    user.setAdmin(true);
                    user.setUserPasswordHash(connectionInfo.getUserPasswordHash());
                    database.setMasterUser(user);
                }
                databaseHolder.database = database;
            }
        }
        if (z3) {
            database.opened();
        }
        if (database.isClosing()) {
            return null;
        }
        if (user == null) {
            if (database.validateFilePasswordHash(str, connectionInfo.getFilePasswordHash())) {
                if (connectionInfo.getProperty("AUTHREALM") == null) {
                    user = database.findUser(connectionInfo.getUserName());
                    if (user != null && !user.validateUserPasswordHash(connectionInfo.getUserPasswordHash())) {
                        user = null;
                    }
                } else {
                    if (database.getAuthenticator() == null) {
                        throw DbException.get(ErrorCode.AUTHENTICATOR_NOT_AVAILABLE, name);
                    }
                    try {
                        user = database.getAuthenticator().authenticate(new AuthenticationInfo(connectionInfo), database);
                    } catch (AuthenticationException e) {
                        database.getTrace(2).error(e, "an error occurred during authentication; user: \"" + connectionInfo.getUserName() + "\"");
                    }
                }
            }
            if (z3 && (user == null || !user.isAdmin())) {
                database.setEventListener(null);
            }
        }
        if (user == null) {
            DbException dbException = DbException.get(ErrorCode.WRONG_USER_OR_PASSWORD);
            database.getTrace(2).error(dbException, "wrong user or password; user: \"" + connectionInfo.getUserName() + "\"");
            database.removeSession(null);
            throw dbException;
        }
        connectionInfo.cleanAuthenticationInfo();
        checkClustering(connectionInfo, database);
        SessionLocal createSession = database.createSession(user, connectionInfo.getNetworkConnectionInfo());
        if (createSession == null) {
            return null;
        }
        if (connectionInfo.getProperty("OLD_INFORMATION_SCHEMA", false)) {
            createSession.setOldInformationSchema(true);
        }
        if (connectionInfo.getProperty("JMX", false)) {
            try {
                Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.registerMBean", connectionInfo, database);
                JMX = true;
            } catch (Exception e2) {
                database.removeSession(createSession);
                throw DbException.get(ErrorCode.FEATURE_NOT_SUPPORTED_1, e2, "JMX");
            }
        }
        return createSession;
    }

    private static void throwNotFound(boolean z, boolean z2, String str) {
        if (z) {
            throw DbException.get(ErrorCode.DATABASE_NOT_FOUND_WITH_IF_EXISTS_1, str);
        }
        if (z2) {
            throw DbException.get(ErrorCode.REMOTE_DATABASE_NOT_FOUND_1, str);
        }
    }

    public static SessionLocal createSession(ConnectionInfo connectionInfo) {
        try {
            SessionLocal openSession = openSession(connectionInfo);
            validateUserAndPassword(true);
            return openSession;
        } catch (DbException e) {
            if (e.getErrorCode() == 28000) {
                validateUserAndPassword(false);
            }
            throw e;
        }
    }

    private static SessionLocal openSession(ConnectionInfo connectionInfo) {
        boolean removeProperty = connectionInfo.removeProperty("IFEXISTS", false);
        boolean removeProperty2 = connectionInfo.removeProperty("FORBID_CREATION", false);
        boolean removeProperty3 = connectionInfo.removeProperty("IGNORE_UNKNOWN_SETTINGS", false);
        String removeProperty4 = connectionInfo.removeProperty("CIPHER", (String) null);
        String removeProperty5 = connectionInfo.removeProperty("INIT", (String) null);
        long nanoTime = System.nanoTime();
        while (true) {
            SessionLocal openSession = openSession(connectionInfo, removeProperty, removeProperty2, removeProperty4);
            if (openSession != null) {
                openSession.lock();
                try {
                    openSession.setAllowLiterals(true);
                    DbSettings dbSettings = DbSettings.DEFAULT;
                    for (String str : connectionInfo.getKeys()) {
                        if (!dbSettings.containsKey(str)) {
                            String property = connectionInfo.getProperty(str);
                            StringBuilder append = new StringBuilder("SET ").append(str).append(' ');
                            if (!ParserUtil.isSimpleIdentifier(str, false, false)) {
                                if (!str.equalsIgnoreCase("TIME ZONE")) {
                                    throw DbException.get(ErrorCode.UNSUPPORTED_SETTING_1, str);
                                }
                                StringUtils.quoteStringSQL(append, property);
                            } else {
                                append.append(property);
                            }
                            try {
                                openSession.prepareLocal(append.toString()).executeUpdate(null);
                            } catch (DbException e) {
                                if (e.getErrorCode() == 90040) {
                                    openSession.getTrace().error(e, "admin rights required; user: \"" + connectionInfo.getUserName() + "\"");
                                } else {
                                    openSession.getTrace().error(e, "");
                                }
                                if (!removeProperty3) {
                                    openSession.close();
                                    throw e;
                                }
                            }
                        }
                    }
                    TimeZoneProvider timeZone = connectionInfo.getTimeZone();
                    if (timeZone != null) {
                        openSession.setTimeZone(timeZone);
                    }
                    if (removeProperty5 != null) {
                        try {
                            openSession.prepareLocal(removeProperty5).executeUpdate(null);
                        } catch (DbException e2) {
                            if (!removeProperty3) {
                                openSession.close();
                                throw e2;
                            }
                        }
                    }
                    openSession.setAllowLiterals(false);
                    openSession.commit(true);
                    openSession.unlock();
                    return openSession;
                } catch (Throwable th) {
                    openSession.unlock();
                    throw th;
                }
            }
            if (System.nanoTime() - nanoTime > DateTimeUtils.NANOS_PER_MINUTE) {
                throw DbException.get(ErrorCode.DATABASE_ALREADY_OPEN_1, "Waited for database closing longer than 1 minute");
            }
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e3) {
                throw DbException.get(ErrorCode.DATABASE_CALLED_AT_SHUTDOWN);
            }
        }
    }

    private static void checkClustering(ConnectionInfo connectionInfo, Database database) {
        String property = connectionInfo.getProperty(12, (String) null);
        if (Constants.CLUSTERING_DISABLED.equals(property)) {
            return;
        }
        String cluster = database.getCluster();
        if (!Constants.CLUSTERING_DISABLED.equals(cluster) && !Constants.CLUSTERING_ENABLED.equals(property) && !Objects.equals(property, cluster)) {
            if (cluster.equals(Constants.CLUSTERING_DISABLED)) {
                throw DbException.get(ErrorCode.CLUSTER_ERROR_DATABASE_RUNS_ALONE);
            }
            throw DbException.get(ErrorCode.CLUSTER_ERROR_DATABASE_RUNS_CLUSTERED_1, cluster);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void close(String str) {
        if (JMX) {
            try {
                Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.unregisterMBean", str);
            } catch (Exception e) {
                throw DbException.get(ErrorCode.FEATURE_NOT_SUPPORTED_1, e, "JMX");
            }
        }
        synchronized (DATABASES) {
            DATABASES.remove(str);
        }
    }

    private static void validateUserAndPassword(boolean z) {
        int i = SysProperties.DELAY_WRONG_PASSWORD_MIN;
        if (z) {
            long j = WRONG_PASSWORD_DELAY;
            if (j > i && j > 0) {
                synchronized (Engine.class) {
                    try {
                        Thread.sleep(MathUtils.secureRandomInt((int) j));
                    } catch (InterruptedException e) {
                    }
                    WRONG_PASSWORD_DELAY = i;
                }
                return;
            }
            return;
        }
        synchronized (Engine.class) {
            long j2 = WRONG_PASSWORD_DELAY;
            int i2 = SysProperties.DELAY_WRONG_PASSWORD_MAX;
            if (i2 <= 0) {
                i2 = Integer.MAX_VALUE;
            }
            WRONG_PASSWORD_DELAY += WRONG_PASSWORD_DELAY;
            if (WRONG_PASSWORD_DELAY > i2 || WRONG_PASSWORD_DELAY < 0) {
                WRONG_PASSWORD_DELAY = i2;
            }
            if (i > 0) {
                try {
                    Thread.sleep(j2 + Math.abs(MathUtils.secureRandomLong() % 100));
                } catch (InterruptedException e2) {
                }
            }
            throw DbException.get(ErrorCode.WRONG_USER_OR_PASSWORD);
        }
    }

    private Engine() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Engine$DatabaseHolder.class */
    public static final class DatabaseHolder {
        volatile Database database;

        DatabaseHolder() {
        }
    }
}
