package org.apache.catalina.core;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.Context;
import org.apache.catalina.util.ParameterMap;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationFilterRegistration.class */
public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationFilterRegistration.class);
    private final FilterDef filterDef;
    private final Context context;

    public ApplicationFilterRegistration(FilterDef filterDef, Context context) {
        this.filterDef = filterDef;
        this.context = context;
    }

    @Override // jakarta.servlet.FilterRegistration
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(this.filterDef.getFilterName());
        if (dispatcherTypes != null) {
            Iterator it = dispatcherTypes.iterator();
            while (it.hasNext()) {
                DispatcherType dispatcherType = (DispatcherType) it.next();
                filterMap.setDispatcher(dispatcherType.name());
            }
        }
        if (servletNames != null) {
            for (String servletName : servletNames) {
                filterMap.addServletName(servletName);
            }
            if (isMatchAfter) {
                this.context.addFilterMap(filterMap);
            } else {
                this.context.addFilterMapBefore(filterMap);
            }
        }
    }

    @Override // jakarta.servlet.FilterRegistration
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(this.filterDef.getFilterName());
        if (dispatcherTypes != null) {
            Iterator it = dispatcherTypes.iterator();
            while (it.hasNext()) {
                DispatcherType dispatcherType = (DispatcherType) it.next();
                filterMap.setDispatcher(dispatcherType.name());
            }
        }
        if (urlPatterns != null) {
            for (String urlPattern : urlPatterns) {
                filterMap.addURLPattern(urlPattern);
            }
            if (isMatchAfter) {
                this.context.addFilterMap(filterMap);
            } else {
                this.context.addFilterMapBefore(filterMap);
            }
        }
    }

    @Override // jakarta.servlet.FilterRegistration
    public Collection<String> getServletNameMappings() {
        Collection<String> result = new HashSet<>();
        FilterMap[] filterMaps = this.context.findFilterMaps();
        for (FilterMap filterMap : filterMaps) {
            if (filterMap.getFilterName().equals(this.filterDef.getFilterName())) {
                result.addAll(Arrays.asList(filterMap.getServletNames()));
            }
        }
        return result;
    }

    @Override // jakarta.servlet.FilterRegistration
    public Collection<String> getUrlPatternMappings() {
        Collection<String> result = new HashSet<>();
        FilterMap[] filterMaps = this.context.findFilterMaps();
        for (FilterMap filterMap : filterMaps) {
            if (filterMap.getFilterName().equals(this.filterDef.getFilterName())) {
                result.addAll(Arrays.asList(filterMap.getURLPatterns()));
            }
        }
        return result;
    }

    @Override // jakarta.servlet.Registration
    public String getClassName() {
        return this.filterDef.getFilterClass();
    }

    @Override // jakarta.servlet.Registration
    public String getInitParameter(String name) {
        return this.filterDef.getParameterMap().get(name);
    }

    @Override // jakarta.servlet.Registration
    public Map<String, String> getInitParameters() {
        ParameterMap<String, String> result = new ParameterMap<>();
        result.putAll(this.filterDef.getParameterMap());
        result.setLocked(true);
        return result;
    }

    @Override // jakarta.servlet.Registration
    public String getName() {
        return this.filterDef.getFilterName();
    }

    @Override // jakarta.servlet.Registration
    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParam", name, value));
        }
        if (getInitParameter(name) != null) {
            return false;
        }
        this.filterDef.addInitParameter(name, value);
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
        for (Map.Entry<String, String> entry2 : initParameters.entrySet()) {
            setInitParameter(entry2.getKey(), entry2.getValue());
        }
        return conflicts;
    }

    @Override // jakarta.servlet.Registration.Dynamic
    public void setAsyncSupported(boolean asyncSupported) {
        this.filterDef.setAsyncSupported(Boolean.valueOf(asyncSupported).toString());
    }
}
