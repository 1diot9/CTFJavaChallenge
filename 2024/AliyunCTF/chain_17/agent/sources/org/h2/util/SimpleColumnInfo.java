package org.h2.util;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SimpleColumnInfo.class */
public final class SimpleColumnInfo {
    public final String name;
    public final int type;
    public final String typeName;
    public final int precision;
    public final int scale;

    public SimpleColumnInfo(String str, int i, String str2, int i2, int i3) {
        this.name = str;
        this.type = i;
        this.typeName = str2;
        this.precision = i2;
        this.scale = i3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.name.equals(((SimpleColumnInfo) obj).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
