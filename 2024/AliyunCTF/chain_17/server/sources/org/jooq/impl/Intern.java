package org.jooq.impl;

import java.io.Serializable;
import org.jooq.Field;
import org.jooq.Name;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Intern.class */
public final class Intern implements Serializable {
    int[] internIndexes;
    Field<?>[] internFields;
    String[] internNameStrings;
    Name[] internNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int[] internIndexes(Field<?>[] fields) {
        if (this.internIndexes != null) {
            return this.internIndexes;
        }
        if (this.internFields != null) {
            return new FieldsImpl(fields).indexesOf(this.internFields);
        }
        if (this.internNameStrings != null) {
            return new FieldsImpl(fields).indexesOf(this.internNameStrings);
        }
        if (this.internNames != null) {
            return new FieldsImpl(fields).indexesOf(this.internNames);
        }
        return null;
    }
}
