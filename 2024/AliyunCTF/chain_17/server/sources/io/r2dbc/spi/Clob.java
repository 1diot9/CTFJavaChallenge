package io.r2dbc.spi;

import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Clob.class */
public interface Clob {
    Publisher<CharSequence> stream();

    Publisher<Void> discard();

    static Clob from(Publisher<? extends CharSequence> p) {
        Assert.requireNonNull(p, "Publisher must not be null");
        final DefaultLob<CharSequence> lob = new DefaultLob<>(p);
        return new Clob() { // from class: io.r2dbc.spi.Clob.1
            @Override // io.r2dbc.spi.Clob
            public Publisher<CharSequence> stream() {
                return DefaultLob.this.stream();
            }

            @Override // io.r2dbc.spi.Clob
            public Publisher<Void> discard() {
                return DefaultLob.this.discard();
            }
        };
    }
}
