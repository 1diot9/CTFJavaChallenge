package org.apache.catalina.core;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletSecurityElement;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.ParameterMap;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationServletRegistration.class */
public class ApplicationServletRegistration implements ServletRegistration.Dynamic {
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationServletRegistration.class);
    private final Wrapper wrapper;
    private final Context context;
    private ServletSecurityElement constraint;

    public ApplicationServletRegistration(Wrapper wrapper, Context context) {
        this.wrapper = wrapper;
        this.context = context;
    }

    @Override // jakarta.servlet.Registration
    public String getClassName() {
        return this.wrapper.getServletClass();
    }

    @Override // jakarta.servlet.Registration
    public String getInitParameter(String name) {
        return this.wrapper.findInitParameter(name);
    }

    @Override // jakarta.servlet.Registration
    public Map<String, String> getInitParameters() {
        ParameterMap<String, String> result = new ParameterMap<>();
        String[] parameterNames = this.wrapper.findInitParameters();
        for (String parameterName : parameterNames) {
            result.put(parameterName, this.wrapper.findInitParameter(parameterName));
        }
        result.setLocked(true);
        return result;
    }

    @Override // jakarta.servlet.Registration
    public String getName() {
        return this.wrapper.getName();
    }

    @Override // jakarta.servlet.Registration
    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParam", name, value));
        }
        if (getInitParameter(name) != null) {
            return false;
        }
        this.wrapper.addInitParameter(name, value);
        return true;
    }

    @Override // jakarta.servlet.Registration
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String> conflicts = new HashSet<>();
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParams", entry.getKey(), entry.getValue()));
            }
            if (getInitParameter(entry.getKey()) != null) {
                conflicts.add(entry.getKey());
            }
        }
        if (conflicts.isEmpty()) {
            for (Map.Entry<String, String> entry2 : initParameters.entrySet()) {
                setInitParameter(entry2.getKey(), entry2.getValue());
            }
        }
        return conflicts;
    }

    @Override // jakarta.servlet.Registration.Dynamic
    public void setAsyncSupported(boolean asyncSupported) {
        this.wrapper.setAsyncSupported(asyncSupported);
    }

    @Override // jakarta.servlet.ServletRegistration.Dynamic
    public void setLoadOnStartup(int loadOnStartup) {
        this.wrapper.setLoadOnStartup(loadOnStartup);
    }

    @Override // jakarta.servlet.ServletRegistration.Dynamic
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.wrapper.setMultipartConfigElement(multipartConfig);
    }

    @Override // jakarta.servlet.ServletRegistration.Dynamic
    public void setRunAsRole(String roleName) {
        this.wrapper.setRunAs(roleName);
    }

    @Override // jakarta.servlet.ServletRegistration.Dynamic
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException(sm.getString("applicationServletRegistration.setServletSecurity.iae", getName(), this.context.getName()));
        }
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationServletRegistration.setServletSecurity.ise", getName(), this.context.getName()));
        }
        this.constraint = constraint;
        return this.context.addServletSecurity(this, constraint);
    }

    @Override // jakarta.servlet.ServletRegistration
    public Set<String> addMapping(String... urlPatterns) {
        if (urlPatterns == null) {
            return Collections.emptySet();
        }
        Set<String> conflicts = new HashSet<>();
        for (String urlPattern : urlPatterns) {
            String wrapperName = this.context.findServletMapping(urlPattern);
            if (wrapperName != null) {
                Wrapper wrapper = (Wrapper) this.context.findChild(wrapperName);
                if (wrapper.isOverridable()) {
                    this.context.removeServletMapping(urlPattern);
                } else {
                    conflicts.add(urlPattern);
                }
            }
        }
        if (!conflicts.isEmpty()) {
            return conflicts;
        }
        for (String str : urlPatterns) {
            this.context.addServletMappingDecoded(UDecoder.URLDecode(str, StandardCharsets.UTF_8), this.wrapper.getName());
        }
        if (this.constraint != null) {
            this.context.addServletSecurity(this, this.constraint);
        }
        return Collections.emptySet();
    }

    @Override // jakarta.servlet.ServletRegistration
    public Collection<String> getMappings() {
        Set<String> result = new HashSet<>();
        String servletName = this.wrapper.getName();
        String[] urlPatterns = this.context.findServletMappings();
        for (String urlPattern : urlPatterns) {
            String name = this.context.findServletMapping(urlPattern);
            if (name.equals(servletName)) {
                result.add(urlPattern);
            }
        }
        return result;
    }

    @Override // jakarta.servlet.ServletRegistration
    public String getRunAsRole() {
        return this.wrapper.getRunAs();
    }
}
