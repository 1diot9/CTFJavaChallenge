package org.h2.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ExtTypeInfoRow.class */
public final class ExtTypeInfoRow extends ExtTypeInfo {
    private final LinkedHashMap<String, TypeInfo> fields;
    private int hash;

    public ExtTypeInfoRow(Typed[] typedArr) {
        this(typedArr, typedArr.length);
    }

    public ExtTypeInfoRow(Typed[] typedArr, int i) {
        if (i > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        LinkedHashMap<String, TypeInfo> linkedHashMap = new LinkedHashMap<>((int) Math.ceil(i / 0.75d));
        int i2 = 0;
        while (i2 < i) {
            TypeInfo type = typedArr[i2].getType();
            i2++;
            linkedHashMap.put("C" + i2, type);
        }
        this.fields = linkedHashMap;
    }

    public ExtTypeInfoRow(LinkedHashMap<String, TypeInfo> linkedHashMap) {
        if (linkedHashMap.size() > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        this.fields = linkedHashMap;
    }

    public Set<Map.Entry<String, TypeInfo>> getFields() {
        return this.fields.entrySet();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append('(');
        boolean z = false;
        for (Map.Entry<String, TypeInfo> entry : this.fields.entrySet()) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            ParserUtil.quoteIdentifier(sb, entry.getKey(), i).append(' ');
            entry.getValue().getSQL(sb, i);
        }
        return sb.append(')');
    }

    public int hashCode() {
        int i = this.hash;
        if (i != 0) {
            return i;
        }
        int i2 = 67378403;
        for (Map.Entry<String, TypeInfo> entry : this.fields.entrySet()) {
            i2 = (((i2 * 31) + entry.getKey().hashCode()) * 37) + entry.getValue().hashCode();
        }
        int i3 = i2;
        this.hash = i3;
        return i3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != ExtTypeInfoRow.class) {
            return false;
        }
        LinkedHashMap<String, TypeInfo> linkedHashMap = ((ExtTypeInfoRow) obj).fields;
        if (this.fields.size() != linkedHashMap.size()) {
            return false;
        }
        Iterator<Map.Entry<String, TypeInfo>> it = linkedHashMap.entrySet().iterator();
        for (Map.Entry<String, TypeInfo> entry : this.fields.entrySet()) {
            Map.Entry<String, TypeInfo> next = it.next();
            if (!entry.getKey().equals(next.getKey()) || !entry.getValue().equals(next.getValue())) {
                return false;
            }
        }
        return true;
    }
}
