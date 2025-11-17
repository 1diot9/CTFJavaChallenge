package org.h2.expression;

import java.io.IOException;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ParameterRemote.class */
public class ParameterRemote implements ParameterInterface {
    private Value value;
    private final int index;
    private TypeInfo type = TypeInfo.TYPE_UNKNOWN;
    private int nullable = 2;

    public ParameterRemote(int i) {
        this.index = i;
    }

    @Override // org.h2.expression.ParameterInterface
    public void setValue(Value value, boolean z) {
        if (z && (this.value instanceof ValueLob)) {
            ((ValueLob) this.value).remove();
        }
        this.value = value;
    }

    @Override // org.h2.expression.ParameterInterface
    public Value getParamValue() {
        return this.value;
    }

    @Override // org.h2.expression.ParameterInterface
    public void checkSet() {
        if (this.value == null) {
            throw DbException.get(ErrorCode.PARAMETER_NOT_SET_1, "#" + (this.index + 1));
        }
    }

    @Override // org.h2.expression.ParameterInterface
    public boolean isValueSet() {
        return this.value != null;
    }

    @Override // org.h2.expression.ParameterInterface
    public TypeInfo getType() {
        return this.value == null ? this.type : this.value.getType();
    }

    @Override // org.h2.expression.ParameterInterface
    public int getNullable() {
        return this.nullable;
    }

    public void readMetaData(Transfer transfer) throws IOException {
        this.type = transfer.readTypeInfo();
        this.nullable = transfer.readInt();
    }

    public static void writeMetaData(Transfer transfer, ParameterInterface parameterInterface) throws IOException {
        transfer.writeTypeInfo(parameterInterface.getType()).writeInt(parameterInterface.getNullable());
    }
}
