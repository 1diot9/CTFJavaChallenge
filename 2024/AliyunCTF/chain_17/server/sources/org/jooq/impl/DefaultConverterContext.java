package org.jooq.impl;

import java.util.Map;
import org.jooq.Configuration;
import org.jooq.ConverterContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConverterContext.class */
public final class DefaultConverterContext extends AbstractScope implements ConverterContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConverterContext(Configuration configuration) {
        super(configuration, new DataMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConverterContext(Configuration configuration, Map<Object, Object> data) {
        super(configuration, data);
    }
}
