package org.apache.catalina.util;

import ch.qos.logback.core.net.ssl.SSL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.SessionIdGenerator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/SessionIdGeneratorBase.class */
public abstract class SessionIdGeneratorBase extends LifecycleBase implements SessionIdGenerator {
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");
    public static final String DEFAULT_SECURE_RANDOM_ALGORITHM;
    private final Log log = LogFactory.getLog((Class<?>) SessionIdGeneratorBase.class);
    private final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue();
    private String secureRandomClass = null;
    private String secureRandomAlgorithm = DEFAULT_SECURE_RANDOM_ALGORITHM;
    private String secureRandomProvider = null;
    private String jvmRoute = "";
    private int sessionIdLength = 16;

    static {
        Set<String> algorithmNames = Security.getAlgorithms("SecureRandom");
        if (algorithmNames.contains(SSL.DEFAULT_SECURE_RANDOM_ALGORITHM)) {
            DEFAULT_SECURE_RANDOM_ALGORITHM = SSL.DEFAULT_SECURE_RANDOM_ALGORITHM;
            return;
        }
        DEFAULT_SECURE_RANDOM_ALGORITHM = "";
        Log log = LogFactory.getLog((Class<?>) SessionIdGeneratorBase.class);
        log.warn(sm.getString("sessionIdGeneratorBase.noSHA1PRNG"));
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    @Override // org.apache.catalina.SessionIdGenerator
    public String getJvmRoute() {
        return this.jvmRoute;
    }

    @Override // org.apache.catalina.SessionIdGenerator
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }

    @Override // org.apache.catalina.SessionIdGenerator
    public int getSessionIdLength() {
        return this.sessionIdLength;
    }

    @Override // org.apache.catalina.SessionIdGenerator
    public void setSessionIdLength(int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }

    @Override // org.apache.catalina.SessionIdGenerator
    public String generateSessionId() {
        return generateSessionId(this.jvmRoute);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void getRandomBytes(byte[] bytes) {
        SecureRandom random = this.randoms.poll();
        if (random == null) {
            random = createSecureRandom();
        }
        random.nextBytes(bytes);
        this.randoms.add(random);
    }

    private SecureRandom createSecureRandom() {
        SecureRandom result = null;
        long t1 = System.currentTimeMillis();
        if (this.secureRandomClass != null) {
            try {
                Class<?> clazz = Class.forName(this.secureRandomClass);
                result = (SecureRandom) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                this.log.error(sm.getString("sessionIdGeneratorBase.random", this.secureRandomClass), e);
            }
        }
        boolean error = false;
        if (result == null) {
            try {
                if (this.secureRandomProvider != null && this.secureRandomProvider.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm, this.secureRandomProvider);
                } else if (this.secureRandomAlgorithm != null && this.secureRandomAlgorithm.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm);
                }
            } catch (NoSuchAlgorithmException e2) {
                error = true;
                this.log.error(sm.getString("sessionIdGeneratorBase.randomAlgorithm", this.secureRandomAlgorithm), e2);
            } catch (NoSuchProviderException e3) {
                error = true;
                this.log.error(sm.getString("sessionIdGeneratorBase.randomProvider", this.secureRandomProvider), e3);
            }
        }
        if (result == null && error && !DEFAULT_SECURE_RANDOM_ALGORITHM.equals(this.secureRandomAlgorithm)) {
            try {
                result = SecureRandom.getInstance(DEFAULT_SECURE_RANDOM_ALGORITHM);
            } catch (NoSuchAlgorithmException e4) {
                this.log.error(sm.getString("sessionIdGeneratorBase.randomAlgorithm", this.secureRandomAlgorithm), e4);
            }
        }
        if (result == null) {
            result = new SecureRandom();
        }
        result.nextInt();
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > 100) {
            this.log.warn(sm.getString("sessionIdGeneratorBase.createRandom", result.getAlgorithm(), Long.valueOf(t2 - t1)));
        }
        return result;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        generateSessionId();
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        this.randoms.clear();
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() throws LifecycleException {
    }
}
