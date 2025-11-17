package org.h2.value;

import java.util.Objects;
import org.h2.util.geometry.EWKTUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ExtTypeInfoGeometry.class */
public final class ExtTypeInfoGeometry extends ExtTypeInfo {
    private final int type;
    private final Integer srid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder toSQL(StringBuilder sb, int i, Integer num) {
        if (i == 0 && num == null) {
            return sb;
        }
        sb.append('(');
        if (i == 0) {
            sb.append("GEOMETRY");
        } else {
            EWKTUtils.formatGeometryTypeAndDimensionSystem(sb, i);
        }
        if (num != null) {
            sb.append(", ").append(num.intValue());
        }
        return sb.append(')');
    }

    public ExtTypeInfoGeometry(int i, Integer num) {
        this.type = i;
        this.srid = num;
    }

    public int hashCode() {
        return (31 * (this.srid == null ? 0 : this.srid.hashCode())) + this.type;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != ExtTypeInfoGeometry.class) {
            return false;
        }
        ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry) obj;
        return this.type == extTypeInfoGeometry.type && Objects.equals(this.srid, extTypeInfoGeometry.srid);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return toSQL(sb, this.type, this.srid);
    }

    public int getType() {
        return this.type;
    }

    public Integer getSrid() {
        return this.srid;
    }
}
