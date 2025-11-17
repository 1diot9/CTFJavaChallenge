package org.jooq;

import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertOnDuplicateSetMoreStep.class */
public interface InsertOnDuplicateSetMoreStep<R extends Record> extends InsertOnDuplicateSetStep<R>, InsertOnConflictWhereStep<R> {
}
