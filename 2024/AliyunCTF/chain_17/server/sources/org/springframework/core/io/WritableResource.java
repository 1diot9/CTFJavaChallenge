package org.springframework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/WritableResource.class */
public interface WritableResource extends Resource {
    OutputStream getOutputStream() throws IOException;

    default boolean isWritable() {
        return true;
    }

    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }
}
