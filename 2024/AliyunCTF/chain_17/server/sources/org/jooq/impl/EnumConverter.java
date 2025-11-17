package org.jooq.impl;

import java.lang.Enum;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EnumConverter.class */
public class EnumConverter<T, U extends Enum<U>> extends AbstractConverter<T, U> {
    private final Map<T, U> lookup;
    private final java.util.function.Function<? super U, ? extends T> to;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Converter
    public /* bridge */ /* synthetic */ Object from(Object obj) {
        return from((EnumConverter<T, U>) obj);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public EnumConverter(java.lang.Class<T> r7, java.lang.Class<U> r8) {
        /*
            r6 = this;
            r0 = r6
            r1 = r7
            r2 = r8
            java.lang.Class<java.lang.Number> r3 = java.lang.Number.class
            r4 = r7
            java.lang.Class r4 = org.jooq.tools.reflect.Reflect.wrapper(r4)
            boolean r3 = r3.isAssignableFrom(r4)
            if (r3 == 0) goto L18
            r3 = r7
            void r3 = (v1) -> { // java.util.function.Function.apply(java.lang.Object):java.lang.Object
                return lambda$new$0(r3, v1);
            }
            goto L30
        L18:
            java.lang.Class<org.jooq.EnumType> r3 = org.jooq.EnumType.class
            r4 = r8
            boolean r3 = r3.isAssignableFrom(r4)
            if (r3 == 0) goto L2a
            r3 = r7
            void r3 = (v1) -> { // java.util.function.Function.apply(java.lang.Object):java.lang.Object
                return lambda$new$1(r3, v1);
            }
            goto L30
        L2a:
            r3 = r7
            void r3 = (v1) -> { // java.util.function.Function.apply(java.lang.Object):java.lang.Object
                return lambda$new$2(r3, v1);
            }
        L30:
            r0.<init>(r1, r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.EnumConverter.<init>(java.lang.Class, java.lang.Class):void");
    }

    public EnumConverter(Class<T> fromType, Class<U> toType, java.util.function.Function<? super U, ? extends T> to) {
        super(fromType, toType);
        this.to = to;
        this.lookup = new LinkedHashMap();
        for (U u : toType.getEnumConstants()) {
            T key = to((EnumConverter<T, U>) u);
            if (key != null) {
                this.lookup.put(key, u);
            }
        }
    }

    @Override // org.jooq.Converter
    public final U from(T t) {
        if (t == null) {
            return null;
        }
        return this.lookup.get(t);
    }

    @Override // org.jooq.Converter
    public T to(U u) {
        if (u == null) {
            return null;
        }
        return this.to.apply(u);
    }

    @Override // org.jooq.impl.AbstractConverter
    public String toString() {
        return "EnumConverter [ " + fromType().getName() + " -> " + toType().getName() + " ]";
    }
}
