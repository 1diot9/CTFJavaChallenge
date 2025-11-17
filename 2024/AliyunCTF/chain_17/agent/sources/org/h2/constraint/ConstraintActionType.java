package org.h2.constraint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintActionType.class */
public enum ConstraintActionType {
    RESTRICT,
    CASCADE,
    SET_DEFAULT,
    SET_NULL;

    public String getSqlName() {
        if (this == SET_DEFAULT) {
            return "SET DEFAULT";
        }
        if (this == SET_NULL) {
            return "SET NULL";
        }
        return name();
    }
}
