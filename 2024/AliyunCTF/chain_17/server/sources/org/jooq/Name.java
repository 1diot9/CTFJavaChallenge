package org.jooq;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Name.class */
public interface Name extends QueryPart, Comparable<Name> {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Name$Quoted.class */
    public enum Quoted {
        QUOTED,
        UNQUOTED,
        SYSTEM,
        DEFAULT,
        MIXED
    }

    @Nullable
    String first();

    @Nullable
    String last();

    boolean empty();

    boolean qualified();

    boolean qualifierQualified();

    @Nullable
    Name qualifier();

    @NotNull
    Name unqualifiedName();

    @NotNull
    Quoted quoted();

    @NotNull
    Name quotedName();

    @NotNull
    Name unquotedName();

    @NotNull
    Name[] parts();

    @NotNull
    Name append(String str);

    @NotNull
    Name append(Name name);

    @NotNull
    String[] getName();

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowDefinition as();

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowDefinition as(WindowSpecification windowSpecification);

    @Support
    @NotNull
    <R extends Record> CommonTableExpression<R> as(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record> CommonTableExpression<R> asMaterialized(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record> CommonTableExpression<R> asNotMaterialized(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    DerivedColumnList fields(String... strArr);

    @Support
    @NotNull
    DerivedColumnList fields(Name... nameArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    DerivedColumnList fields(Function<? super Field<?>, ? extends String> function);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    DerivedColumnList fields(BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support
    @NotNull
    DerivedColumnList1 fields(String str);

    @Support
    @NotNull
    DerivedColumnList2 fields(String str, String str2);

    @Support
    @NotNull
    DerivedColumnList3 fields(String str, String str2, String str3);

    @Support
    @NotNull
    DerivedColumnList4 fields(String str, String str2, String str3, String str4);

    @Support
    @NotNull
    DerivedColumnList5 fields(String str, String str2, String str3, String str4, String str5);

    @Support
    @NotNull
    DerivedColumnList6 fields(String str, String str2, String str3, String str4, String str5, String str6);

    @Support
    @NotNull
    DerivedColumnList7 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Support
    @NotNull
    DerivedColumnList8 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support
    @NotNull
    DerivedColumnList9 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Support
    @NotNull
    DerivedColumnList10 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Support
    @NotNull
    DerivedColumnList11 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Support
    @NotNull
    DerivedColumnList12 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Support
    @NotNull
    DerivedColumnList13 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Support
    @NotNull
    DerivedColumnList14 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Support
    @NotNull
    DerivedColumnList15 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Support
    @NotNull
    DerivedColumnList16 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    @Support
    @NotNull
    DerivedColumnList17 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    @Support
    @NotNull
    DerivedColumnList18 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    @Support
    @NotNull
    DerivedColumnList19 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19);

    @Support
    @NotNull
    DerivedColumnList20 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20);

    @Support
    @NotNull
    DerivedColumnList21 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21);

    @Support
    @NotNull
    DerivedColumnList22 fields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22);

    @Support
    @NotNull
    DerivedColumnList1 fields(Name name);

    @Support
    @NotNull
    DerivedColumnList2 fields(Name name, Name name2);

    @Support
    @NotNull
    DerivedColumnList3 fields(Name name, Name name2, Name name3);

    @Support
    @NotNull
    DerivedColumnList4 fields(Name name, Name name2, Name name3, Name name4);

    @Support
    @NotNull
    DerivedColumnList5 fields(Name name, Name name2, Name name3, Name name4, Name name5);

    @Support
    @NotNull
    DerivedColumnList6 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6);

    @Support
    @NotNull
    DerivedColumnList7 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7);

    @Support
    @NotNull
    DerivedColumnList8 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support
    @NotNull
    DerivedColumnList9 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9);

    @Support
    @NotNull
    DerivedColumnList10 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10);

    @Support
    @NotNull
    DerivedColumnList11 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11);

    @Support
    @NotNull
    DerivedColumnList12 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12);

    @Support
    @NotNull
    DerivedColumnList13 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13);

    @Support
    @NotNull
    DerivedColumnList14 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14);

    @Support
    @NotNull
    DerivedColumnList15 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15);

    @Support
    @NotNull
    DerivedColumnList16 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16);

    @Support
    @NotNull
    DerivedColumnList17 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17);

    @Support
    @NotNull
    DerivedColumnList18 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18);

    @Support
    @NotNull
    DerivedColumnList19 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19);

    @Support
    @NotNull
    DerivedColumnList20 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20);

    @Support
    @NotNull
    DerivedColumnList21 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21);

    @Support
    @NotNull
    DerivedColumnList22 fields(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22);

    @Override // org.jooq.QueryPart
    boolean equals(Object obj);

    boolean equalsIgnoreCase(Name name);

    @Override // java.lang.Comparable
    int compareTo(Name name);
}
