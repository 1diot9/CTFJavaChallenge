package org.jooq.impl;

import java.util.Arrays;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.jooq.XMLFormat;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractStore.class */
public abstract class AbstractStore extends AbstractFormattable {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int size();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Object get(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractStore() {
        this(null);
    }

    AbstractStore(Configuration configuration) {
        super(configuration);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public final DSLContext create() {
        return DSL.using(configuration());
    }

    @Override // org.jooq.impl.AbstractFormattable
    final JSONFormat defaultJSONFormat() {
        return Tools.configuration(this).formattingProvider().jsonFormatForRecords();
    }

    @Override // org.jooq.impl.AbstractFormattable
    final XMLFormat defaultXMLFormat() {
        return Tools.configuration(this).formattingProvider().xmlFormatForRecords();
    }

    public int hashCode() {
        int hashCode;
        int hashCode2 = 1;
        for (int i = 0; i < size(); i++) {
            Object obj = get(i);
            if (obj == null) {
                hashCode = 31 * hashCode2;
            } else if (obj.getClass().isArray()) {
                hashCode = 31 * hashCode2;
            } else {
                hashCode = (31 * hashCode2) + obj.hashCode();
            }
            hashCode2 = hashCode;
        }
        return hashCode2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractStore) {
            AbstractStore that = (AbstractStore) obj;
            if (size() == that.size()) {
                for (int i = 0; i < size(); i++) {
                    Object thisValue = get(i);
                    Object thatValue = that.get(i);
                    if (thisValue != null || thatValue != null) {
                        if (thisValue == null || thatValue == null) {
                            return false;
                        }
                        if (thisValue.getClass().isArray() && thatValue.getClass().isArray()) {
                            if (thisValue.getClass() == byte[].class && thatValue.getClass() == byte[].class) {
                                if (!Arrays.equals((byte[]) thisValue, (byte[]) thatValue)) {
                                    return false;
                                }
                            } else if (thisValue.getClass().getComponentType().isPrimitive() || thatValue.getClass().getComponentType().isPrimitive() || !Arrays.deepEquals((Object[]) thisValue, (Object[]) thatValue)) {
                                return false;
                            }
                        } else if (!thisValue.equals(thatValue)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
