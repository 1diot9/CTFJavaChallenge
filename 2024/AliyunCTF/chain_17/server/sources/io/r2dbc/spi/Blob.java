package io.r2dbc.spi;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Blob.class */
public interface Blob {
    Publisher<ByteBuffer> stream();

    Publisher<Void> discard();

    static Blob from(Publisher<ByteBuffer> p) {
        Assert.requireNonNull(p, "Publisher must not be null");
        final DefaultLob<ByteBuffer> lob = new DefaultLob<>(p);
        return new Blob() { // from class: io.r2dbc.spi.Blob.1
            @Override // io.r2dbc.spi.Blob
            public Publisher<ByteBuffer> stream() {
                return DefaultLob.this.stream();
            }

            @Override // io.r2dbc.spi.Blob
            public Publisher<Void> discard() {
                return DefaultLob.this.discard();
            }
        };
    }
}
