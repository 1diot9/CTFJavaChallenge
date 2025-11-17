package org.h2.mode;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionInfo.class */
public final class FunctionInfo {
    public final String name;
    public final int type;
    final int parameterCount;
    public final int returnDataType;
    public final boolean nullIfParameterIsNull;
    public final boolean deterministic;

    public FunctionInfo(String str, int i, int i2, int i3, boolean z, boolean z2) {
        this.name = str;
        this.type = i;
        this.parameterCount = i2;
        this.returnDataType = i3;
        this.nullIfParameterIsNull = z;
        this.deterministic = z2;
    }

    public FunctionInfo(FunctionInfo functionInfo, String str) {
        this.name = str;
        this.type = functionInfo.type;
        this.returnDataType = functionInfo.returnDataType;
        this.parameterCount = functionInfo.parameterCount;
        this.nullIfParameterIsNull = functionInfo.nullIfParameterIsNull;
        this.deterministic = functionInfo.deterministic;
    }
}
