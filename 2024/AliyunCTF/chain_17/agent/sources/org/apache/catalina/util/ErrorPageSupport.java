package org.apache.catalina.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/ErrorPageSupport.class */
public class ErrorPageSupport {
    private Map<String, ErrorPage> exceptionPages = new ConcurrentHashMap();
    private Map<Integer, ErrorPage> statusPages = new ConcurrentHashMap();

    public void add(ErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.put(Integer.valueOf(errorPage.getErrorCode()), errorPage);
        } else {
            this.exceptionPages.put(exceptionType, errorPage);
        }
    }

    public void remove(ErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.remove(Integer.valueOf(errorPage.getErrorCode()), errorPage);
        } else {
            this.exceptionPages.remove(exceptionType, errorPage);
        }
    }

    public ErrorPage find(int statusCode) {
        return this.statusPages.get(Integer.valueOf(statusCode));
    }

    public ErrorPage find(String exceptionType) {
        return this.exceptionPages.get(exceptionType);
    }

    public ErrorPage find(Throwable exceptionType) {
        if (exceptionType == null) {
            return null;
        }
        Class<?> clazz = exceptionType.getClass();
        String name = clazz.getName();
        while (true) {
            String name2 = name;
            if (!Object.class.equals(clazz)) {
                ErrorPage errorPage = this.exceptionPages.get(name2);
                if (errorPage != null) {
                    return errorPage;
                }
                clazz = clazz.getSuperclass();
                if (clazz != null) {
                    name = clazz.getName();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public ErrorPage[] findAll() {
        Set<ErrorPage> errorPages = new HashSet<>();
        errorPages.addAll(this.exceptionPages.values());
        errorPages.addAll(this.statusPages.values());
        return (ErrorPage[]) errorPages.toArray(new ErrorPage[0]);
    }
}
