package org.springframework.boot.autoconfigure.data.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.tags.form.InputTag;
import org.springframework.web.util.TagUtils;

@ConfigurationProperties("spring.data.web")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/web/SpringDataWebProperties.class */
public class SpringDataWebProperties {
    private final Pageable pageable = new Pageable();
    private final Sort sort = new Sort();

    public Pageable getPageable() {
        return this.pageable;
    }

    public Sort getSort() {
        return this.sort;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/web/SpringDataWebProperties$Pageable.class */
    public static class Pageable {
        private String pageParameter = TagUtils.SCOPE_PAGE;
        private String sizeParameter = InputTag.SIZE_ATTRIBUTE;
        private boolean oneIndexedParameters = false;
        private String prefix = "";
        private String qualifierDelimiter = "_";
        private int defaultPageSize = 20;
        private int maxPageSize = 2000;

        public String getPageParameter() {
            return this.pageParameter;
        }

        public void setPageParameter(String pageParameter) {
            this.pageParameter = pageParameter;
        }

        public String getSizeParameter() {
            return this.sizeParameter;
        }

        public void setSizeParameter(String sizeParameter) {
            this.sizeParameter = sizeParameter;
        }

        public boolean isOneIndexedParameters() {
            return this.oneIndexedParameters;
        }

        public void setOneIndexedParameters(boolean oneIndexedParameters) {
            this.oneIndexedParameters = oneIndexedParameters;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getQualifierDelimiter() {
            return this.qualifierDelimiter;
        }

        public void setQualifierDelimiter(String qualifierDelimiter) {
            this.qualifierDelimiter = qualifierDelimiter;
        }

        public int getDefaultPageSize() {
            return this.defaultPageSize;
        }

        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }

        public int getMaxPageSize() {
            return this.maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/web/SpringDataWebProperties$Sort.class */
    public static class Sort {
        private String sortParameter = "sort";

        public String getSortParameter() {
            return this.sortParameter;
        }

        public void setSortParameter(String sortParameter) {
            this.sortParameter = sortParameter;
        }
    }
}
