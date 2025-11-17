package org.apache.catalina.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.LibraryNotFoundError;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/AprLifecycleListener.class */
public class AprLifecycleListener implements LifecycleListener {
    protected static final int TCN_REQUIRED_MAJOR = 1;
    protected static final int TCN_REQUIRED_MINOR = 2;
    protected static final int TCN_REQUIRED_PATCH = 34;
    protected static final int TCN_RECOMMENDED_MAJOR = 2;
    protected static final int TCN_RECOMMENDED_MINOR = 0;
    protected static final int TCN_RECOMMENDED_PV = 5;
    private static final int FIPS_ON = 1;
    private static final int FIPS_OFF = 0;
    private static final Log log = LogFactory.getLog((Class<?>) AprLifecycleListener.class);
    private static final List<String> initInfoLogMessages = new ArrayList(3);
    protected static final StringManager sm = StringManager.getManager((Class<?>) AprLifecycleListener.class);
    private static int tcnMajor = 0;
    private static int tcnMinor = 0;
    private static int tcnPatch = 0;
    private static int tcnVersion = 0;
    protected static String SSLEngine = CustomBooleanEditor.VALUE_ON;
    protected static String FIPSMode = CustomBooleanEditor.VALUE_OFF;
    protected static String SSLRandomSeed = "builtin";
    protected static boolean sslInitialized = false;
    protected static boolean fipsModeActive = false;
    protected static final Object lock = new Object();

    public static boolean isAprAvailable() {
        if (AprStatus.isInstanceCreated()) {
            synchronized (lock) {
                init();
            }
        }
        return AprStatus.isAprAvailable();
    }

