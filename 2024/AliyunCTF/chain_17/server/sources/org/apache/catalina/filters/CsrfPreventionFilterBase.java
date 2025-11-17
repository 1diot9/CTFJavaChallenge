package org.apache.catalina.filters;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/CsrfPreventionFilterBase.class */
public abstract class CsrfPreventionFilterBase extends FilterBase {
    private Random randomSource;
    private final Log log = LogFactory.getLog((Class<?>) CsrfPreventionFilterBase.class);
    private String randomClass = SecureRandom.class.getName();
    private int denyStatus = 403;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    public void setRandomClass(String randomClass) {
        this.randomClass = randomClass;
    }

    @Override // org.apache.catalina.filters.FilterBase, jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        try {
            Class<?> clazz = Class.forName(this.randomClass);
            this.randomSource = (Random) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ReflectiveOperationException e) {
            ServletException se = new ServletException(sm.getString("csrfPrevention.invalidRandomClass", this.randomClass), e);
            throw se;
        }
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected boolean isConfigProblemFatal() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String generateNonce(HttpServletRequest request) {
        byte[] random = new byte[16];
        StringBuilder buffer = new StringBuilder();
        this.randomSource.nextBytes(random);
        for (byte b : random) {
            byte b1 = (byte) ((b & 240) >> 4);
            byte b2 = (byte) (b & 15);
            if (b1 < 10) {
                buffer.append((char) (48 + b1));
            } else {
                buffer.append((char) (65 + (b1 - 10)));
            }
            if (b2 < 10) {
                buffer.append((char) (48 + b2));
            } else {
                buffer.append((char) (65 + (b2 - 10)));
            }
        }
        return buffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getRequestedPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path = path + request.getPathInfo();
        }
        return path;
    }
}
