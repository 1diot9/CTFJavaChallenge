package org.springframework.http;

import java.io.Serializable;
import java.util.function.Supplier;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpStatusCode.class */
public interface HttpStatusCode extends Serializable {
    int value();

    boolean is1xxInformational();

    boolean is2xxSuccessful();

    boolean is3xxRedirection();

    boolean is4xxClientError();

    boolean is5xxServerError();

    boolean isError();

    default boolean isSameCodeAs(HttpStatusCode other) {
        return value() == other.value();
    }

    static HttpStatusCode valueOf(int code) {
        Assert.isTrue(code >= 100 && code <= 999, (Supplier<String>) () -> {
            return "Status code '" + code + "' should be a three-digit positive integer";
        });
        HttpStatus status = HttpStatus.resolve(code);
        if (status != null) {
            return status;
        }
        return new DefaultHttpStatusCode(code);
    }
}
