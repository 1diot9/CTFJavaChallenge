package org.springframework.web.bind.annotation;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/annotation/RequestMethod.class */
public enum RequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    @Nullable
    public static RequestMethod resolve(String method) {
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
                return null;
        }
    }

    @Nullable
    public static RequestMethod resolve(HttpMethod httpMethod) {
        Assert.notNull(httpMethod, "HttpMethod must not be null");
        return resolve(httpMethod.name());
    }

    public HttpMethod asHttpMethod() {
        switch (this) {
            case GET:
                return HttpMethod.GET;
            case HEAD:
                return HttpMethod.HEAD;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case PATCH:
                return HttpMethod.PATCH;
            case DELETE:
                return HttpMethod.DELETE;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            case TRACE:
                return HttpMethod.TRACE;
            default:
                throw new IncompatibleClassChangeError();
        }
    }
}
