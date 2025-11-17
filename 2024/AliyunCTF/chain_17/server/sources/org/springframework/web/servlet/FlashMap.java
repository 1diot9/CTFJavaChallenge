package org.springframework.web.servlet;

import java.util.HashMap;
import java.util.Iterator;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/FlashMap.class */
public final class FlashMap extends HashMap<String, Object> implements Comparable<FlashMap> {

    @Nullable
    private String targetRequestPath;
    private final MultiValueMap<String, String> targetRequestParams = new LinkedMultiValueMap(3);
    private long expirationTime = -1;

    public void setTargetRequestPath(@Nullable String path) {
        this.targetRequestPath = path;
    }

    @Nullable
    public String getTargetRequestPath() {
        return this.targetRequestPath;
    }

    public FlashMap addTargetRequestParams(@Nullable MultiValueMap<String, String> params) {
        if (params != null) {
            params.forEach((key, values) -> {
                Iterator it = values.iterator();
                while (it.hasNext()) {
                    String value = (String) it.next();
                    addTargetRequestParam(key, value);
                }
            });
        }
        return this;
    }

    public FlashMap addTargetRequestParam(String name, String value) {
        if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
            this.targetRequestParams.add(name, value);
        }
        return this;
    }

    public MultiValueMap<String, String> getTargetRequestParams() {
        return this.targetRequestParams;
    }

    public void startExpirationPeriod(int timeToLive) {
        this.expirationTime = System.currentTimeMillis() + (timeToLive * 1000);
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public boolean isExpired() {
        return this.expirationTime != -1 && System.currentTimeMillis() > this.expirationTime;
    }

    @Override // java.lang.Comparable
    public int compareTo(FlashMap other) {
        int thisUrlPath = this.targetRequestPath != null ? 1 : 0;
        int otherUrlPath = other.targetRequestPath != null ? 1 : 0;
        if (thisUrlPath != otherUrlPath) {
            return otherUrlPath - thisUrlPath;
        }
        return other.targetRequestParams.size() - this.targetRequestParams.size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof FlashMap) {
                FlashMap that = (FlashMap) other;
                if (!super.equals(other) || !ObjectUtils.nullSafeEquals(this.targetRequestPath, that.targetRequestPath) || !this.targetRequestParams.equals(that.targetRequestParams)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.targetRequestPath))) + this.targetRequestParams.hashCode();
    }

    @Override // java.util.AbstractMap
    public String toString() {
        return "FlashMap [attributes=" + super.toString() + ", targetRequestPath=" + this.targetRequestPath + ", targetRequestParams=" + this.targetRequestParams + "]";
    }
}
