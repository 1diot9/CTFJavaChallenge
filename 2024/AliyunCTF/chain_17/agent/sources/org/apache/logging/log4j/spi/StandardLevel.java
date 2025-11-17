package org.apache.logging.log4j.spi;

import java.util.EnumSet;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/StandardLevel.class */
public enum StandardLevel {
    OFF(0),
    FATAL(100),
    ERROR(200),
    WARN(300),
    INFO(400),
    DEBUG(500),
    TRACE(600),
    ALL(Integer.MAX_VALUE);

    private static final EnumSet<StandardLevel> LEVELSET = EnumSet.allOf(StandardLevel.class);
    private final int intLevel;

    StandardLevel(final int val) {
        this.intLevel = val;
    }

    public int intLevel() {
        return this.intLevel;
    }

    public static StandardLevel getStandardLevel(final int intLevel) {
        StandardLevel level = OFF;
        Iterator it = LEVELSET.iterator();
        while (it.hasNext()) {
            StandardLevel lvl = (StandardLevel) it.next();
            if (lvl.intLevel() > intLevel) {
                break;
            }
            level = lvl;
        }
        return level;
    }
}
