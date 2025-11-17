package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/FileStore.class */
public final class FileStore extends StoreBase {
    private static final Log log = LogFactory.getLog((Class<?>) FileStore.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) FileStore.class);
    private static final String FILE_EXT = ".session";
    private String directory = ".";
    private File directoryFile = null;
    private static final String storeName = "fileStore";
    private static final String threadName = "FileStore";

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String path) {
        String oldDirectory = this.directory;
        this.directory = path;
        this.directoryFile = null;
        this.support.firePropertyChange("directory", oldDirectory, this.directory);
    }

    public String getThreadName() {
        return threadName;
    }

    @Override // org.apache.catalina.session.StoreBase
    public String getStoreName() {
        return storeName;
    }

    @Override // org.apache.catalina.Store
    public int getSize() throws IOException {
        File dir = directory();
        if (dir == null) {
            return 0;
        }
        String[] files = dir.list();
        int keycount = 0;
        if (files != null) {
            for (String file : files) {
                if (file.endsWith(FILE_EXT)) {
                    keycount++;
                }
            }
        }
        return keycount;
    }

    @Override // org.apache.catalina.Store
    public void clear() throws IOException {
        String[] keys = keys();
        for (String key : keys) {
            remove(key);
        }
    }

    @Override // org.apache.catalina.Store
    public String[] keys() throws IOException {
        File dir = directory();
        if (dir == null) {
            return new String[0];
        }
        String[] files = dir.list();
        if (files == null || files.length < 1) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        int n = FILE_EXT.length();
        for (String file : files) {
            if (file.endsWith(FILE_EXT)) {
                list.add(file.substring(0, file.length() - n));
            }
        }
        return (String[]) list.toArray(new String[0]);
    }

    @Override // org.apache.catalina.Store
    public Session load(String id) throws ClassNotFoundException, IOException {
        File file = file(id);
        if (file == null || !file.exists()) {
            return null;
        }
        Context context = getManager().getContext();
        Log contextLog = context.getLogger();
        if (contextLog.isDebugEnabled()) {
            contextLog.debug(sm.getString(getStoreName() + ".loading", id, file.getAbsolutePath()));
        }
        ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
        try {
            try {
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                try {
                    ObjectInputStream ois = getObjectInputStream(fis);
                    try {
                        StandardSession session = (StandardSession) this.manager.createEmptySession();
                        session.readObjectData(ois);
                        session.setManager(this.manager);
                        if (ois != null) {
                            ois.close();
                        }
                        fis.close();
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                        return session;
                    } catch (Throwable th) {
                        if (ois != null) {
                            try {
                                ois.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    try {
                        fis.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            } catch (FileNotFoundException e) {
                if (contextLog.isDebugEnabled()) {
                    contextLog.debug("No persisted data file found");
                }
                context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                return null;
            }
        } catch (Throwable th5) {
            context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
            throw th5;
        }
    }

    @Override // org.apache.catalina.Store
    public void remove(String id) throws IOException {
        File file = file(id);
        if (file == null) {
            return;
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".removing", id, file.getAbsolutePath()));
        }
        if (file.exists() && !file.delete()) {
            throw new IOException(sm.getString("fileStore.deleteSessionFailed", file));
        }
    }

    @Override // org.apache.catalina.Store
    public void save(Session session) throws IOException {
        File file = file(session.getIdInternal());
        if (file == null) {
            return;
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".saving", session.getIdInternal(), file.getAbsolutePath()));
        }
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            try {
                ((StandardSession) session).writeObjectData(oos);
                oos.close();
                fos.close();
            } finally {
            }
        } catch (Throwable th) {
            try {
                fos.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private File directory() throws IOException {
        if (this.directory == null) {
            return null;
        }
        if (this.directoryFile != null) {
            return this.directoryFile;
        }
        File file = new File(this.directory);
        if (!file.isAbsolute()) {
            Context context = this.manager.getContext();
            ServletContext servletContext = context.getServletContext();
            File work = (File) servletContext.getAttribute("jakarta.servlet.context.tempdir");
            file = new File(work, this.directory);
        }
        if (!file.exists() || !file.isDirectory()) {
            if (!file.delete() && file.exists()) {
                throw new IOException(sm.getString("fileStore.deleteFailed", file));
            }
            if (!file.mkdirs() && !file.isDirectory()) {
                throw new IOException(sm.getString("fileStore.createFailed", file));
            }
        }
        this.directoryFile = file;
        return file;
    }

    private File file(String id) throws IOException {
        File storageDir = directory();
        if (storageDir == null) {
            return null;
        }
        String filename = id + ".session";
        File file = new File(storageDir, filename);
        File canonicalFile = file.getCanonicalFile();
        if (!canonicalFile.toPath().startsWith(storageDir.getCanonicalFile().toPath())) {
            log.warn(sm.getString("fileStore.invalid", file.getPath(), id));
            return null;
        }
        return canonicalFile;
    }
}
