package org.jooq;

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Attachable.class */
public interface Attachable extends Serializable {
    void attach(Configuration configuration);

    void detach();

    @Nullable
    Configuration configuration();
}
