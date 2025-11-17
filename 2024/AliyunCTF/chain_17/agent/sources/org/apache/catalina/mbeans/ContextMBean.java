package org.apache.catalina.mbeans;

import javax.management.MBeanException;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ContextMBean.class */
public class ContextMBean extends BaseCatalinaMBean<Context> {
    public String[] findApplicationParameters() throws MBeanException {
        Context context = doGetManagedResource();
        ApplicationParameter[] params = context.findApplicationParameters();
        String[] stringParams = new String[params.length];
        for (int counter = 0; counter < params.length; counter++) {
            stringParams[counter] = params[counter].toString();
        }
        return stringParams;
    }

    public String[] findConstraints() throws MBeanException {
        Context context = doGetManagedResource();
        SecurityConstraint[] constraints = context.findConstraints();
        String[] stringConstraints = new String[constraints.length];
        for (int counter = 0; counter < constraints.length; counter++) {
            stringConstraints[counter] = constraints[counter].toString();
        }
        return stringConstraints;
    }

    public String findErrorPage(int errorCode) throws MBeanException {
        Context context = doGetManagedResource();
        ErrorPage errorPage = context.findErrorPage(errorCode);
        if (errorPage != null) {
            return errorPage.toString();
        }
        return null;
    }

    public String findErrorPage(Throwable exceptionType) throws MBeanException {
        Context context = doGetManagedResource();
        ErrorPage errorPage = context.findErrorPage(exceptionType);
        if (errorPage != null) {
            return errorPage.toString();
        }
        return null;
    }

    public String[] findErrorPages() throws MBeanException {
        Context context = doGetManagedResource();
        ErrorPage[] pages = context.findErrorPages();
        String[] stringPages = new String[pages.length];
        for (int counter = 0; counter < pages.length; counter++) {
            stringPages[counter] = pages[counter].toString();
        }
        return stringPages;
    }

    public String findFilterDef(String name) throws MBeanException {
        Context context = doGetManagedResource();
        FilterDef filterDef = context.findFilterDef(name);
        if (filterDef != null) {
            return filterDef.toString();
        }
        return null;
    }

    public String[] findFilterDefs() throws MBeanException {
        Context context = doGetManagedResource();
        FilterDef[] filterDefs = context.findFilterDefs();
        String[] stringFilters = new String[filterDefs.length];
        for (int counter = 0; counter < filterDefs.length; counter++) {
            stringFilters[counter] = filterDefs[counter].toString();
        }
        return stringFilters;
    }

    public String[] findFilterMaps() throws MBeanException {
        Context context = doGetManagedResource();
        FilterMap[] maps = context.findFilterMaps();
        String[] stringMaps = new String[maps.length];
        for (int counter = 0; counter < maps.length; counter++) {
            stringMaps[counter] = maps[counter].toString();
        }
        return stringMaps;
    }
}
