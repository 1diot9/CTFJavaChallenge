package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/StandardManager.class */
public class StandardManager extends ManagerBase {
    protected static final String name = "StandardManager";
    private final Log log = LogFactory.getLog((Class<?>) StandardManager.class);
    protected String pathname = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/StandardManager$PrivilegedDoLoad.class */
    public class PrivilegedDoLoad implements PrivilegedExceptionAction<Void> {
        PrivilegedDoLoad() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            StandardManager.this.doLoad();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/StandardManager$PrivilegedDoUnload.class */
    public class PrivilegedDoUnload implements PrivilegedExceptionAction<Void> {
        PrivilegedDoUnload() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            StandardManager.this.doUnload();
            return null;
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        String oldPathname = this.pathname;
        this.pathname = pathname;
        this.support.firePropertyChange("pathname", oldPathname, this.pathname);
    }

    @Override // org.apache.catalina.Manager
    public void load() throws ClassNotFoundException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoLoad());
                return;
            } catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof ClassNotFoundException) {
                    throw ((ClassNotFoundException) exception);
                }
                if (exception instanceof IOException) {
                    throw ((IOException) exception);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unreported exception in load() ", exception);
                    return;
                }
                return;
            }
        }
        doLoad();
    }

    protected void doLoad() throws ClassNotFoundException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Start: Loading persisted sessions");
        }
        this.sessions.clear();
        File file = file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.loading", this.pathname));
        }
        ClassLoader classLoader = null;
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            try {
                BufferedInputStream bis = new BufferedInputStream(fis);
                try {
                    Context c = getContext();
                    Loader loader = c.getLoader();
                    Log logger = c.getLogger();
                    if (loader != null) {
                        classLoader = loader.getClassLoader();
                    }
                    if (classLoader == null) {
                        classLoader = getClass().getClassLoader();
                    }
                    synchronized (this.sessions) {
                        try {
                            ObjectInputStream ois = new CustomObjectInputStream(bis, classLoader, logger, getSessionAttributeValueClassNamePattern(), getWarnOnSessionAttributeFilterFailure());
                            try {
                                Integer count = (Integer) ois.readObject();
                                int n = count.intValue();
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug("Loading " + n + " persisted sessions");
                                }
                                for (int i = 0; i < n; i++) {
                                    StandardSession session = getNewSession();
                                    session.readObjectData(ois);
                                    session.setManager(this);
                                    this.sessions.put(session.getIdInternal(), session);
                                    session.activate();
                                    if (!session.isValidInternal()) {
                                        session.setValid(true);
                                        session.expire();
                                    }
                                    this.sessionCounter++;
                                }
                                ois.close();
                                if (file.exists() && !file.delete()) {
                                    this.log.warn(sm.getString("standardManager.deletePersistedFileFail", file));
                                }
                            } catch (Throwable th) {
                                try {
                                    ois.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            if (file.exists() && !file.delete()) {
                                this.log.warn(sm.getString("standardManager.deletePersistedFileFail", file));
                            }
                            throw th3;
                        }
                    }
                    bis.close();
                    fis.close();
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Finish: Loading persisted sessions");
                    }
                } catch (Throwable th4) {
                    try {
                        bis.close();
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                    }
                    throw th4;
                }
            } finally {
            }
        } catch (FileNotFoundException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("No persisted data file found");
            }
        }
    }

    @Override // org.apache.catalina.Manager
    public void unload() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoUnload());
                return;
            } catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof IOException) {
                    throw ((IOException) exception);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unreported exception in unLoad()", exception);
                    return;
                }
                return;
            }
        }
        doUnload();
    }

    protected void doUnload() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.unloading.debug"));
        }
        if (this.sessions.isEmpty()) {
            this.log.debug(sm.getString("standardManager.unloading.nosessions"));
            return;
        }
        File file = file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.unloading", this.pathname));
        }
        List<StandardSession> list = new ArrayList<>();
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        try {
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                try {
                    synchronized (this.sessions) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Unloading " + this.sessions.size() + " sessions");
                        }
                        oos.writeObject(Integer.valueOf(this.sessions.size()));
                        for (Session s : this.sessions.values()) {
                            StandardSession session = (StandardSession) s;
                            list.add(session);
                            session.passivate();
                            session.writeObjectData(oos);
                        }
                    }
                    oos.close();
                    bos.close();
                    fos.close();
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Expiring " + list.size() + " persisted sessions");
                    }
                    for (StandardSession session2 : list) {
                        try {
                            try {
                                session2.expire(false);
                                session2.recycle();
                            } catch (Throwable t) {
                                ExceptionUtils.handleThrowable(t);
                                session2.recycle();
                            }
                        } catch (Throwable th) {
                            session2.recycle();
                            throw th;
                        }
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Unloading complete");
                    }
                } catch (Throwable th2) {
                    try {
                        oos.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } finally {
            }
        } catch (Throwable th4) {
            try {
                fos.close();
            } catch (Throwable th5) {
                th4.addSuppressed(th5);
            }
            throw th4;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            load();
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error(sm.getString("standardManager.managerLoad"), t);
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
        try {
            unload();
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error(sm.getString("standardManager.managerUnload"), t);
        }
        Session[] sessions = findSessions();
        for (Session session : sessions) {
            try {
                try {
                    if (session.isValid()) {
                        session.expire();
                    }
                    session.recycle();
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    session.recycle();
                }
            } catch (Throwable th) {
                session.recycle();
                throw th;
            }
        }
        super.stopInternal();
    }

    protected File file() {
        if (this.pathname == null || this.pathname.length() == 0) {
            return null;
        }
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            Context context = getContext();
            ServletContext servletContext = context.getServletContext();
            File tempdir = (File) servletContext.getAttribute("jakarta.servlet.context.tempdir");
            if (tempdir != null) {
                file = new File(tempdir, this.pathname);
            }
        }
        return file;
    }
}
