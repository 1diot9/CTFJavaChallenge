package com.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/async/ByteBufferFeeder.class */
public interface ByteBufferFeeder extends NonBlockingInputFeeder {
    void feedInput(ByteBuffer byteBuffer) throws IOException;
}
