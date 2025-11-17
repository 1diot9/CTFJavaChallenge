package org.springframework.boot.web.embedded.undertow;

import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.SessionPersistenceManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.ConfigurableObjectInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/FileSessionPersistence.class */
class FileSessionPersistence implements SessionPersistenceManager {
    private final File dir;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileSessionPersistence(File dir) {
        this.dir = dir;
    }

    public void persistSessions(String deploymentName, Map<String, SessionPersistenceManager.PersistentSession> sessionData) {
        try {
            save(sessionData, getSessionFile(deploymentName));
        } catch (Exception ex) {
            UndertowServletLogger.ROOT_LOGGER.failedToPersistSessions(ex);
        }
    }

    private void save(Map<String, SessionPersistenceManager.PersistentSession> sessionData, File file) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
        try {
            save(sessionData, stream);
            stream.close();
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void save(Map<String, SessionPersistenceManager.PersistentSession> sessionData, ObjectOutputStream stream) throws IOException {
        Map<String, Serializable> session = new LinkedHashMap<>();
        sessionData.forEach((key, value) -> {
            session.put(key, new SerializablePersistentSession(value));
        });
        stream.writeObject(session);
    }

    public Map<String, SessionPersistenceManager.PersistentSession> loadSessionAttributes(String deploymentName, final ClassLoader classLoader) {
        try {
            File file = getSessionFile(deploymentName);
            if (file.exists()) {
                return load(file, classLoader);
            }
            return null;
        } catch (Exception ex) {
            UndertowServletLogger.ROOT_LOGGER.failedtoLoadPersistentSessions(ex);
            return null;
        }
    }

    private Map<String, SessionPersistenceManager.PersistentSession> load(File file, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ConfigurableObjectInputStream(new FileInputStream(file), classLoader);
        try {
            Map<String, SessionPersistenceManager.PersistentSession> load = load(stream);
            stream.close();
            return load;
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private Map<String, SessionPersistenceManager.PersistentSession> load(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        Map<String, SerializablePersistentSession> session = readSession(stream);
        long time = System.currentTimeMillis();
        Map<String, SessionPersistenceManager.PersistentSession> result = new LinkedHashMap<>();
        session.forEach((key, value) -> {
            SessionPersistenceManager.PersistentSession entrySession = value.getPersistentSession();
            if (entrySession.getExpiration().getTime() > time) {
                result.put(key, entrySession);
            }
        });
        return result;
    }

    private Map<String, SerializablePersistentSession> readSession(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        return (Map) stream.readObject();
    }

    private File getSessionFile(String deploymentName) {
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }
        return new File(this.dir, deploymentName + ".session");
    }

    public void clear(String deploymentName) {
        getSessionFile(deploymentName).delete();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/FileSessionPersistence$SerializablePersistentSession.class */
    public static class SerializablePersistentSession implements Serializable {
        private static final long serialVersionUID = 0;
        private final Date expiration;
        private final Map<String, Object> sessionData;

        SerializablePersistentSession(SessionPersistenceManager.PersistentSession session) {
            this.expiration = session.getExpiration();
            this.sessionData = new LinkedHashMap(session.getSessionData());
        }

        SessionPersistenceManager.PersistentSession getPersistentSession() {
            return new SessionPersistenceManager.PersistentSession(this.expiration, this.sessionData);
        }
    }
}
