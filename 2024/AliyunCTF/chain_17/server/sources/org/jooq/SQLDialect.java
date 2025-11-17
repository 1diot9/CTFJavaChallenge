package org.jooq;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SQLDialect.class */
public enum SQLDialect {
    DEFAULT("", false, false),
    CUBRID("CUBRID", false, true, SQLDialectCategory.MYSQL),
    DERBY("Derby", false, true),
    DUCKDB("DuckDB", false, true),
    FIREBIRD("Firebird", false, true, new RequiredVersion(4, null, null)),
    H2("H2", false, true, new RequiredVersion(2, 2, 220)),
    HSQLDB("HSQLDB", false, true),
    IGNITE("Ignite", false, true),
    MARIADB("MariaDB", false, true, new RequiredVersion(10, 7, null), SQLDialectCategory.MYSQL),
    MYSQL("MySQL", false, true, new RequiredVersion(8, 0, 31), SQLDialectCategory.MYSQL),
    POSTGRES("Postgres", false, true, new RequiredVersion(15, null, null), SQLDialectCategory.POSTGRES),
    SQLITE("SQLite", false, true, new RequiredVersion(3, 40, null)),
    TRINO("Trino", false, true),
    YUGABYTEDB("YugabyteDB", false, true, new RequiredVersion(2, 9, null), SQLDialectCategory.POSTGRES);

    private static final SQLDialect[] FAMILIES;
    private final String name;
    private final boolean commercial;
    private final boolean supported;
    private final RequiredVersion requiredVersion;
    private final SQLDialect family;
    private final SQLDialectCategory category;
    private SQLDialect predecessor;
    private transient EnumSet<SQLDialect> predecessors;
    private final ThirdParty thirdParty;
    private static final Pattern P_PATCH_VERSION_MYSQL;

    static {
        Set<SQLDialect> set = EnumSet.noneOf(SQLDialect.class);
        for (SQLDialect dialect : values()) {
            set.add(dialect.family());
        }
        FAMILIES = (SQLDialect[]) set.toArray(new SQLDialect[0]);
        P_PATCH_VERSION_MYSQL = Pattern.compile("^\\d+\\.\\d+\\.(\\d+).*$");
    }

    @NotNull
    public static final Set<SQLDialect> predecessors(SQLDialect... dialects) {
        EnumSet<SQLDialect> result = EnumSet.noneOf(SQLDialect.class);
        for (SQLDialect dialect : dialects) {
            result.addAll(dialect.predecessors());
        }
        return result;
    }

    @NotNull
    public static final Set<SQLDialect> supportedUntil(SQLDialect dialect) {
        return predecessors(dialect);
    }

    @NotNull
    public static final Set<SQLDialect> supportedUntil(SQLDialect... dialects) {
        return predecessors(dialects);
    }

    @NotNull
    public static final Set<SQLDialect> supportedBy(SQLDialect dialect) {
        EnumSet<SQLDialect> result = EnumSet.noneOf(SQLDialect.class);
        addSupportedBy(dialect, result);
        return result;
    }

    @NotNull
    public static final Set<SQLDialect> supportedBy(SQLDialect... dialects) {
        EnumSet<SQLDialect> result = EnumSet.noneOf(SQLDialect.class);
        for (SQLDialect dialect : dialects) {
            addSupportedBy(dialect, result);
        }
        return result;
    }

    private static final void addSupportedBy(SQLDialect dialect, EnumSet<SQLDialect> supported) {
        supported.add(dialect);
        if (dialect.isFamily()) {
            supported.addAll(dialect.predecessors());
            return;
        }
        SQLDialect family = dialect.family();
        while (true) {
            SQLDialect candidate = family;
            if (candidate != dialect) {
                supported.add(candidate);
                family = candidate.predecessor();
            } else {
                return;
            }
        }
    }

    SQLDialect(String name, boolean commercial, boolean supported) {
        this(name, commercial, supported, (RequiredVersion) null);
    }

    SQLDialect(String name, boolean commercial, boolean supported, SQLDialectCategory category) {
        this(name, commercial, supported, (RequiredVersion) null, category);
    }

    SQLDialect(String name, boolean commercial, boolean supported, RequiredVersion requiredVersion) {
        this(name, commercial, supported, requiredVersion, SQLDialectCategory.OTHER, null, null);
    }

    SQLDialect(String name, boolean commercial, boolean supported, RequiredVersion requiredVersion, SQLDialectCategory category) {
        this(name, commercial, supported, requiredVersion, category, null, null);
    }

    SQLDialect(String name, boolean commercial, boolean supported, RequiredVersion requiredVersion, SQLDialect family) {
        this(name, commercial, supported, requiredVersion, family, null);
    }

    SQLDialect(String name, boolean commercial, boolean supported, RequiredVersion requiredVersion, SQLDialect family, SQLDialect predecessor) {
        this(name, commercial, supported, requiredVersion, family.category(), family, predecessor);
    }

    SQLDialect(String name, boolean commercial, boolean supported, RequiredVersion requiredVersion, SQLDialectCategory category, SQLDialect family, SQLDialect predecessor) {
        this.name = name;
        this.commercial = commercial;
        this.supported = supported;
        this.requiredVersion = requiredVersion;
        this.family = family == null ? this : family;
        this.category = category == null ? this.family.category() : category;
        this.predecessor = predecessor == null ? this : predecessor;
        if (family != null) {
            family.predecessor = this;
        }
        this.thirdParty = new ThirdParty();
    }

    public final boolean commercial() {
        return this.commercial;
    }

    public final boolean supported() {
        return this.supported;
    }

    @NotNull
    public final SQLDialect family() {
        return this.family;
    }

