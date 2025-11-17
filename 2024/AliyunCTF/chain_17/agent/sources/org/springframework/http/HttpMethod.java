package org.springframework.http;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpMethod.class */
public final class HttpMethod implements Comparable<HttpMethod>, Serializable {
    private static final long serialVersionUID = -70133475680645360L;
    public static final HttpMethod GET = new HttpMethod("GET");
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    public static final HttpMethod POST = new HttpMethod("POST");
    public static final HttpMethod PUT = new HttpMethod("PUT");
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    public static final HttpMethod TRACE = new HttpMethod("TRACE");
    private static final HttpMethod[] values = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE};
    private final String name;

    private HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod[] values() {
        HttpMethod[] copy = new HttpMethod[values.length];
        System.arraycopy(values, 0, copy, 0, values.length);
        return copy;
    }

    public static HttpMethod valueOf(String method) {
        Assert.notNull(method, "Method must not be null");
        boolean z = -1;
        switch (method.hashCode()) {
            case -531492226:
                if (method.equals("OPTIONS")) {
                    z = 6;
                    break;
                }
                break;
            case 70454:
                if (method.equals("GET")) {
                    z = false;
                    break;
                }
                break;
            case 79599:
                if (method.equals("PUT")) {
                    z = 3;
                    break;
                }
                break;
            case 2213344:
                if (method.equals("HEAD")) {
                    z = true;
                    break;
                }
                break;
            case 2461856:
                if (method.equals("POST")) {
                    z = 2;
                    break;
                }
                break;
            case 75900968:
                if (method.equals("PATCH")) {
                    z = 4;
                    break;
                }
                break;
            case 80083237:
                if (method.equals("TRACE")) {
                    z = 7;
                    break;
                }
                break;
            case 2012838315:
                if (method.equals("DELETE")) {
                    z = 5;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return GET;
            case true:
                return HEAD;
            case true:
                return POST;
            case true:
                return PUT;
            case true:
                return PATCH;
            case true:
                return DELETE;
            case true:
                return OPTIONS;
            case true:
                return TRACE;
            default:
                return new HttpMethod(method);
        }
    }

    public String name() {
        return this.name;
    }

    public boolean matches(String method) {
        return name().equals(method);
    }

    @Override // java.lang.Comparable
    public int compareTo(HttpMethod other) {
        return this.name.compareTo(other.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof HttpMethod) {
                HttpMethod that = (HttpMethod) other;
                if (this.name.equals(that.name)) {
                }
            }
            return false;
        }
        return true;
    }

    public String toString() {
        return this.name;
    }
}
