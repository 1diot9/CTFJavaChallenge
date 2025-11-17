package org.springframework.http.codec.multipart;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/PartEvent.class */
public interface PartEvent {
    HttpHeaders headers();

    DataBuffer content();

    boolean isLast();

    default String name() {
        String name = headers().getContentDisposition().getName();
        Assert.state(name != null, "No name available");
        return name;
    }
}