    public AprLifecycleListener() {
        AprStatus.setInstanceCreated(true);
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
            synchronized (lock) {
                if (!(event.getLifecycle() instanceof Server)) {
                    log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
                }
                init();
                for (String msg : initInfoLogMessages) {
                    log.info(msg);
                }
                initInfoLogMessages.clear();
                if (AprStatus.isAprAvailable()) {
                    try {
                        initializeSSL();
                    } catch (Throwable t) {
                        Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
                        ExceptionUtils.handleThrowable(t2);
                        log.error(sm.getString("aprListener.sslInit"), t2);
                    }
                }
                if (null != FIPSMode && !CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(FIPSMode) && !isFIPSModeActive()) {
                    String errorMessage = sm.getString("aprListener.initializeFIPSFailed");
                    Error e = new Error(errorMessage);
                    log.fatal(errorMessage, e);
                    throw e;
                }
            }
            return;
        }
        if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getType())) {
            synchronized (lock) {
                if (AprStatus.isAprAvailable()) {
                    try {
                        terminateAPR();
                    } catch (Throwable t3) {
                        ExceptionUtils.handleThrowable(ExceptionUtils.unwrapInvocationTargetException(t3));
                        log.info(sm.getString("aprListener.aprDestroy"));
                    }
                }
            }
        }
    }

    private static void terminateAPR() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = Class.forName("org.apache.tomcat.jni.Library").getMethod("terminate", (Class[]) null);
        method.invoke(null, (Object[]) null);
        AprStatus.setAprAvailable(false);
        AprStatus.setAprInitialized(false);
        sslInitialized = false;
        fipsModeActive = false;
    }

    private static void init() {
        if (AprStatus.isAprInitialized()) {
            return;
        }
        AprStatus.setAprInitialized(true);
        try {
            Library.initialize(null);
            tcnMajor = Library.TCN_MAJOR_VERSION;
            tcnMinor = Library.TCN_MINOR_VERSION;
            tcnPatch = Library.TCN_PATCH_VERSION;
            tcnVersion = (tcnMajor * 1000) + (tcnMinor * 100) + tcnPatch;
            if (tcnMajor <= 1 || !CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(SSLEngine)) {
                if (tcnVersion >= 1234) {
                    if (tcnVersion < 2005) {
                        initInfoLogMessages.add(sm.getString("aprListener.tcnVersion", Library.versionString(), "2.0.5"));
                    }
                    initInfoLogMessages.add(sm.getString("aprListener.tcnValid", Library.versionString(), Library.aprVersionString()));
                    AprStatus.setAprAvailable(true);
                    return;
                }
                log.error(sm.getString("aprListener.tcnInvalid", Library.versionString(), "1.2.34"));
                try {
                    terminateAPR();
                    return;
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(ExceptionUtils.unwrapInvocationTargetException(t));
                    return;
                }
            }
            log.error(sm.getString("aprListener.sslRequired", SSLEngine, Library.versionString()));
            try {
                terminateAPR();
            } catch (Throwable t2) {
                ExceptionUtils.handleThrowable(ExceptionUtils.unwrapInvocationTargetException(t2));
            }
        } catch (LibraryNotFoundError lnfe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("aprListener.aprInitDebug", lnfe.getLibraryNames(), System.getProperty("java.library.path"), lnfe.getMessage()), lnfe);
            }
            initInfoLogMessages.add(sm.getString("aprListener.aprInit", System.getProperty("java.library.path")));
        } catch (Throwable t3) {
            Throwable t4 = ExceptionUtils.unwrapInvocationTargetException(t3);
            ExceptionUtils.handleThrowable(t4);
            log.warn(sm.getString("aprListener.aprInitError", t4.getMessage()), t4);
        }
    }

    private static void initializeSSL() throws Exception {
        boolean enterFipsMode;
        if (CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(SSLEngine) || sslInitialized) {
            return;
        }
        sslInitialized = true;
        Class<?>[] paramTypes = {String.class};
        Object[] paramValues = {SSLRandomSeed};
        Class<?> clazz = Class.forName("org.apache.tomcat.jni.SSL");
        Method method = clazz.getMethod("randSet", paramTypes);
        method.invoke(null, paramValues);
        paramValues[0] = CustomBooleanEditor.VALUE_ON.equalsIgnoreCase(SSLEngine) ? null : SSLEngine;
        Method method2 = clazz.getMethod("initialize", paramTypes);
        method2.invoke(null, paramValues);
        boolean usingProviders = tcnMajor > 1 || (tcnVersion > 1233 && (((long) SSL.version()) & 4026531840L) > 536870912);
        if (usingProviders || (null != FIPSMode && !CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(FIPSMode))) {
            fipsModeActive = false;
            int fipsModeState = SSL.fipsModeGet();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("aprListener.currentFIPSMode", Integer.valueOf(fipsModeState)));
            }
            if (null == FIPSMode || CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 1) {
                    fipsModeActive = true;
                }
                enterFipsMode = false;
            } else if (CustomBooleanEditor.VALUE_ON.equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 1) {
                    if (!usingProviders) {
                        log.info(sm.getString("aprListener.skipFIPSInitialization"));
                    }
                    fipsModeActive = true;
                    enterFipsMode = false;
                } else {
                    if (usingProviders) {
                        throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", FIPSMode));
                    }
                    enterFipsMode = true;
                }
            } else if ("require".equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 1) {
                    fipsModeActive = true;
                    enterFipsMode = false;
                } else {
                    if (usingProviders) {
                        throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", FIPSMode));
                    }
                    throw new IllegalStateException(sm.getString("aprListener.requireNotInFIPSMode"));
                }
            } else if ("enter".equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 0) {
                    if (usingProviders) {
                        throw new IllegalStateException(sm.getString("aprListener.FIPSProviderNotDefault", FIPSMode));
                    }
                    enterFipsMode = true;
                } else if (usingProviders) {
                    fipsModeActive = true;
                    enterFipsMode = false;
                } else {
                    throw new IllegalStateException(sm.getString("aprListener.enterAlreadyInFIPSMode", Integer.valueOf(fipsModeState)));
                }
            } else {
                throw new IllegalArgumentException(sm.getString("aprListener.wrongFIPSMode", FIPSMode));
            }
            if (enterFipsMode) {
                log.info(sm.getString("aprListener.initializingFIPS"));
                if (SSL.fipsModeSet(1) != 1) {
                    String message = sm.getString("aprListener.initializeFIPSFailed");
                    log.error(message);
                    throw new IllegalStateException(message);
                }
                fipsModeActive = true;
                log.info(sm.getString("aprListener.initializeFIPSSuccess"));
            }
            if (usingProviders && fipsModeActive) {
                log.info(sm.getString("aprListener.usingFIPSProvider"));
            }
        }
        log.info(sm.getString("aprListener.initializedOpenSSL", SSL.versionString()));
    }

    public String getSSLEngine() {
        return SSLEngine;
    }

    public void setSSLEngine(String SSLEngine2) {
        if (!SSLEngine2.equals(SSLEngine)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLEngine"));
            }
            SSLEngine = SSLEngine2;
        }
    }

    public String getSSLRandomSeed() {
        return SSLRandomSeed;
    }

    public void setSSLRandomSeed(String SSLRandomSeed2) {
        if (!SSLRandomSeed2.equals(SSLRandomSeed)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLRandomSeed"));
            }
            SSLRandomSeed = SSLRandomSeed2;
        }
    }

    public String getFIPSMode() {
        return FIPSMode;
    }

    public void setFIPSMode(String FIPSMode2) {
        if (!FIPSMode2.equals(FIPSMode)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForFIPSMode"));
            }
            FIPSMode = FIPSMode2;
        }
    }

    public boolean isFIPSModeActive() {
        return fipsModeActive;
    }

    public void setUseOpenSSL(boolean useOpenSSL) {
        if (useOpenSSL != AprStatus.getUseOpenSSL()) {
            AprStatus.setUseOpenSSL(useOpenSSL);
        }
    }

    public static boolean getUseOpenSSL() {
        return AprStatus.getUseOpenSSL();
    }

    public static boolean isInstanceCreated() {
        return AprStatus.isInstanceCreated();
    }
}
