package org.jooq;

import java.nio.charset.Charset;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CharsetProvider.class */
public interface CharsetProvider {
    Charset provide();
}
