package org.jooq.impl;

import java.util.Arrays;
import java.util.function.BiFunction;
import org.jooq.CommonTableExpression;
import org.jooq.DerivedColumnList;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.WindowDefinition;
import org.jooq.WindowSpecification;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractName.class */
public abstract class AbstractName extends AbstractQueryPart implements Name, SimpleQueryPart, QOM.UEmpty {
    static final UnqualifiedName NO_NAME = new UnqualifiedName("");

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public abstract int hashCode();

    @Override // org.jooq.Name
    public /* bridge */ /* synthetic */ DerivedColumnList fields(BiFunction biFunction) {
        return fields((BiFunction<? super Field<?>, ? super Integer, ? extends String>) biFunction);
    }

    @Override // org.jooq.Name
    public /* bridge */ /* synthetic */ DerivedColumnList fields(java.util.function.Function function) {
        return fields((java.util.function.Function<? super Field<?>, ? extends String>) function);
    }

    @Override // org.jooq.Name
    public final Name append(String name) {
        return append(new UnqualifiedName(name));
    }

    @Override // org.jooq.Name
    public final Name append(Name name) {
        if (empty()) {
            return name;
        }
        if (name.empty()) {
            return this;
        }
        Name[] p1 = parts();
        Name[] p2 = name.parts();
        Name[] array = new Name[p1.length + p2.length];
        System.arraycopy(p1, 0, array, 0, p1.length);
        System.arraycopy(p2, 0, array, p1.length, p2.length);
        return new QualifiedName(array);
    }

    @Override // org.jooq.Name
    public final WindowDefinition as() {
        return new WindowDefinitionImpl(this, null);
    }

    @Override // org.jooq.Name
    public final WindowDefinition as(WindowSpecification window) {
        return new WindowDefinitionImpl(this, window);
    }

    @Override // org.jooq.Name
    public final <R extends Record> CommonTableExpression<R> as(ResultQuery<R> query) {
        return fields(new String[0]).as(query);
    }

    @Override // org.jooq.Name
    public final <R extends Record> CommonTableExpression<R> asMaterialized(ResultQuery<R> query) {
        return fields(new String[0]).asMaterialized(query);
    }

    @Override // org.jooq.Name
    public final <R extends Record> CommonTableExpression<R> asNotMaterialized(ResultQuery<R> query) {
        return fields(new String[0]).asNotMaterialized(query);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String... fieldNames) {
        return fields(Tools.names(fieldNames));
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name... fieldNames) {
        return new DerivedColumnListImpl(unqualifiedName(), fieldNames);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return fields((f, i) -> {
            return (String) fieldNameFunction.apply(f);
        });
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return new DerivedColumnListImpl(first(), fieldNameFunction);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1) {
        return fields(fieldName1);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2) {
        return fields(fieldName1, fieldName2);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3) {
        return fields(fieldName1, fieldName2, fieldName3);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17, String fieldName18) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17, String fieldName18, String fieldName19) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17, String fieldName18, String fieldName19, String fieldName20) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17, String fieldName18, String fieldName19, String fieldName20, String fieldName21) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20, fieldName21);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(String fieldName1, String fieldName2, String fieldName3, String fieldName4, String fieldName5, String fieldName6, String fieldName7, String fieldName8, String fieldName9, String fieldName10, String fieldName11, String fieldName12, String fieldName13, String fieldName14, String fieldName15, String fieldName16, String fieldName17, String fieldName18, String fieldName19, String fieldName20, String fieldName21, String fieldName22) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20, fieldName21, fieldName22);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1) {
        return fields(fieldName1);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2) {
        return fields(fieldName1, fieldName2);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3) {
        return fields(fieldName1, fieldName2, fieldName3);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17, Name fieldName18) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17, Name fieldName18, Name fieldName19) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17, Name fieldName18, Name fieldName19, Name fieldName20) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17, Name fieldName18, Name fieldName19, Name fieldName20, Name fieldName21) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20, fieldName21);
    }

    @Override // org.jooq.Name
    public final DerivedColumnListImpl fields(Name fieldName1, Name fieldName2, Name fieldName3, Name fieldName4, Name fieldName5, Name fieldName6, Name fieldName7, Name fieldName8, Name fieldName9, Name fieldName10, Name fieldName11, Name fieldName12, Name fieldName13, Name fieldName14, Name fieldName15, Name fieldName16, Name fieldName17, Name fieldName18, Name fieldName19, Name fieldName20, Name fieldName21, Name fieldName22) {
        return fields(fieldName1, fieldName2, fieldName3, fieldName4, fieldName5, fieldName6, fieldName7, fieldName8, fieldName9, fieldName10, fieldName11, fieldName12, fieldName13, fieldName14, fieldName15, fieldName16, fieldName17, fieldName18, fieldName19, fieldName20, fieldName21, fieldName22);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof AbstractName) {
            AbstractName n = (AbstractName) that;
            if (qualified() != n.qualified()) {
                return false;
            }
            return Arrays.equals(getName(), n.getName());
        }
        return super.equals(that);
    }

    @Override // org.jooq.Name
    public final boolean equalsIgnoreCase(Name that) {
        if (this == that) {
            return true;
        }
        String[] thisName = getName();
        String[] thatName = that.getName();
        if (thisName.length != thatName.length) {
            return false;
        }
        for (int i = 0; i < thisName.length; i++) {
            if ((thisName[i] != null || thatName[i] != null) && (thisName[i] == null || thatName[i] == null || !thisName[i].equalsIgnoreCase(thatName[i]))) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Name, java.lang.Comparable
    public int compareTo(Name o) {
        return unquotedName().toString().compareTo(o.unquotedName().toString());
    }
}
