package org.jooq;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WithStep.class */
public interface WithStep extends QueryPart {
    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str, String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(String str, Collection<String> collection);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name, Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep with(Name name, Collection<? extends Name> collection);

    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep with(String str, Function<? super Field<?>, ? extends String> function);

    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    WithAsStep with(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep1 with(String str, String str2);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep2 with(String str, String str2, String str3);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep3 with(String str, String str2, String str3, String str4);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep4 with(String str, String str2, String str3, String str4, String str5);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep5 with(String str, String str2, String str3, String str4, String str5, String str6);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep6 with(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep7 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep8 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep9 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep10 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep11 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep12 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep13 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep14 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep15 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep16 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep17 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep18 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep19 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep20 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep21 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep22 with(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep1 with(Name name, Name name2);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep2 with(Name name, Name name2, Name name3);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep3 with(Name name, Name name2, Name name3, Name name4);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep4 with(Name name, Name name2, Name name3, Name name4, Name name5);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep5 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep6 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep7 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep8 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep9 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep10 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep11 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep12 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep13 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep14 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep15 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep16 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep17 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep18 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep19 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep20 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep21 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22);

    @Support
    @CheckReturnValue
    @NotNull
    WithAsStep22 with(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22, Name name23);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep with(CommonTableExpression<?>... commonTableExpressionArr);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep with(Collection<? extends CommonTableExpression<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <R extends Record> SelectWhereStep<R> selectFrom(TableLike<R> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(Name name);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectWhereStep<Record> selectFrom(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    @CheckReturnValue
    @NotNull
    <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> selectField);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> selectField, SelectField<T2> selectField2);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    @CheckReturnValue
    @NotNull
    <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> selectField);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectZero();

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectOne();

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record1<Integer>> selectCount();

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertSetStep<R> insertInto(Table<R> table);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1> InsertValuesStep1<R, T1> insertInto(Table<R> table, Field<T1> field);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2> InsertValuesStep2<R, T1, T2> insertInto(Table<R> table, Field<T1> field, Field<T2> field2);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3> InsertValuesStep3<R, T1, T2, T3> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4> InsertValuesStep4<R, T1, T2, T3, T4> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5> InsertValuesStep5<R, T1, T2, T3, T4, T5> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6> InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> insertInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertValuesStepN<R> insertInto(Table<R> table, Field<?>... fieldArr);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> InsertValuesStepN<R> insertInto(Table<R> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> UpdateSetFirstStep<R> update(Table<R> table);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1> MergeKeyStep1<R, T1> mergeInto(Table<R> table, Field<T1> field);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2> MergeKeyStep2<R, T1, T2> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3> MergeKeyStep3<R, T1, T2, T3> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4> MergeKeyStep4<R, T1, T2, T3, T4> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5> MergeKeyStep5<R, T1, T2, T3, T4, T5> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6> MergeKeyStep6<R, T1, T2, T3, T4, T5, T6> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> mergeInto(Table<R> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Field<?>... fieldArr);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Collection<? extends Field<?>> collection);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <R extends Record> DeleteUsingStep<R> delete(Table<R> table);
}
