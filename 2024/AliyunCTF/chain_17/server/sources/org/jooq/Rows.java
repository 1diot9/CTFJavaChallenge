package org.jooq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Rows.class */
public final class Rows {
    @SafeVarargs
    public static final <T> Collector<T, ?, RowN[]> toRowArray(Function<? super T, ? extends Field<?>>... functions) {
        return Collectors.collectingAndThen(toRowList(functions), l -> {
            return (RowN[]) l.toArray(new RowN[0]);
        });
    }

    @SafeVarargs
    public static final <T, T1> Collector<T, ?, List<RowN>> toRowList(Function<? super T, ? extends Field<?>>... functions) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row(Stream.of((Object[]) functions).map(f -> {
                return (Field) f.apply(t);
            }).toArray()));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1> Collector<T, ?, Row1<T1>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1) {
        return Collectors.collectingAndThen(toRowList(f1), l -> {
            return (Row1[]) l.toArray(new Row1[0]);
        });
    }

    public static final <T, T1> Collector<T, ?, List<Row1<T1>>> toRowList(Function<? super T, ? extends Field<T1>> f1) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2> Collector<T, ?, Row2<T1, T2>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2) {
        return Collectors.collectingAndThen(toRowList(f1, f2), l -> {
            return (Row2[]) l.toArray(new Row2[0]);
        });
    }

    public static final <T, T1, T2> Collector<T, ?, List<Row2<T1, T2>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3> Collector<T, ?, Row3<T1, T2, T3>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3), l -> {
            return (Row3[]) l.toArray(new Row3[0]);
        });
    }

    public static final <T, T1, T2, T3> Collector<T, ?, List<Row3<T1, T2, T3>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4> Collector<T, ?, Row4<T1, T2, T3, T4>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4), l -> {
            return (Row4[]) l.toArray(new Row4[0]);
        });
    }

    public static final <T, T1, T2, T3, T4> Collector<T, ?, List<Row4<T1, T2, T3, T4>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5> Collector<T, ?, Row5<T1, T2, T3, T4, T5>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5), l -> {
            return (Row5[]) l.toArray(new Row5[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5> Collector<T, ?, List<Row5<T1, T2, T3, T4, T5>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6> Collector<T, ?, Row6<T1, T2, T3, T4, T5, T6>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6), l -> {
            return (Row6[]) l.toArray(new Row6[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6> Collector<T, ?, List<Row6<T1, T2, T3, T4, T5, T6>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7> Collector<T, ?, Row7<T1, T2, T3, T4, T5, T6, T7>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7), l -> {
            return (Row7[]) l.toArray(new Row7[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7> Collector<T, ?, List<Row7<T1, T2, T3, T4, T5, T6, T7>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8> Collector<T, ?, Row8<T1, T2, T3, T4, T5, T6, T7, T8>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8), l -> {
            return (Row8[]) l.toArray(new Row8[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8> Collector<T, ?, List<Row8<T1, T2, T3, T4, T5, T6, T7, T8>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9> Collector<T, ?, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9), l -> {
            return (Row9[]) l.toArray(new Row9[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9> Collector<T, ?, List<Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Collector<T, ?, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10), l -> {
            return (Row10[]) l.toArray(new Row10[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Collector<T, ?, List<Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Collector<T, ?, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11), l -> {
            return (Row11[]) l.toArray(new Row11[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Collector<T, ?, List<Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Collector<T, ?, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12), l -> {
            return (Row12[]) l.toArray(new Row12[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Collector<T, ?, List<Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Collector<T, ?, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13), l -> {
            return (Row13[]) l.toArray(new Row13[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Collector<T, ?, List<Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Collector<T, ?, Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14), l -> {
            return (Row14[]) l.toArray(new Row14[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Collector<T, ?, List<Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Collector<T, ?, Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15), l -> {
            return (Row15[]) l.toArray(new Row15[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Collector<T, ?, List<Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Collector<T, ?, Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16), l -> {
            return (Row16[]) l.toArray(new Row16[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Collector<T, ?, List<Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Collector<T, ?, Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17), l -> {
            return (Row17[]) l.toArray(new Row17[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Collector<T, ?, List<Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Collector<T, ?, Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18), l -> {
            return (Row18[]) l.toArray(new Row18[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Collector<T, ?, List<Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t), (SelectField) f18.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Collector<T, ?, Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19), l -> {
            return (Row19[]) l.toArray(new Row19[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Collector<T, ?, List<Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t), (SelectField) f18.apply(t), (SelectField) f19.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Collector<T, ?, Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20), l -> {
            return (Row20[]) l.toArray(new Row20[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Collector<T, ?, List<Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t), (SelectField) f18.apply(t), (SelectField) f19.apply(t), (SelectField) f20.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Collector<T, ?, Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20, Function<? super T, ? extends Field<T21>> f21) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21), l -> {
            return (Row21[]) l.toArray(new Row21[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Collector<T, ?, List<Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20, Function<? super T, ? extends Field<T21>> f21) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t), (SelectField) f18.apply(t), (SelectField) f19.apply(t), (SelectField) f20.apply(t), (SelectField) f21.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Collector<T, ?, Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>[]> toRowArray(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20, Function<? super T, ? extends Field<T21>> f21, Function<? super T, ? extends Field<T22>> f22) {
        return Collectors.collectingAndThen(toRowList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21, f22), l -> {
            return (Row22[]) l.toArray(new Row22[0]);
        });
    }

    public static final <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Collector<T, ?, List<Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>> toRowList(Function<? super T, ? extends Field<T1>> f1, Function<? super T, ? extends Field<T2>> f2, Function<? super T, ? extends Field<T3>> f3, Function<? super T, ? extends Field<T4>> f4, Function<? super T, ? extends Field<T5>> f5, Function<? super T, ? extends Field<T6>> f6, Function<? super T, ? extends Field<T7>> f7, Function<? super T, ? extends Field<T8>> f8, Function<? super T, ? extends Field<T9>> f9, Function<? super T, ? extends Field<T10>> f10, Function<? super T, ? extends Field<T11>> f11, Function<? super T, ? extends Field<T12>> f12, Function<? super T, ? extends Field<T13>> f13, Function<? super T, ? extends Field<T14>> f14, Function<? super T, ? extends Field<T15>> f15, Function<? super T, ? extends Field<T16>> f16, Function<? super T, ? extends Field<T17>> f17, Function<? super T, ? extends Field<T18>> f18, Function<? super T, ? extends Field<T19>> f19, Function<? super T, ? extends Field<T20>> f20, Function<? super T, ? extends Field<T21>> f21, Function<? super T, ? extends Field<T22>> f22) {
        return Collector.of(ArrayList::new, (l, t) -> {
            l.add(DSL.row((SelectField) f1.apply(t), (SelectField) f2.apply(t), (SelectField) f3.apply(t), (SelectField) f4.apply(t), (SelectField) f5.apply(t), (SelectField) f6.apply(t), (SelectField) f7.apply(t), (SelectField) f8.apply(t), (SelectField) f9.apply(t), (SelectField) f10.apply(t), (SelectField) f11.apply(t), (SelectField) f12.apply(t), (SelectField) f13.apply(t), (SelectField) f14.apply(t), (SelectField) f15.apply(t), (SelectField) f16.apply(t), (SelectField) f17.apply(t), (SelectField) f18.apply(t), (SelectField) f19.apply(t), (SelectField) f20.apply(t), (SelectField) f21.apply(t), (SelectField) f22.apply(t)));
        }, listCombiner(), new Collector.Characteristics[0]);
    }

    private static final <T> BinaryOperator<List<T>> listCombiner() {
        return (t1, t2) -> {
            t1.addAll(t2);
            return t1;
        };
    }
}
