package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectOrderByStep.class */
public interface SelectOrderByStep<R extends Record> extends SelectLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    <T1> SelectSeekStep1<R, T1> orderBy(OrderField<T1> orderField);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2> SelectSeekStep2<R, T1, T2> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3> SelectSeekStep3<R, T1, T2, T3> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4> SelectSeekStep4<R, T1, T2, T3, T4> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5> SelectSeekStep5<R, T1, T2, T3, T4, T5> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6> SelectSeekStep6<R, T1, T2, T3, T4, T5, T6> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> SelectSeekStep7<R, T1, T2, T3, T4, T5, T6, T7> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> SelectSeekStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSeekStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSeekStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSeekStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSeekStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSeekStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSeekStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSeekStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSeekStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSeekStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSeekStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17, OrderField<T18> orderField18);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSeekStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17, OrderField<T18> orderField18, OrderField<T19> orderField19);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSeekStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17, OrderField<T18> orderField18, OrderField<T19> orderField19, OrderField<T20> orderField20);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSeekStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17, OrderField<T18> orderField18, OrderField<T19> orderField19, OrderField<T20> orderField20, OrderField<T21> orderField21);

    @Support
    @CheckReturnValue
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSeekStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> orderBy(OrderField<T1> orderField, OrderField<T2> orderField2, OrderField<T3> orderField3, OrderField<T4> orderField4, OrderField<T5> orderField5, OrderField<T6> orderField6, OrderField<T7> orderField7, OrderField<T8> orderField8, OrderField<T9> orderField9, OrderField<T10> orderField10, OrderField<T11> orderField11, OrderField<T12> orderField12, OrderField<T13> orderField13, OrderField<T14> orderField14, OrderField<T15> orderField15, OrderField<T16> orderField16, OrderField<T17> orderField17, OrderField<T18> orderField18, OrderField<T19> orderField19, OrderField<T20> orderField20, OrderField<T21> orderField21, OrderField<T22> orderField22);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekStepN<R> orderBy(OrderField<?>... orderFieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekStepN<R> orderBy(Collection<? extends OrderField<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitStep<R> orderBy(int... iArr);
}
