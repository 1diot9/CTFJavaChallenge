package org.h2.engine;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/GeneratedKeysMode.class */
public final class GeneratedKeysMode {
    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int COLUMN_NUMBERS = 2;
    public static final int COLUMN_NAMES = 3;

    public static int valueOf(Object obj) {
        if (obj == null || Boolean.FALSE.equals(obj)) {
            return 0;
        }
        if (Boolean.TRUE.equals(obj)) {
            return 1;
        }
        if (obj instanceof int[]) {
            return ((int[]) obj).length > 0 ? 2 : 0;
        }
        if (obj instanceof String[]) {
            return ((String[]) obj).length > 0 ? 3 : 0;
        }
        throw DbException.getInternalError();
    }

    private GeneratedKeysMode() {
    }
}
