package org.apache.catalina.core;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.management.ObjectName;
import javax.naming.NamingException;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationFilterConfig.class */
public final class ApplicationFilterConfig implements FilterConfig, Serializable {
    private static final long serialVersionUID = 1;
    private transient Log log = LogFactory.getLog((Class<?>) ApplicationFilterConfig.class);
    private final transient Context context;
    private transient Filter filter;
    private final FilterDef filterDef;
    private ObjectName oname;
    static final StringManager sm = StringManager.getManager((Class<?>) ApplicationFilterConfig.class);
    private static final List<String> emptyString = Collections.emptyList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationFilterConfig(Context context, FilterDef filterDef) throws ClassCastException, ReflectiveOperationException, ServletException, NamingException, IllegalArgumentException, SecurityException {
        this.filter = null;
        this.context = context;
        this.filterDef = filterDef;
        if (filterDef.getFilter() == null) {
            getFilter();
            return;
        }
        this.filter = filterDef.getFilter();
        context.getInstanceManager().newInstance(this.filter);
        initFilter();
    }

    @Override // jakarta.servlet.FilterConfig
    public String getFilterName() {
        return this.filterDef.getFilterName();
    }

    public String getFilterClass() {
        return this.filterDef.getFilterClass();
    }

    @Override // jakarta.servlet.FilterConfig
    public String getInitParameter(String name) {
        Map<String, String> map = this.filterDef.getParameterMap();
        if (map == null) {
            return null;
        }
        return map.get(name);
    }

    @Override // jakarta.servlet.FilterConfig
    public Enumeration<String> getInitParameterNames() {
        Map<String, String> map = this.filterDef.getParameterMap();
        if (map == null) {
            return Collections.enumeration(emptyString);
        }
        return Collections.enumeration(map.keySet());
    }

    @Override // jakarta.servlet.FilterConfig
    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    public String toString() {
        return "ApplicationFilterConfig[name=" + this.filterDef.getFilterName() + ", filterClass=" + this.filterDef.getFilterClass() + ']';
    }

    public Map<String, String> getFilterInitParameterMap() {
        return Collections.unmodifiableMap(this.filterDef.getParameterMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Filter getFilter() throws ClassCastException, ReflectiveOperationException, ServletException, NamingException, IllegalArgumentException, SecurityException {
        if (this.filter != null) {
            return this.filter;
        }
        String filterClass = this.filterDef.getFilterClass();
        this.filter = (Filter) this.context.getInstanceManager().newInstance(filterClass);
        initFilter();
        return this.filter;
    }

    private void initFilter() throws ServletException {
        if ((this.context instanceof StandardContext) && this.context.getSwallowOutput()) {
            try {
                SystemLogHandler.startCapture();
                this.filter.init(this);
                String capturedlog = SystemLogHandler.stopCapture();
                if (capturedlog != null && capturedlog.length() > 0) {
                    getServletContext().log(capturedlog);
                }
            } catch (Throwable th) {
                String capturedlog2 = SystemLogHandler.stopCapture();
                if (capturedlog2 != null && capturedlog2.length() > 0) {
                    getServletContext().log(capturedlog2);
                }
                throw th;
            }
        } else {
            this.filter.init(this);
        }
        registerJMX();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilterDef getFilterDef() {
        return this.filterDef;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        unregisterJMX();
        if (this.filter != null) {
            try {
                if (Globals.IS_SECURITY_ENABLED) {
                    try {
                        SecurityUtil.doAsPrivilege("destroy", this.filter);
                        SecurityUtil.remove(this.filter);
                    } catch (Throwable th) {
                        SecurityUtil.remove(this.filter);
                        throw th;
                    }
                } else {
                    this.filter.destroy();
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.context.getLogger().error(sm.getString("applicationFilterConfig.release", this.filterDef.getFilterName(), this.filterDef.getFilterClass()), t);
            }
            if (!this.context.getIgnoreAnnotations()) {
                try {
                    this.context.getInstanceManager().destroyInstance(this.filter);
                } catch (Exception e) {
                    Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(e);
                    ExceptionUtils.handleThrowable(t2);
                    this.context.getLogger().error(sm.getString("applicationFilterConfig.preDestroy", this.filterDef.getFilterName(), this.filterDef.getFilterClass()), t2);
                }
            }
        }
        this.filter = null;
    }

    private void registerJMX() {
        String onameStr;
        String parentName = this.context.getName();
        if (!parentName.startsWith("/")) {
            parentName = "/" + parentName;
        }
        String hostName = this.context.getParent().getName();
        String hostName2 = hostName == null ? "DEFAULT" : hostName;
        String domain = this.context.getParent().getParent().getName();
        String webMod = "//" + hostName2 + parentName;
        String filterName = this.filterDef.getFilterName();
        if (Util.objectNameValueNeedsQuote(filterName)) {
            filterName = ObjectName.quote(filterName);
        }
        if (this.context instanceof StandardContext) {
            StandardContext standardContext = (StandardContext) this.context;
            onameStr = domain + ":j2eeType=Filter,WebModule=" + webMod + ",name=" + filterName + ",J2EEApplication=" + standardContext.getJ2EEApplication() + ",J2EEServer=" + standardContext.getJ2EEServer();
        } else {
            onameStr = domain + ":j2eeType=Filter,name=" + filterName + ",WebModule=" + webMod;
        }
        try {
            this.oname = new ObjectName(onameStr);
            Registry.getRegistry(null, null).registerComponent(this, this.oname, (String) null);
        } catch (Exception ex) {
            this.log.warn(sm.getString("applicationFilterConfig.jmxRegisterFail", getFilterClass(), getFilterName()), ex);
        }
    }

    private void unregisterJMX() {
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("applicationFilterConfig.jmxUnregister", getFilterClass(), getFilterName()));
                }
            } catch (Exception ex) {
                this.log.warn(sm.getString("applicationFilterConfig.jmxUnregisterFail", getFilterClass(), getFilterName()), ex);
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog((Class<?>) ApplicationFilterConfig.class);
    }
}
