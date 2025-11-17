package org.jooq;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BatchBindStep.class */
public interface BatchBindStep extends Batch {
    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep bind(Object... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep bind(Object[]... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep bind(Map<String, Object> map);

    @Support
    @CheckReturnValue
    @NotNull
    BatchBindStep bind(Map<String, Object>... mapArr);
}
