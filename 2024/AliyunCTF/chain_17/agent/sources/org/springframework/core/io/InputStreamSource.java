package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/InputStreamSource.class */
public interface InputStreamSource {
    InputStream getInputStream() throws IOException;
}
