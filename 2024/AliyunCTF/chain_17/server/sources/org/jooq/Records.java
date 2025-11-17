package org.jooq;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jooq.exception.InvalidResultException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Records.class */
public final class Records {
    private static final Object NULL = new Object();

    public static final <E, R extends Record1<E>> Collector<R, ?, E[]> intoArray(E[] a) {
        return intoArray(a, (v0) -> {
            return v0.value1();
        });
    }

    public static final <E, R extends Record1<E>> Collector<R, ?, E[]> intoArray(Class<? extends E> componentType) {
        return intoArray(componentType, (v0) -> {
            return v0.value1();
        });
    }

    public static final <E, R extends Record> Collector<R, ?, E[]> intoArray(E[] a, Function<? super R, ? extends E> function) {
        return Collectors.collectingAndThen(Collectors.mapping(function, Collectors.toCollection(ArrayList::new)), l -> {
            return l.toArray(a);
        });
    }

    public static final <E, R extends Record> Collector<R, ?, E[]> intoArray(Class<? extends E> componentType, Function<? super R, ? extends E> function) {
        return Collectors.collectingAndThen(Collectors.mapping(function, Collectors.toCollection(ArrayList::new)), l -> {
            return l.toArray((Object[]) Array.newInstance((Class<?>) componentType, l.size()));
        });
    }

    public static final <E, R extends Record1<E>> Collector<R, ?, List<E>> intoList() {
        return Collectors.mapping((v0) -> {
            return v0.value1();
        }, Collectors.toCollection(ArrayList::new));
    }

    public static final <E, R extends Record> Collector<R, ?, List<E>> intoList(Function<? super R, ? extends E> function) {
        return Collectors.mapping(function, Collectors.toCollection(ArrayList::new));
    }

    public static final <E, R extends Record1<E>> Collector<R, ?, Set<E>> intoSet() {
        return intoSet((v0) -> {
            return v0.value1();
        });
    }

    public static final <E, R extends Record> Collector<R, ?, Set<E>> intoSet(Function<? super R, ? extends E> function) {
        return Collectors.mapping(function, Collectors.toCollection(LinkedHashSet::new));
    }

    public static final <K, V, R extends Record2<K, V>> Collector<R, ?, Map<K, V>> intoMap() {
        return intoMap((v0) -> {
            return v0.value1();
        }, (v0) -> {
            return v0.value2();
        });
    }

    public static final <K, R extends Record> Collector<R, ?, Map<K, R>> intoMap(Function<? super R, ? extends K> keyMapper) {
        return intoMap(keyMapper, r -> {
            return r;
        });
    }

    public static final <K, V, R extends Record> Collector<R, ?, Map<K, V>> intoMap(Function<? super R, ? extends K> keyMapper, Function<? super R, ? extends V> valueMapper) {
        return Collector.of(LinkedHashMap::new, (m, e) -> {
            Object apply = keyMapper.apply(e);
            if (m.containsKey(apply)) {
                throw new InvalidResultException("Key " + String.valueOf(apply) + " is not unique in Result");
            }
            m.put(apply, valueMapper.apply(e));
        }, (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        }, new Collector.Characteristics[0]);
    }

    public static final <K, V, R extends Record2<K, V>> Collector<R, ?, Map<K, List<V>>> intoGroups() {
        return intoGroups((v0) -> {
            return v0.value1();
        }, (v0) -> {
            return v0.value2();
        });
    }

    public static final <K, R extends Record> Collector<R, ?, Map<K, List<R>>> intoGroups(Function<? super R, ? extends K> keyMapper) {
        return intoGroups(keyMapper, r -> {
            return r;
        });
    }

    public static final <K, V, R extends Record> Collector<R, ?, Map<K, List<V>>> intoGroups(Function<? super R, ? extends K> keyMapper, Function<? super R, ? extends V> valueMapper) {
        return Collectors.collectingAndThen(Collectors.groupingBy(keyMapper.andThen(wrapNulls()), LinkedHashMap::new, Collectors.mapping(valueMapper, Collectors.toList())), unwrapNulls());
    }

    public static final <K, R extends Record> Collector<R, ?, Map<K, Result<R>>> intoResultGroups(Function<? super R, ? extends K> keyMapper) {
        return intoResultGroups(keyMapper, r -> {
            return r;
        });
    }

