package org.jooq.impl;

import java.nio.charset.Charset;
import org.jooq.CharsetProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCharsetProvider.class */
final class DefaultCharsetProvider implements CharsetProvider {
    @Override // org.jooq.CharsetProvider
    public final Charset provide() {
        return Charset.defaultCharset();
    }
}
