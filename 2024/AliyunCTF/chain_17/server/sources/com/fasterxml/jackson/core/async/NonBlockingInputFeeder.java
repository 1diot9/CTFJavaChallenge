package com.fasterxml.jackson.core.async;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/async/NonBlockingInputFeeder.class */
public interface NonBlockingInputFeeder {
    boolean needMoreInput();

    void endOfInput();
}
