package org.jooq.impl;

import java.util.EnumMap;
import org.jooq.SQLDialect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Identifiers.class */
final class Identifiers {
    static final EnumMap<SQLDialect, char[][][]> QUOTES = new EnumMap<>(SQLDialect.class);
    static final int QUOTE_START_DELIMITER = 0;
    static final int QUOTE_END_DELIMITER = 1;
    static final int QUOTE_END_DELIMITER_ESCAPED = 2;

    Identifiers() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    static {
        for (SQLDialect family : SQLDialect.values()) {
            switch (family) {
                case MARIADB:
                case MYSQL:
                    QUOTES.put((EnumMap<SQLDialect, char[][][]>) family, (SQLDialect) new char[][]{new char[]{new char[]{'`'}, new char[]{'\"'}}, new char[]{new char[]{'`'}, new char[]{'\"'}}, new char[]{new char[]{'`', '`'}, new char[]{'\"', '\"'}}});
                    break;
                default:
                    QUOTES.put((EnumMap<SQLDialect, char[][][]>) family, (SQLDialect) new char[][]{new char[]{new char[]{'\"'}}, new char[]{new char[]{'\"'}}, new char[]{new char[]{'\"', '\"'}}});
                    break;
            }
        }
    }
}
