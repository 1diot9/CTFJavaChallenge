package org.jooq;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Field.class */
public interface Field<T> extends SelectField<T>, GroupField, OrderField<T>, FieldOrRow, FieldOrRowOrSelect, FieldOrConstraint, TableElement {
    @Override // org.jooq.Named
    @NotNull
    String getName();

    @Override // org.jooq.Named
    @NotNull
    String getComment();

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Field<T> as(Function<? super Field<T>, ? extends String> function);

    @Override // org.jooq.QueryPart
    boolean equals(Object obj);

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    Field<T> as(String str);

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    Field<T> as(Name name);

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    Field<T> as(Field<?> field);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convert(Binding<T, U> binding);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convert(Converter<T, U> converter);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convert(Class<U> cls, Function<? super T, ? extends U> function, Function<? super U, ? extends T> function2);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convertFrom(Class<U> cls, Function<? super T, ? extends U> function);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convertFrom(Function<? super T, ? extends U> function);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convertTo(Class<U> cls, Function<? super U, ? extends T> function);

    @Override // org.jooq.SelectField
    @NotNull
    <U> Field<U> convertTo(Function<? super U, ? extends T> function);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> comment(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> comment(Comment comment);

    @Support
    @NotNull
    <Z> Field<Z> cast(Field<Z> field);

    @Support
    @NotNull
    <Z> Field<Z> cast(DataType<Z> dataType);

    @Support
    @NotNull
    <Z> Field<Z> cast(Class<Z> cls);

    @Support
    @NotNull
    <Z> Field<Z> coerce(Field<Z> field);

    @Support
    @NotNull
    <Z> Field<Z> coerce(DataType<Z> dataType);

    @Support
    @NotNull
    <Z> Field<Z> coerce(Class<Z> cls);

    @Support
    @NotNull
    SortField<T> asc();

    @Support
    @NotNull
    SortField<T> desc();

    @Support
    @NotNull
    SortField<T> sortDefault();

    @Support
    @NotNull
    SortField<T> sort(SortOrder sortOrder);

    @Support
    @NotNull
    SortField<Integer> sortAsc(Collection<T> collection);

    @Support
    @NotNull
    SortField<Integer> sortAsc(T... tArr);

    @Support
    @NotNull
    SortField<Integer> sortDesc(Collection<T> collection);

    @Support
    @NotNull
    SortField<Integer> sortDesc(T... tArr);

    @Support
    @NotNull
    <Z> SortField<Z> sort(Map<T, Z> map);

    @Support
    @NotNull
    SortField<T> nullsFirst();

    @Support
    @NotNull
    SortField<T> nullsLast();

    @Support
    @NotNull
    Condition eq(T t);

    @Support
    @NotNull
    Condition eq(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition eq(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(T t);

    @Support
    @NotNull
    Condition equal(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition equal(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(T t);

    @Support
    @NotNull
    Condition ge(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition ge(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(T t);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(T t);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition greaterThan(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(T t);

    @Support
    @NotNull
    Condition gt(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition gt(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition in(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition isDistinctFrom(T t);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T> field);

    @Support
    @NotNull
    Condition isNull();

    @Support
    @NotNull
    Condition isNotDistinctFrom(T t);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T> field);

    @Support
    @NotNull
    Condition isNotNull();

    @Support
    @NotNull
    Condition le(T t);

    @Support
    @NotNull
    Condition le(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition le(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition le(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(T t);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessThan(T t);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition lessThan(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    LikeEscapeStep like(String str);

    @Support
    @NotNull
    LikeEscapeStep like(Field<String> field);

    @Support
    @NotNull
    LikeEscapeStep like(QuantifiedSelect<? extends Record1<String>> quantifiedSelect);

    @Support
    @NotNull
    LikeEscapeStep likeIgnoreCase(String str);

    @Support
    @NotNull
    LikeEscapeStep likeIgnoreCase(Field<String> field);

    @Support
    @NotNull
    Condition lt(T t);

    @Support
    @NotNull
    Condition lt(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition lt(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(T t);

    @Support
    @NotNull
    Condition ne(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition ne(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(T t);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition notEqual(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    LikeEscapeStep notLike(String str);

    @Support
    @NotNull
    LikeEscapeStep notLike(Field<String> field);

    @Support
    @NotNull
    LikeEscapeStep notLike(QuantifiedSelect<? extends Record1<String>> quantifiedSelect);

    @Support
    @NotNull
    LikeEscapeStep notLikeIgnoreCase(String str);

    @Support
    @NotNull
    LikeEscapeStep notLikeIgnoreCase(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep notSimilarTo(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep notSimilarTo(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep notSimilarTo(QuantifiedSelect<? extends Record1<String>> quantifiedSelect);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep similarTo(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep similarTo(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    LikeEscapeStep similarTo(QuantifiedSelect<? extends Record1<String>> quantifiedSelect);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition isDocument();

    @Support({SQLDialect.POSTGRES})
    @NotNull
    Condition isNotDocument();

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Condition isJson();

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Condition isNotJson();

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitAnd(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitAnd(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitNand(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitNand(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitNor(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitNor(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitNot();

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitOr(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitOr(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitXNor(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitXNor(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitXor(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> bitXor(Field<T> field);

    @Support
    @NotNull
    Field<T> mod(Number number);

    @Support
    @NotNull
    Field<T> mod(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> modulo(Number number);

    @Support
    @NotNull
    Field<T> modulo(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> rem(Number number);

    @Support
    @NotNull
    Field<T> rem(Field<? extends Number> field);

    @Support
    @NotNull
    Field<BigDecimal> power(Number number);

    @Support
    @NotNull
    Field<BigDecimal> power(Field<? extends Number> field);

    @Support
    @NotNull
    Field<BigDecimal> pow(Number number);

    @Support
    @NotNull
    Field<BigDecimal> pow(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> shl(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> shl(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> shr(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> shr(Field<? extends Number> field);

    @Support
    @NotNull
    Condition contains(T t);

    @Support
    @NotNull
    Condition contains(Field<T> field);

    @Support
    @NotNull
    Condition containsIgnoreCase(T t);

    @Support
    @NotNull
    Condition containsIgnoreCase(Field<T> field);

    @Support
    @NotNull
    Condition endsWith(T t);

    @Support
    @NotNull
    Condition endsWith(Field<T> field);

    @Support
    @NotNull
    Condition endsWithIgnoreCase(T t);

    @Support
    @NotNull
    Condition endsWithIgnoreCase(Field<T> field);

    @Support
    @NotNull
    Condition startsWith(T t);

    @Support
    @NotNull
    Condition startsWith(Field<T> field);

    @Support
    @NotNull
    Condition startsWithIgnoreCase(T t);

    @Support
    @NotNull
    Condition startsWithIgnoreCase(Field<T> field);

    @Support
    @NotNull
    Field<T> neg();

    @Support
    @NotNull
    Field<T> unaryMinus();

    @Support
    @NotNull
    Field<T> unaryPlus();

    @Support
    @NotNull
    Field<T> add(Number number);

    @Support
    @NotNull
    Field<T> add(Field<?> field);

    @Support
    @NotNull
    Field<T> plus(Number number);

    @Support
    @NotNull
    Field<T> plus(Field<?> field);

    @Support
    @NotNull
    Field<T> sub(Number number);

    @Support
    @NotNull
    Field<T> sub(Field<?> field);

    @Support
    @NotNull
    Field<T> subtract(Number number);

    @Support
    @NotNull
    Field<T> subtract(Field<?> field);

    @Support
    @NotNull
    Field<T> minus(Number number);

    @Support
    @NotNull
    Field<T> minus(Field<?> field);

    @Support
    @NotNull
    Field<T> mul(Number number);

    @Support
    @NotNull
    Field<T> mul(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> multiply(Number number);

    @Support
    @NotNull
    Field<T> multiply(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> times(Number number);

    @Support
    @NotNull
    Field<T> times(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> div(Number number);

    @Support
    @NotNull
    Field<T> div(Field<? extends Number> field);

    @Support
    @NotNull
    Field<T> divide(Number number);

    @Support
    @NotNull
    Field<T> divide(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition likeRegex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition likeRegex(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLikeRegex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLikeRegex(Field<String> field);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition similarTo(Field<String> field, char c);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition similarTo(String str, char c);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notSimilarTo(Field<String> field, char c);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notSimilarTo(String str, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition like(Field<String> field, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition like(String str, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition likeIgnoreCase(Field<String> field, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition likeIgnoreCase(String str, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLike(Field<String> field, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLike(String str, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLikeIgnoreCase(Field<String> field, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notLikeIgnoreCase(String str, char c);

    @Support
    @NotNull
    Condition notContains(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notContains(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notContainsIgnoreCase(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Condition notContainsIgnoreCase(Field<T> field);

    @Support
    @NotNull
    Condition in(Collection<?> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record1<T>> result);

    @Support
    @NotNull
    Condition in(T... tArr);

    @Support
    @NotNull
    Condition in(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition notIn(Collection<?> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record1<T>> result);

    @Support
    @NotNull
    Condition notIn(T... tArr);

    @Support
    @NotNull
    Condition notIn(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition between(T t, T t2);

    @Support
    @NotNull
    Condition between(Field<T> field, Field<T> field2);

    @Support
    @NotNull
    Condition betweenSymmetric(T t, T t2);

    @Support
    @NotNull
    Condition betweenSymmetric(Field<T> field, Field<T> field2);

    @Support
    @NotNull
    Condition notBetween(T t, T t2);

    @Support
    @NotNull
    Condition notBetween(Field<T> field, Field<T> field2);

    @Support
    @NotNull
    Condition notBetweenSymmetric(T t, T t2);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Field<T> field, Field<T> field2);

    @Support
    @NotNull
    BetweenAndStep<T> between(T t);

    @Support
    @NotNull
    BetweenAndStep<T> between(Field<T> field);

    @Support
    @NotNull
    BetweenAndStep<T> betweenSymmetric(T t);

    @Support
    @NotNull
    BetweenAndStep<T> betweenSymmetric(Field<T> field);

    @Support
    @NotNull
    BetweenAndStep<T> notBetween(T t);

    @Support
    @NotNull
    BetweenAndStep<T> notBetween(Field<T> field);

    @Support
    @NotNull
    BetweenAndStep<T> notBetweenSymmetric(T t);

    @Support
    @NotNull
    BetweenAndStep<T> notBetweenSymmetric(Field<T> field);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T t);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T> field);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T>> quantifiedSelect);

    @Support
    @NotNull
    Condition isTrue();

    @Support
    @NotNull
    Condition isFalse();

    @Support
    @NotNull
    Condition equalIgnoreCase(String str);

    @Support
    @NotNull
    Condition equalIgnoreCase(Field<String> field);

    @Support
    @NotNull
    Condition notEqualIgnoreCase(String str);

    @Support
    @NotNull
    Condition notEqualIgnoreCase(Field<String> field);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<Integer> sign();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> abs();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> round();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> round(int i);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> floor();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> ceil();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> sqrt();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> exp();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> ln();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> log(int i);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> acos();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> asin();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> atan();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> atan2(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> atan2(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> cos();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> sin();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> tan();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> cot();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> sinh();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> cosh();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> tanh();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> coth();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> deg();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> rad();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<Integer> count();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<Integer> countDistinct();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> max();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> min();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> sum();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> avg();

    @Support({SQLDialect.CUBRID, SQLDialect.HSQLDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> median();

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> stddevPop();

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> stddevSamp();

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> varPop();

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<BigDecimal> varSamp();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<Integer> countOver();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<T> maxOver();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<T> minOver();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> sumOver();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> avgOver();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> firstValue();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lastValue();

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lead();

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lead(int i);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lead(int i, T t);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lead(int i, Field<T> field);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lag();

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lag(int i);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lag(int i, T t);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowIgnoreNullsStep<T> lag(int i, Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> stddevPopOver();

    @Support({SQLDialect.CUBRID, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> stddevSampOver();

    @Support({SQLDialect.CUBRID, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> varPopOver();

    @Support({SQLDialect.CUBRID, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    WindowPartitionByStep<BigDecimal> varSampOver();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> upper();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> lower();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> trim();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> rtrim();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> ltrim();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> rpad(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> rpad(int i);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> rpad(Field<? extends Number> field, Field<String> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> rpad(int i, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> lpad(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> lpad(int i);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> lpad(Field<? extends Number> field, Field<String> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> lpad(int i, char c);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> repeat(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> repeat(Field<? extends Number> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> replace(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> replace(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> replace(Field<String> field, Field<String> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> replace(String str, String str2);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> position(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> position(Field<String> field);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> ascii();

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<String> collate(String str);

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<String> collate(Name name);

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<String> collate(Collation collation);

    @Support
    @NotNull
    Field<String> concat(Field<?>... fieldArr);

    @Support
    @NotNull
    Field<String> concat(String... strArr);

    @Support
    @NotNull
    Field<String> concat(char... cArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> substring(int i);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> substring(Field<? extends Number> field);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> substring(int i, int i2);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<String> substring(Field<? extends Number> field, Field<? extends Number> field2);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> length();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> charLength();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> bitLength();

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<Integer> octetLength();

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<Integer> extract(DatePart datePart);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> greatest(T... tArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> greatest(Field<?>... fieldArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> least(T... tArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    Field<T> least(Field<?>... fieldArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> nvl(T t);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> nvl(Field<T> field);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> nvl2(Z z, Z z2);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> nvl2(Field<Z> field, Field<Z> field2);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> nullif(T t);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> nullif(Field<T> field);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> decode(T t, Z z);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> decode(T t, Z z, Object... objArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> decode(Field<T> field, Field<Z> field2);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    <Z> Field<Z> decode(Field<T> field, Field<Z> field2, Field<?>... fieldArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> coalesce(T t, T... tArr);

    @Support
    @Deprecated(forRemoval = true, since = "3.13")
    @NotNull
    Field<T> coalesce(Field<T> field, Field<?>... fieldArr);

    @Nullable
    Field<T> field(Record record);

    @Nullable
    T get(Record record);

    @Nullable
    T getValue(Record record);

    @Nullable
    T original(Record record);

    boolean changed(Record record);

    void reset(Record record);

    @Nullable
    Record1<T> from(Record record);

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    /* bridge */ /* synthetic */ default SelectField as(Field field) {
        return as((Field<?>) field);
    }
}
