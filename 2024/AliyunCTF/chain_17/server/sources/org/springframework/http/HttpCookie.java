package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpCookie.class */
public class HttpCookie {
    private final String name;
    private final String value;

    public HttpCookie(String name, @Nullable String value) {
        Assert.hasLength(name, "'name' is required and must not be empty.");
        this.name = name;
        this.value = value != null ? value : "";
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof HttpCookie) {
                HttpCookie that = (HttpCookie) other;
                if (this.name.equalsIgnoreCase(that.getName())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name + "=" + this.value;
    }
}
