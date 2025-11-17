package org.jooq.impl;

import java.util.Comparator;
import java.util.List;
import org.jooq.Check;
import org.jooq.Condition;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Key;
import org.jooq.Named;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Comparators.class */
public final class Comparators {
    static final Comparator<Named> NAMED_COMP = Comparator.comparing((v0) -> {
        return v0.getQualifiedName();
    });
    static final Comparator<Table<?>> TABLE_VIEW_COMP = Comparator.comparing(t -> {
        return Integer.valueOf(t.getTableType() == TableOptions.TableType.TABLE ? 0 : 1);
    });
    static final Comparator<Key<?>> KEY_COMP = new KeyComparator();
    static final Comparator<ForeignKey<?, ?>> FOREIGN_KEY_COMP = new ForeignKeyComparator();
    static final Comparator<Check<?>> CHECK_COMP = Comparator.comparing(c -> {
        return c.condition().toString();
    });
    static final Comparator<Index> INDEX_COMP = new IndexComparator();

    Comparators() {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Comparators$KeyComparator.class */
    private static final class KeyComparator implements Comparator<Key<?>> {
        private KeyComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Key<?> o1, Key<?> o2) {
            List<? extends Named> f1 = o1.getFields();
            List<? extends Named> f2 = o2.getFields();
            int c = f1.size() - f2.size();
            if (c != 0) {
                return c;
            }
            for (int i = 0; i < f1.size(); i++) {
                int c2 = Comparators.NAMED_COMP.compare(f1.get(i), f2.get(i));
                if (c2 != 0) {
                    return c2;
                }
            }
            return 0;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Comparators$ForeignKeyComparator.class */
    private static final class ForeignKeyComparator implements Comparator<ForeignKey<?, ?>> {
        private ForeignKeyComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ForeignKey<?, ?> o1, ForeignKey<?, ?> o2) {
            int c = Comparators.KEY_COMP.compare(o1, o2);
            if (c != 0) {
                return c;
            }
            return Comparators.KEY_COMP.compare(o1.getKey(), o2.getKey());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Comparators$IndexComparator.class */
    private static final class IndexComparator implements Comparator<Index> {
        private IndexComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Index o1, Index o2) {
            int c = Boolean.valueOf(o1.getUnique()).compareTo(Boolean.valueOf(o2.getUnique()));
            if (c != 0) {
                return c;
            }
            int c2 = ((Condition) StringUtils.defaultIfNull(o1.getWhere(), DSL.noCondition())).toString().compareTo(((Condition) StringUtils.defaultIfNull(o2.getWhere(), DSL.noCondition())).toString());
            if (c2 != 0) {
                return c2;
            }
            List<SortField<?>> f1 = o1.getFields();
            List<SortField<?>> f2 = o2.getFields();
            int c3 = f1.size() - f2.size();
            if (c3 != 0) {
                return c3;
            }
            for (int i = 0; i < f1.size(); i++) {
                SortField<?> s1 = f1.get(i);
                SortField<?> s2 = f2.get(i);
                int c4 = s1.getName().compareTo(s2.getName());
                if (c4 != 0) {
                    return c4;
                }
                SortOrder d1 = s1.getOrder();
                if (d1 == SortOrder.DEFAULT) {
                    d1 = SortOrder.ASC;
                }
                SortOrder d2 = s2.getOrder();
                if (d2 == SortOrder.DEFAULT) {
                    d2 = SortOrder.ASC;
                }
                int c5 = d1.compareTo(d2);
                if (c5 != 0) {
                    return c5;
                }
            }
            return 0;
        }
    }
}