    public static final <K, V extends Record, R extends Record> Collector<R, ?, Map<K, Result<V>>> intoResultGroups(Function<? super R, ? extends K> keyMapper, Function<? super R, ? extends V> valueMapper) {
        return Collectors.groupingBy(keyMapper, LinkedHashMap::new, Collector.of(() -> {
            return new Result[1];
        }, (x, r) -> {
            Record record = (Record) valueMapper.apply(r);
            if (x[0] == null) {
                x[0] = org.jooq.impl.Internal.result(record);
            }
            x[0].add(record);
        }, (r1, r2) -> {
            r1[0].addAll(r2[0]);
            return r1;
        }, r3 -> {
            return r3[0];
        }, new Collector.Characteristics[0]));
    }

    public static final <K, E, R extends Record> Collector<R, ?, List<E>> intoHierarchy(Function<? super R, ? extends K> keyMapper, Function<? super R, ? extends K> parentKeyMapper, Function<? super R, ? extends E> nodeMapper, BiConsumer<? super E, ? super E> parentChildAppender) {
        return Collectors.collectingAndThen(intoMap(keyMapper, r -> {
            return new AbstractMap.SimpleImmutableEntry(r, nodeMapper.apply(r));
        }), m -> {
            ArrayList arrayList = new ArrayList();
            m.forEach((k, v) -> {
                Map.Entry entry = (Map.Entry) m.get(parentKeyMapper.apply(v.getKey()));
                if (entry != null) {
                    parentChildAppender.accept(entry.getValue(), v.getValue());
                } else {
                    arrayList.add(v.getValue());
                }
            });
            return arrayList;
        });
    }

    public static final <T1, R extends Record1<T1>, U> RecordMapper<R, U> mapping(Function1<? super T1, ? extends U> function) {
        return r -> {
            return function.apply(r.value1());
        };
    }

    public static final <T1, T2, R extends Record2<T1, T2>, U> RecordMapper<R, U> mapping(Function2<? super T1, ? super T2, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2());
        };
    }

    public static final <T1, T2, T3, R extends Record3<T1, T2, T3>, U> RecordMapper<R, U> mapping(Function3<? super T1, ? super T2, ? super T3, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3());
        };
    }

    public static final <T1, T2, T3, T4, R extends Record4<T1, T2, T3, T4>, U> RecordMapper<R, U> mapping(Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4());
        };
    }

    public static final <T1, T2, T3, T4, T5, R extends Record5<T1, T2, T3, T4, T5>, U> RecordMapper<R, U> mapping(Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, R extends Record6<T1, T2, T3, T4, T5, T6>, U> RecordMapper<R, U> mapping(Function6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, R extends Record7<T1, T2, T3, T4, T5, T6, T7>, U> RecordMapper<R, U> mapping(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, R extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>, U> RecordMapper<R, U> mapping(Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, R extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, U> RecordMapper<R, U> mapping(Function9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, U> RecordMapper<R, U> mapping(Function10<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, U> RecordMapper<R, U> mapping(Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, U> RecordMapper<R, U> mapping(Function12<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, U> RecordMapper<R, U> mapping(Function13<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R extends Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, U> RecordMapper<R, U> mapping(Function14<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R extends Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, U> RecordMapper<R, U> mapping(Function15<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R extends Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, U> RecordMapper<R, U> mapping(Function16<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R extends Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, U> RecordMapper<R, U> mapping(Function17<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R extends Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, U> RecordMapper<R, U> mapping(Function18<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? super T18, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17(), r.value18());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R extends Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, U> RecordMapper<R, U> mapping(Function19<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? super T18, ? super T19, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17(), r.value18(), r.value19());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R extends Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, U> RecordMapper<R, U> mapping(Function20<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? super T18, ? super T19, ? super T20, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17(), r.value18(), r.value19(), r.value20());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R extends Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, U> RecordMapper<R, U> mapping(Function21<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? super T18, ? super T19, ? super T20, ? super T21, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17(), r.value18(), r.value19(), r.value20(), r.value21());
        };
    }

    public static final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R extends Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, U> RecordMapper<R, U> mapping(Function22<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? super T13, ? super T14, ? super T15, ? super T16, ? super T17, ? super T18, ? super T19, ? super T20, ? super T21, ? super T22, ? extends U> function) {
        return r -> {
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11(), r.value12(), r.value13(), r.value14(), r.value15(), r.value16(), r.value17(), r.value18(), r.value19(), r.value20(), r.value21(), r.value22());
        };
    }

    private static final <T> Function<T, Object> wrapNulls() {
        return t -> {
            return t == null ? NULL : t;
        };
    }

    private static final <K, V> Function<Map<Object, V>, Map<K, V>> unwrapNulls() {
        return map -> {
            if (map.containsKey(NULL)) {
                map.put(null, map.remove(NULL));
            }
            return map;
        };
    }
}
