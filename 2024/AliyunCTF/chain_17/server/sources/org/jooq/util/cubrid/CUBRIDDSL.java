package org.jooq.util.cubrid;

import org.jooq.Field;
import org.jooq.impl.DSL;

@Deprecated(forRemoval = true, since = "3.13")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/cubrid/CUBRIDDSL.class */
public class CUBRIDDSL extends DSL {
    protected CUBRIDDSL() {
    }

    public static <T> Field<T> incr(Field<T> field) {
        return function("incr", field.getDataType(), (Field<?>[]) new Field[]{field});
    }

    public static <T> Field<T> decr(Field<T> field) {
        return function("decr", field.getDataType(), (Field<?>[]) new Field[]{field});
    }
}
