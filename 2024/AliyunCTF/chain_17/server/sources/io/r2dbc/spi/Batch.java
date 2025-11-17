package io.r2dbc.spi;

import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Batch.class */
public interface Batch {
    Batch add(String str);

    Publisher<? extends Result> execute();
}
