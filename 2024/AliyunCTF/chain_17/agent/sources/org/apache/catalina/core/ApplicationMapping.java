package org.apache.catalina.core;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.MappingMatch;
import org.apache.catalina.mapper.MappingData;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationMapping.class */
public class ApplicationMapping {
    private final MappingData mappingData;
    private volatile HttpServletMapping mapping = null;

    public ApplicationMapping(MappingData mappingData) {
        this.mappingData = mappingData;
    }

    public HttpServletMapping getHttpServletMapping() {
        String servletName;
        String matchValue;
        if (this.mapping == null) {
            if (this.mappingData.wrapper == null) {
                servletName = "";
            } else {
                servletName = this.mappingData.wrapper.getName();
            }
            if (this.mappingData.matchType == null) {
                this.mapping = new MappingImpl("", "", null, servletName);
            } else {
                switch (this.mappingData.matchType) {
                    case CONTEXT_ROOT:
                        this.mapping = new MappingImpl("", "", this.mappingData.matchType, servletName);
                        break;
                    case DEFAULT:
                        this.mapping = new MappingImpl("", "/", this.mappingData.matchType, servletName);
                        break;
                    case EXACT:
                        this.mapping = new MappingImpl(this.mappingData.wrapperPath.toString().substring(1), this.mappingData.wrapperPath.toString(), this.mappingData.matchType, servletName);
                        break;
                    case EXTENSION:
                        String path = this.mappingData.wrapperPath.toString();
                        int extIndex = path.lastIndexOf(46);
                        this.mapping = new MappingImpl(path.substring(1, extIndex), "*" + path.substring(extIndex), this.mappingData.matchType, servletName);
                        break;
                    case PATH:
                        if (this.mappingData.pathInfo.isNull()) {
                            matchValue = null;
                        } else {
                            matchValue = this.mappingData.pathInfo.toString().substring(1);
                        }
                        this.mapping = new MappingImpl(matchValue, this.mappingData.wrapperPath.toString() + "/*", this.mappingData.matchType, servletName);
                        break;
                }
            }
        }
        return this.mapping;
    }

    public void recycle() {
        this.mapping = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationMapping$MappingImpl.class */
    public static class MappingImpl implements HttpServletMapping {
        private final String matchValue;
        private final String pattern;
        private final MappingMatch mappingType;
        private final String servletName;

        MappingImpl(String matchValue, String pattern, MappingMatch mappingType, String servletName) {
            this.matchValue = matchValue;
            this.pattern = pattern;
            this.mappingType = mappingType;
            this.servletName = servletName;
        }

        @Override // jakarta.servlet.http.HttpServletMapping
        public String getMatchValue() {
            return this.matchValue;
        }

        @Override // jakarta.servlet.http.HttpServletMapping
        public String getPattern() {
            return this.pattern;
        }

        @Override // jakarta.servlet.http.HttpServletMapping
        public MappingMatch getMappingMatch() {
            return this.mappingType;
        }

        @Override // jakarta.servlet.http.HttpServletMapping
        public String getServletName() {
            return this.servletName;
        }
    }
}
