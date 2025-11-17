package org.springframework.boot.autoconfigure.data.rest;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.http.MediaType;

@ConfigurationProperties(prefix = "spring.data.rest")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/rest/RepositoryRestProperties.class */
public class RepositoryRestProperties {
    private String basePath;
    private Integer defaultPageSize;
    private Integer maxPageSize;
    private String pageParamName;
    private String limitParamName;
    private String sortParamName;
    private RepositoryDetectionStrategy.RepositoryDetectionStrategies detectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.DEFAULT;
    private MediaType defaultMediaType;
    private Boolean returnBodyOnCreate;
    private Boolean returnBodyOnUpdate;
    private Boolean enableEnumTranslation;

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Integer getDefaultPageSize() {
        return this.defaultPageSize;
    }

    public void setDefaultPageSize(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public Integer getMaxPageSize() {
        return this.maxPageSize;
    }

    public void setMaxPageSize(Integer maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public String getPageParamName() {
        return this.pageParamName;
    }

    public void setPageParamName(String pageParamName) {
        this.pageParamName = pageParamName;
    }

    public String getLimitParamName() {
        return this.limitParamName;
    }

    public void setLimitParamName(String limitParamName) {
        this.limitParamName = limitParamName;
    }

    public String getSortParamName() {
        return this.sortParamName;
    }

    public void setSortParamName(String sortParamName) {
        this.sortParamName = sortParamName;
    }

    public RepositoryDetectionStrategy.RepositoryDetectionStrategies getDetectionStrategy() {
        return this.detectionStrategy;
    }

    public void setDetectionStrategy(RepositoryDetectionStrategy.RepositoryDetectionStrategies detectionStrategy) {
        this.detectionStrategy = detectionStrategy;
    }

    public MediaType getDefaultMediaType() {
        return this.defaultMediaType;
    }

    public void setDefaultMediaType(MediaType defaultMediaType) {
        this.defaultMediaType = defaultMediaType;
    }

    public Boolean getReturnBodyOnCreate() {
        return this.returnBodyOnCreate;
    }

    public void setReturnBodyOnCreate(Boolean returnBodyOnCreate) {
        this.returnBodyOnCreate = returnBodyOnCreate;
    }

    public Boolean getReturnBodyOnUpdate() {
        return this.returnBodyOnUpdate;
    }

    public void setReturnBodyOnUpdate(Boolean returnBodyOnUpdate) {
        this.returnBodyOnUpdate = returnBodyOnUpdate;
    }

    public Boolean getEnableEnumTranslation() {
        return this.enableEnumTranslation;
    }

    public void setEnableEnumTranslation(Boolean enableEnumTranslation) {
        this.enableEnumTranslation = enableEnumTranslation;
    }

    public void applyTo(RepositoryRestConfiguration rest) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from(this::getBasePath);
        Objects.requireNonNull(rest);
        from.to(rest::setBasePath);
        PropertyMapper.Source from2 = map.from(this::getDefaultPageSize);
        Objects.requireNonNull(rest);
        from2.to((v1) -> {
            r1.setDefaultPageSize(v1);
        });
        PropertyMapper.Source from3 = map.from(this::getMaxPageSize);
        Objects.requireNonNull(rest);
        from3.to((v1) -> {
            r1.setMaxPageSize(v1);
        });
        PropertyMapper.Source from4 = map.from(this::getPageParamName);
        Objects.requireNonNull(rest);
        from4.to(rest::setPageParamName);
        PropertyMapper.Source from5 = map.from(this::getLimitParamName);
        Objects.requireNonNull(rest);
        from5.to(rest::setLimitParamName);
        PropertyMapper.Source from6 = map.from(this::getSortParamName);
        Objects.requireNonNull(rest);
        from6.to(rest::setSortParamName);
        PropertyMapper.Source from7 = map.from(this::getDetectionStrategy);
        Objects.requireNonNull(rest);
        from7.to((v1) -> {
            r1.setRepositoryDetectionStrategy(v1);
        });
        PropertyMapper.Source from8 = map.from(this::getDefaultMediaType);
        Objects.requireNonNull(rest);
        from8.to(rest::setDefaultMediaType);
        PropertyMapper.Source from9 = map.from(this::getReturnBodyOnCreate);
        Objects.requireNonNull(rest);
        from9.to(rest::setReturnBodyOnCreate);
        PropertyMapper.Source from10 = map.from(this::getReturnBodyOnUpdate);
        Objects.requireNonNull(rest);
        from10.to(rest::setReturnBodyOnUpdate);
        PropertyMapper.Source from11 = map.from(this::getEnableEnumTranslation);
        Objects.requireNonNull(rest);
        from11.to((v1) -> {
            r1.setEnableEnumTranslation(v1);
        });
    }
}
