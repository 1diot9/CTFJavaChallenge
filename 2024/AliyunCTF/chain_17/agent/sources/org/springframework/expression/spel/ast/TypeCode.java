package org.springframework.expression.spel.ast;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/TypeCode.class */
public enum TypeCode {
    OBJECT(Object.class),
    BOOLEAN(Boolean.TYPE),
    CHAR(Character.TYPE),
    BYTE(Byte.TYPE),
    SHORT(Short.TYPE),
    INT(Integer.TYPE),
    LONG(Long.TYPE),
    FLOAT(Float.TYPE),
    DOUBLE(Double.TYPE);

    private final Class<?> type;

    TypeCode(Class type) {
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static TypeCode forName(String name) {
        for (TypeCode typeCode : values()) {
            if (typeCode.name().equalsIgnoreCase(name)) {
                return typeCode;
            }
        }
        return OBJECT;
    }

    public static TypeCode forClass(Class<?> clazz) {
        for (TypeCode typeCode : values()) {
            if (typeCode.getType() == clazz) {
                return typeCode;
            }
        }
        return OBJECT;
    }
}
