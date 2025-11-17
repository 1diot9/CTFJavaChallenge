package org.springframework.http;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ProblemDetail.class */
public class ProblemDetail {
    private static final URI BLANK_TYPE = URI.create("about:blank");
    private URI type;

    @Nullable
    private String title;
    private int status;

    @Nullable
    private String detail;

    @Nullable
    private URI instance;

    @Nullable
    private Map<String, Object> properties;

    protected ProblemDetail(int rawStatusCode) {
        this.type = BLANK_TYPE;
        this.status = rawStatusCode;
    }

    protected ProblemDetail(ProblemDetail other) {
        this.type = BLANK_TYPE;
        this.type = other.type;
        this.title = other.title;
        this.status = other.status;
        this.detail = other.detail;
        this.instance = other.instance;
        this.properties = other.properties != null ? new LinkedHashMap(other.properties) : null;
    }

    protected ProblemDetail() {
        this.type = BLANK_TYPE;
    }

    public void setType(URI type) {
        Assert.notNull(type, "'type' is required");
        this.type = type;
    }

    public URI getType() {
        return this.type;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getTitle() {
        HttpStatus httpStatus;
        if (this.title == null && (httpStatus = HttpStatus.resolve(this.status)) != null) {
            return httpStatus.getReasonPhrase();
        }
        return this.title;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus.value();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setDetail(@Nullable String detail) {
        this.detail = detail;
    }

    @Nullable
    public String getDetail() {
        return this.detail;
    }

    public void setInstance(@Nullable URI instance) {
        this.instance = instance;
    }

    @Nullable
    public URI getInstance() {
        return this.instance;
    }

    public void setProperty(String name, @Nullable Object value) {
        this.properties = this.properties != null ? this.properties : new LinkedHashMap<>();
        this.properties.put(name, value);
    }

    public void setProperties(@Nullable Map<String, Object> properties) {
        this.properties = properties;
    }

    @Nullable
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ProblemDetail) {
                ProblemDetail that = (ProblemDetail) other;
                if (!getType().equals(that.getType()) || !ObjectUtils.nullSafeEquals(getTitle(), that.getTitle()) || this.status != that.status || !ObjectUtils.nullSafeEquals(this.detail, that.detail) || !ObjectUtils.nullSafeEquals(this.instance, that.instance) || !ObjectUtils.nullSafeEquals(this.properties, that.properties)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.type, getTitle(), Integer.valueOf(this.status), this.detail, this.instance, this.properties);
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + initToStringContent() + "]";
    }

    protected String initToStringContent() {
        return "type='" + getType() + "', title='" + getTitle() + "', status=" + getStatus() + ", detail='" + getDetail() + "', instance='" + getInstance() + "', properties='" + getProperties() + "'";
    }

    public static ProblemDetail forStatus(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode is required");
        return forStatus(status.value());
    }

    public static ProblemDetail forStatus(int status) {
        return new ProblemDetail(status);
    }

    public static ProblemDetail forStatusAndDetail(HttpStatusCode status, String detail) {
        Assert.notNull(status, "HttpStatusCode is required");
        ProblemDetail problemDetail = forStatus(status.value());
        problemDetail.setDetail(detail);
        return problemDetail;
    }
}
