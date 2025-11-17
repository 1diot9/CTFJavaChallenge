package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Results.class */
public interface Results extends List<Result<Record>>, Attachable {
    @NotNull
    List<ResultOrRows> resultsOrRows();

    @Override // org.jooq.Attachable
    void attach(Configuration configuration);

    @Override // org.jooq.Attachable
    void detach();
}
