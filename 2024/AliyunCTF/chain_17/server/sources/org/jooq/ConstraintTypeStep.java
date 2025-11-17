package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintTypeStep.class */
public interface ConstraintTypeStep extends ConstraintFinalStep {
    @Support
    @NotNull
    ConstraintEnforcementStep primaryKey(String... strArr);

    @Support
    @NotNull
    ConstraintEnforcementStep primaryKey(Name... nameArr);

    @Support
    @NotNull
    ConstraintEnforcementStep primaryKey(Field<?>... fieldArr);

    @Support
    @NotNull
    ConstraintEnforcementStep primaryKey(Collection<? extends Field<?>> collection);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStepN foreignKey(String... strArr);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStepN foreignKey(Name... nameArr);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStepN foreignKey(Field<?>... fieldArr);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStepN foreignKey(Collection<? extends Field<?>> collection);

    @Support
    @NotNull
    <T1> ConstraintForeignKeyReferencesStep1<T1> foreignKey(Field<T1> field);

    @Support
    @NotNull
    <T1, T2> ConstraintForeignKeyReferencesStep2<T1, T2> foreignKey(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    <T1, T2, T3> ConstraintForeignKeyReferencesStep3<T1, T2, T3> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    <T1, T2, T3, T4> ConstraintForeignKeyReferencesStep4<T1, T2, T3, T4> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5> ConstraintForeignKeyReferencesStep5<T1, T2, T3, T4, T5> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6> ConstraintForeignKeyReferencesStep6<T1, T2, T3, T4, T5, T6> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> ConstraintForeignKeyReferencesStep7<T1, T2, T3, T4, T5, T6, T7> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> ConstraintForeignKeyReferencesStep8<T1, T2, T3, T4, T5, T6, T7, T8> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> ConstraintForeignKeyReferencesStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ConstraintForeignKeyReferencesStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ConstraintForeignKeyReferencesStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ConstraintForeignKeyReferencesStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ConstraintForeignKeyReferencesStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ConstraintForeignKeyReferencesStep14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ConstraintForeignKeyReferencesStep15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ConstraintForeignKeyReferencesStep16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ConstraintForeignKeyReferencesStep17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ConstraintForeignKeyReferencesStep18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ConstraintForeignKeyReferencesStep19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ConstraintForeignKeyReferencesStep20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ConstraintForeignKeyReferencesStep21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Support
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ConstraintForeignKeyReferencesStep22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> foreignKey(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep1<?> foreignKey(Name name);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep2<?, ?> foreignKey(Name name, Name name2);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep3<?, ?, ?> foreignKey(Name name, Name name2, Name name3);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep4<?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep5<?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep6<?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep7<?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep8<?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep9<?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep11<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep12<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep14<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep15<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep17<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep19<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep20<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep21<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep22<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8, Name name9, Name name10, Name name11, Name name12, Name name13, Name name14, Name name15, Name name16, Name name17, Name name18, Name name19, Name name20, Name name21, Name name22);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep1<?> foreignKey(String str);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep2<?, ?> foreignKey(String str, String str2);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep3<?, ?, ?> foreignKey(String str, String str2, String str3);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep4<?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep5<?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep6<?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep7<?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep8<?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep9<?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep11<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep12<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep14<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep15<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep17<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep19<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep20<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep21<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21);

    @Support
    @NotNull
    ConstraintForeignKeyReferencesStep22<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22);

    @Support
    @NotNull
    ConstraintEnforcementStep unique(String... strArr);

    @Support
    @NotNull
    ConstraintEnforcementStep unique(Name... nameArr);

    @Support
    @NotNull
    ConstraintEnforcementStep unique(Field<?>... fieldArr);

    @Support
    @NotNull
    ConstraintEnforcementStep unique(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    ConstraintEnforcementStep check(Condition condition);
}
