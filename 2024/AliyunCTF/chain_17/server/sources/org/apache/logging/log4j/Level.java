package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.util.Strings;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/Level.class */
public final class Level implements Comparable<Level>, Serializable {
    private static final Level[] EMPTY_ARRAY = new Level[0];
    private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap();
    public static final Level OFF = new Level("OFF", StandardLevel.OFF.intLevel());
    public static final Level FATAL = new Level("FATAL", StandardLevel.FATAL.intLevel());
    public static final Level ERROR = new Level("ERROR", StandardLevel.ERROR.intLevel());
    public static final Level WARN = new Level("WARN", StandardLevel.WARN.intLevel());
    public static final Level INFO = new Level("INFO", StandardLevel.INFO.intLevel());
    public static final Level DEBUG = new Level("DEBUG", StandardLevel.DEBUG.intLevel());
    public static final Level TRACE = new Level("TRACE", StandardLevel.TRACE.intLevel());
    public static final Level ALL = new Level("ALL", StandardLevel.ALL.intLevel());
    public static final String CATEGORY = "Level";
    private static final long serialVersionUID = 1581082;
    private final String name;
    private final int intLevel;
    private final StandardLevel standardLevel;

    private Level(final String name, final int intLevel) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Illegal null or empty Level name.");
        }
        if (intLevel < 0) {
            throw new IllegalArgumentException("Illegal Level int less than zero.");
        }
        this.name = name;
        this.intLevel = intLevel;
        this.standardLevel = StandardLevel.getStandardLevel(intLevel);
        if (LEVELS.putIfAbsent(Strings.toRootUpperCase(name.trim()), this) != null) {
            throw new IllegalStateException("Level " + name + " has already been defined.");
        }
    }

    public int intLevel() {
        return this.intLevel;
    }

    public StandardLevel getStandardLevel() {
        return this.standardLevel;
    }

    public boolean isInRange(final Level minLevel, final Level maxLevel) {
        return this.intLevel >= minLevel.intLevel && this.intLevel <= maxLevel.intLevel;
    }

    public boolean isLessSpecificThan(final Level level) {
        return this.intLevel >= level.intLevel;
    }

    public boolean isMoreSpecificThan(final Level level) {
        return this.intLevel <= level.intLevel;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Level m807clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override // java.lang.Comparable
    public int compareTo(final Level other) {
        if (this.intLevel < other.intLevel) {
            return -1;
        }
        return this.intLevel > other.intLevel ? 1 : 0;
    }

    public boolean equals(final Object other) {
        return (other instanceof Level) && other == this;
    }

    public Class<Level> getDeclaringClass() {
        return Level.class;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static Level forName(final String name, final int intValue) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Illegal null or empty Level name.");
        }
        String normalizedName = Strings.toRootUpperCase(name.trim());
        Level level = LEVELS.get(normalizedName);
        if (level != null) {
            return level;
        }
        try {
            return new Level(name, intValue);
        } catch (IllegalStateException e) {
            return LEVELS.get(normalizedName);
        }
    }

    public static Level getLevel(final String name) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Illegal null or empty Level name.");
        }
        return LEVELS.get(Strings.toRootUpperCase(name.trim()));
    }

    public static Level toLevel(final String level) {
        return toLevel(level, DEBUG);
    }

    public static Level toLevel(final String name, final Level defaultLevel) {
        if (name == null) {
            return defaultLevel;
        }
        Level level = LEVELS.get(Strings.toRootUpperCase(name.trim()));
        return level == null ? defaultLevel : level;
    }

    public static Level[] values() {
        return (Level[]) LEVELS.values().toArray(EMPTY_ARRAY);
    }

    public static Level valueOf(final String name) {
        Objects.requireNonNull(name, "No level name given.");
        String levelName = Strings.toRootUpperCase(name.trim());
        Level level = LEVELS.get(levelName);
        if (level != null) {
            return level;
        }
        throw new IllegalArgumentException("Unknown level constant [" + levelName + "].");
    }

    public static <T extends Enum<T>> T valueOf(Class<T> cls, String str) {
        return (T) Enum.valueOf(cls, str);
    }

    protected Object readResolve() {
        return valueOf(this.name);
    }
}
