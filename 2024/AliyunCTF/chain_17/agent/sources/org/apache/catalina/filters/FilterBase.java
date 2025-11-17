package org.apache.catalina.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import java.util.Enumeration;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/FilterBase.class */
public abstract class FilterBase implements Filter {
    protected static final StringManager sm = StringManager.getManager((Class<?>) FilterBase.class);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Log getLogger();

    @Override // jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> paramNames = filterConfig.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!IntrospectionUtils.setProperty(this, paramName, filterConfig.getInitParameter(paramName))) {
                String msg = sm.getString("filterbase.noSuchProperty", paramName, getClass().getName());
                if (isConfigProblemFatal()) {
                    throw new ServletException(msg);
                }
                getLogger().warn(msg);
            }
        }
    }

    protected boolean isConfigProblemFatal() {
        return false;
    }
}
