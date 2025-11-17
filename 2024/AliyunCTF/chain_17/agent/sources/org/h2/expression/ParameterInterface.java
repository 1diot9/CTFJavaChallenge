package org.h2.expression;

import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ParameterInterface.class */
public interface ParameterInterface {
    void setValue(Value value, boolean z);

    Value getParamValue();

    void checkSet() throws DbException;

    boolean isValueSet();

    TypeInfo getType();

    int getNullable();
}