    @NotNull
    public final SQLDialectCategory category() {
        return this.category;
    }

    public final boolean isFamily() {
        return this == this.family;
    }

    public final boolean isVersioned() {
        return this.requiredVersion != null;
    }

    @NotNull
    public final SQLDialect predecessor() {
        return this.predecessor;
    }

    @NotNull
    public final Set<SQLDialect> predecessors() {
        if (this.predecessors == null) {
            SQLDialect curr = this;
            EnumSet<SQLDialect> result = EnumSet.of(curr);
            while (true) {
                SQLDialect pred = curr.predecessor();
                result.add(pred);
                if (curr == pred) {
                    break;
                }
                curr = pred;
            }
            this.predecessors = result;
        }
        return Collections.unmodifiableSet(this.predecessors);
    }

    public final boolean precedes(SQLDialect other) {
        if (this.family != other.family) {
            return false;
        }
        return other.predecessors().contains(this);
    }

    public final boolean precedesStrictly(SQLDialect other) {
        return precedes(other) && this != other;
    }

    public final boolean supports(SQLDialect other) {
        if (this.family != other.family) {
            return false;
        }
        if (isFamily() || other.isFamily()) {
            return true;
        }
        return other.precedes(this);
    }

    @Deprecated(forRemoval = true, since = "3.14")
    public final boolean supports(Collection<SQLDialect> other) {
        SQLDialect sQLDialect;
        SQLDialect predecessor;
        if (other.contains(this.family)) {
            return true;
        }
        SQLDialect candidate = this.family.predecessor();
        boolean successor = this == this.family;
        do {
            successor = successor || this == candidate;
            if (other.contains(candidate)) {
                return successor;
            }
            sQLDialect = candidate;
            predecessor = candidate.predecessor();
            candidate = predecessor;
        } while (sQLDialect != predecessor);
        return false;
    }

    public final boolean supportsDatabaseVersion(int majorVersion, int minorVersion, String productVersion) {
        return this.requiredVersion == null || this.requiredVersion.major == null || this.requiredVersion.major.intValue() < majorVersion || (this.requiredVersion.major.intValue() == majorVersion && (this.requiredVersion.minor == null || this.requiredVersion.minor.intValue() < minorVersion || (this.requiredVersion.minor.intValue() == minorVersion && (this.requiredVersion.patch == null || this.requiredVersion.patch.intValue() <= patchVersion(productVersion)))));
    }

    private final int patchVersion(String productVersion) {
        if (productVersion == null) {
            return Integer.MAX_VALUE;
        }
        switch (family().ordinal()) {
            case 5:
                return Integer.parseInt(productVersion.split(" ")[0].split("\\.")[2]);
            case 9:
                return Integer.parseInt(P_PATCH_VERSION_MYSQL.matcher(productVersion).replaceFirst("$1"));
            default:
                return Integer.MAX_VALUE;
        }
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getNameLC() {
        return this.name.toLowerCase();
    }

    @NotNull
    public final String getNameUC() {
        return this.name.toUpperCase();
    }

    @NotNull
    public final ThirdParty thirdParty() {
        return this.thirdParty;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SQLDialect$ThirdParty.class */
    public final class ThirdParty {
        public ThirdParty() {
        }

        @NotNull
        public final String driver() {
            try {
                Class<?> utils = Class.forName("org.jooq.tools.jdbc.JDBCUtils");
                return (String) utils.getMethod("driver", SQLDialect.class).invoke(utils, SQLDialect.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Nullable
        public final String springDbName() {
            switch (SQLDialect.this.family.ordinal()) {
                case 2:
                    return "Derby";
                case 3:
                case 4:
                case 7:
                case 11:
                case 12:
                default:
                    return null;
                case 5:
                    return "H2";
                case 6:
                    return "HSQL";
                case 8:
                case 9:
                    return "MySQL";
                case 10:
                case 13:
                    return "PostgreSQL";
            }
        }

        @Nullable
        public final String hibernateDialect() {
            switch (SQLDialect.this.family().ordinal()) {
                case 1:
                    return "org.hibernate.dialect.CUBRIDDialect";
                case 2:
                    return "org.hibernate.dialect.DerbyTenSevenDialect";
                case 3:
                case 7:
                case 12:
                default:
                    return null;
                case 4:
                    return "org.hibernate.dialect.FirebirdDialect";
                case 5:
                    return "org.hibernate.dialect.H2Dialect";
                case 6:
                    return "org.hibernate.dialect.HSQLDialect";
                case 8:
                case 9:
                    return "org.hibernate.dialect.MySQL5Dialect";
                case 10:
                    return "org.hibernate.dialect.PostgreSQL94Dialect";
                case 11:
                    return null;
                case 13:
                    return "org.hibernate.dialect.PostgreSQL94Dialect";
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SQLDialect$RequiredVersion.class */
    static final class RequiredVersion extends java.lang.Record {
        private final Integer major;
        private final Integer minor;
        private final Integer patch;

        RequiredVersion(Integer major, Integer minor, Integer patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, RequiredVersion.class), RequiredVersion.class, "major;minor;patch", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->major:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->minor:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->patch:Ljava/lang/Integer;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, RequiredVersion.class), RequiredVersion.class, "major;minor;patch", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->major:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->minor:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->patch:Ljava/lang/Integer;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, RequiredVersion.class, Object.class), RequiredVersion.class, "major;minor;patch", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->major:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->minor:Ljava/lang/Integer;", "FIELD:Lorg/jooq/SQLDialect$RequiredVersion;->patch:Ljava/lang/Integer;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Integer major() {
            return this.major;
        }

        public Integer minor() {
            return this.minor;
        }

        public Integer patch() {
            return this.patch;
        }
    }
}
