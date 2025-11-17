package org.springframework.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/Type.class */
public final class Type {
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final int METHOD = 11;
    private static final int INTERNAL = 12;
    private static final String PRIMITIVE_DESCRIPTORS = "VZCBSIFJD";
    public static final Type VOID_TYPE = new Type(0, PRIMITIVE_DESCRIPTORS, 0, 1);
    public static final Type BOOLEAN_TYPE = new Type(1, PRIMITIVE_DESCRIPTORS, 1, 2);
    public static final Type CHAR_TYPE = new Type(2, PRIMITIVE_DESCRIPTORS, 2, 3);
    public static final Type BYTE_TYPE = new Type(3, PRIMITIVE_DESCRIPTORS, 3, 4);
    public static final Type SHORT_TYPE = new Type(4, PRIMITIVE_DESCRIPTORS, 4, 5);
    public static final Type INT_TYPE = new Type(5, PRIMITIVE_DESCRIPTORS, 5, 6);
    public static final Type FLOAT_TYPE = new Type(6, PRIMITIVE_DESCRIPTORS, 6, 7);
    public static final Type LONG_TYPE = new Type(7, PRIMITIVE_DESCRIPTORS, 7, 8);
    public static final Type DOUBLE_TYPE = new Type(8, PRIMITIVE_DESCRIPTORS, 8, 9);
    private final int sort;
    private final String valueBuffer;
    private final int valueBegin;
    private final int valueEnd;

    private Type(final int sort, final String valueBuffer, final int valueBegin, final int valueEnd) {
        this.sort = sort;
        this.valueBuffer = valueBuffer;
        this.valueBegin = valueBegin;
        this.valueEnd = valueEnd;
    }

    public static Type getType(final String typeDescriptor) {
        return getTypeInternal(typeDescriptor, 0, typeDescriptor.length());
    }

    public static Type getType(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return INT_TYPE;
            }
            if (clazz == Void.TYPE) {
                return VOID_TYPE;
            }
            if (clazz == Boolean.TYPE) {
                return BOOLEAN_TYPE;
            }
            if (clazz == Byte.TYPE) {
                return BYTE_TYPE;
            }
            if (clazz == Character.TYPE) {
                return CHAR_TYPE;
            }
            if (clazz == Short.TYPE) {
                return SHORT_TYPE;
            }
            if (clazz == Double.TYPE) {
                return DOUBLE_TYPE;
            }
            if (clazz == Float.TYPE) {
                return FLOAT_TYPE;
            }
            if (clazz == Long.TYPE) {
                return LONG_TYPE;
            }
            throw new AssertionError();
        }
        return getType(getDescriptor(clazz));
    }

    public static Type getType(final Constructor<?> constructor) {
        return getType(getConstructorDescriptor(constructor));
    }

    public static Type getType(final Method method) {
        return getType(getMethodDescriptor(method));
    }

    public Type getElementType() {
        int numDimensions = getDimensions();
        return getTypeInternal(this.valueBuffer, this.valueBegin + numDimensions, this.valueEnd);
    }

    public static Type getObjectType(final String internalName) {
        return new Type(internalName.charAt(0) == '[' ? 9 : 12, internalName, 0, internalName.length());
    }

    public static Type getMethodType(final String methodDescriptor) {
        return new Type(11, methodDescriptor, 0, methodDescriptor.length());
    }

    public static Type getMethodType(final Type returnType, final Type... argumentTypes) {
        return getType(getMethodDescriptor(returnType, argumentTypes));
    }

    public Type[] getArgumentTypes() {
        return getArgumentTypes(getDescriptor());
    }

    public static Type[] getArgumentTypes(final String methodDescriptor) {
        int numArgumentTypes = getArgumentCount(methodDescriptor);
        Type[] argumentTypes = new Type[numArgumentTypes];
        int currentOffset = 1;
        int currentArgumentTypeIndex = 0;
        while (methodDescriptor.charAt(currentOffset) != ')') {
            int currentArgumentTypeOffset = currentOffset;
            while (methodDescriptor.charAt(currentOffset) == '[') {
                currentOffset++;
            }
            int i = currentOffset;
            currentOffset++;
            if (methodDescriptor.charAt(i) == 'L') {
                int semiColumnOffset = methodDescriptor.indexOf(59, currentOffset);
                currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
            }
            int i2 = currentArgumentTypeIndex;
            currentArgumentTypeIndex++;
            argumentTypes[i2] = getTypeInternal(methodDescriptor, currentArgumentTypeOffset, currentOffset);
        }
        return argumentTypes;
    }

    public static Type[] getArgumentTypes(final Method method) {
        Class<?>[] classes = method.getParameterTypes();
        Type[] types = new Type[classes.length];
        for (int i = classes.length - 1; i >= 0; i--) {
            types[i] = getType(classes[i]);
        }
        return types;
    }

    public Type getReturnType() {
        return getReturnType(getDescriptor());
    }

    public static Type getReturnType(final String methodDescriptor) {
        return getTypeInternal(methodDescriptor, getReturnTypeOffset(methodDescriptor), methodDescriptor.length());
    }

    public static Type getReturnType(final Method method) {
        return getType(method.getReturnType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getReturnTypeOffset(final String methodDescriptor) {
        int currentOffset = 1;
        while (methodDescriptor.charAt(currentOffset) != ')') {
            while (methodDescriptor.charAt(currentOffset) == '[') {
                currentOffset++;
            }
            int i = currentOffset;
            currentOffset++;
            if (methodDescriptor.charAt(i) == 'L') {
                int semiColumnOffset = methodDescriptor.indexOf(59, currentOffset);
                currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
            }
        }
        return currentOffset + 1;
    }

    private static Type getTypeInternal(final String descriptorBuffer, final int descriptorBegin, final int descriptorEnd) {
        switch (descriptorBuffer.charAt(descriptorBegin)) {
            case '(':
                return new Type(11, descriptorBuffer, descriptorBegin, descriptorEnd);
            case 'B':
                return BYTE_TYPE;
            case 'C':
                return CHAR_TYPE;
            case 'D':
                return DOUBLE_TYPE;
            case 'F':
                return FLOAT_TYPE;
            case 'I':
                return INT_TYPE;
            case 'J':
                return LONG_TYPE;
            case 'L':
                return new Type(10, descriptorBuffer, descriptorBegin + 1, descriptorEnd - 1);
            case 'S':
                return SHORT_TYPE;
            case 'V':
                return VOID_TYPE;
            case 'Z':
                return BOOLEAN_TYPE;
            case '[':
                return new Type(9, descriptorBuffer, descriptorBegin, descriptorEnd);
            default:
                throw new IllegalArgumentException("Invalid descriptor: " + descriptorBuffer);
        }
    }

    public String getClassName() {
        switch (this.sort) {
            case 0:
                return "void";
            case 1:
                return "boolean";
            case 2:
                return "char";
            case 3:
                return "byte";
            case 4:
                return "short";
            case 5:
                return "int";
            case 6:
                return "float";
            case 7:
                return "long";
            case 8:
                return "double";
            case 9:
                StringBuilder stringBuilder = new StringBuilder(getElementType().getClassName());
                for (int i = getDimensions(); i > 0; i--) {
                    stringBuilder.append(ClassUtils.ARRAY_SUFFIX);
                }
                return stringBuilder.toString();
            case 10:
            case 12:
                return this.valueBuffer.substring(this.valueBegin, this.valueEnd).replace('/', '.');
            case 11:
            default:
                throw new AssertionError();
        }
    }

    public String getInternalName() {
        return this.valueBuffer.substring(this.valueBegin, this.valueEnd);
    }

    public static String getInternalName(final Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    public String getDescriptor() {
        if (this.sort == 10) {
            return this.valueBuffer.substring(this.valueBegin - 1, this.valueEnd + 1);
        }
        if (this.sort == 12) {
            return "L" + this.valueBuffer.substring(this.valueBegin, this.valueEnd) + ";";
        }
        return this.valueBuffer.substring(this.valueBegin, this.valueEnd);
    }

    public static String getDescriptor(final Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder();
        appendDescriptor(clazz, stringBuilder);
        return stringBuilder.toString();
    }

    public static String getConstructorDescriptor(final Constructor<?> constructor) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        Class<?>[] parameters = constructor.getParameterTypes();
        for (Class<?> parameter : parameters) {
            appendDescriptor(parameter, stringBuilder);
        }
        return stringBuilder.append(")V").toString();
    }

    public static String getMethodDescriptor(final Type returnType, final Type... argumentTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for (Type argumentType : argumentTypes) {
            argumentType.appendDescriptor(stringBuilder);
        }
        stringBuilder.append(')');
        returnType.appendDescriptor(stringBuilder);
        return stringBuilder.toString();
    }

    public static String getMethodDescriptor(final Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        Class<?>[] parameters = method.getParameterTypes();
        for (Class<?> parameter : parameters) {
            appendDescriptor(parameter, stringBuilder);
        }
        stringBuilder.append(')');
        appendDescriptor(method.getReturnType(), stringBuilder);
        return stringBuilder.toString();
    }

    private void appendDescriptor(final StringBuilder stringBuilder) {
        if (this.sort == 10) {
            stringBuilder.append((CharSequence) this.valueBuffer, this.valueBegin - 1, this.valueEnd + 1);
        } else if (this.sort == 12) {
            stringBuilder.append('L').append((CharSequence) this.valueBuffer, this.valueBegin, this.valueEnd).append(';');
        } else {
            stringBuilder.append((CharSequence) this.valueBuffer, this.valueBegin, this.valueEnd);
        }
    }

    private static void appendDescriptor(final Class<?> clazz, final StringBuilder stringBuilder) {
        Class<?> currentClass;
        char descriptor;
        Class<?> cls = clazz;
        while (true) {
            currentClass = cls;
            if (!currentClass.isArray()) {
                break;
            }
            stringBuilder.append('[');
            cls = currentClass.componentType();
        }
        if (currentClass.isPrimitive()) {
            if (currentClass == Integer.TYPE) {
                descriptor = 'I';
            } else if (currentClass == Void.TYPE) {
                descriptor = 'V';
            } else if (currentClass == Boolean.TYPE) {
                descriptor = 'Z';
            } else if (currentClass == Byte.TYPE) {
                descriptor = 'B';
            } else if (currentClass == Character.TYPE) {
                descriptor = 'C';
            } else if (currentClass == Short.TYPE) {
                descriptor = 'S';
            } else if (currentClass == Double.TYPE) {
                descriptor = 'D';
            } else if (currentClass == Float.TYPE) {
                descriptor = 'F';
            } else if (currentClass == Long.TYPE) {
                descriptor = 'J';
            } else {
                throw new AssertionError();
            }
            stringBuilder.append(descriptor);
            return;
        }
        stringBuilder.append('L').append(getInternalName(currentClass)).append(';');
    }

    public int getSort() {
        if (this.sort == 12) {
            return 10;
        }
        return this.sort;
    }

    public int getDimensions() {
        int numDimensions = 1;
        while (this.valueBuffer.charAt(this.valueBegin + numDimensions) == '[') {
            numDimensions++;
        }
        return numDimensions;
    }

    public int getSize() {
        switch (this.sort) {
            case 0:
                return 0;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            case 12:
                return 1;
            case 7:
            case 8:
                return 2;
            case 11:
            default:
                throw new AssertionError();
        }
    }

    public int getArgumentCount() {
        return getArgumentCount(getDescriptor());
    }

    public static int getArgumentCount(final String methodDescriptor) {
        int argumentCount = 0;
        int currentOffset = 1;
        while (methodDescriptor.charAt(currentOffset) != ')') {
            while (methodDescriptor.charAt(currentOffset) == '[') {
                currentOffset++;
            }
            int i = currentOffset;
            currentOffset++;
            if (methodDescriptor.charAt(i) == 'L') {
                int semiColumnOffset = methodDescriptor.indexOf(59, currentOffset);
                currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
            }
            argumentCount++;
        }
        return argumentCount;
    }

    public int getArgumentsAndReturnSizes() {
        return getArgumentsAndReturnSizes(getDescriptor());
    }

    public static int getArgumentsAndReturnSizes(final String methodDescriptor) {
        int argumentsSize = 1;
        int currentOffset = 1;
        int charAt = methodDescriptor.charAt(1);
        while (true) {
            int currentChar = charAt;
            if (currentChar == 41) {
                break;
            }
            if (currentChar == 74 || currentChar == 68) {
                currentOffset++;
                argumentsSize += 2;
            } else {
                while (methodDescriptor.charAt(currentOffset) == '[') {
                    currentOffset++;
                }
                int i = currentOffset;
                currentOffset++;
                if (methodDescriptor.charAt(i) == 'L') {
                    int semiColumnOffset = methodDescriptor.indexOf(59, currentOffset);
                    currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
                }
                argumentsSize++;
            }
            charAt = methodDescriptor.charAt(currentOffset);
        }
        int currentChar2 = methodDescriptor.charAt(currentOffset + 1);
        if (currentChar2 == 86) {
            return argumentsSize << 2;
        }
        int returnSize = (currentChar2 == 74 || currentChar2 == 68) ? 2 : 1;
        return (argumentsSize << 2) | returnSize;
    }

    public int getOpcode(final int opcode) {
        if (opcode == 46 || opcode == 79) {
            switch (this.sort) {
                case 0:
                case 11:
                    throw new UnsupportedOperationException();
                case 1:
                case 3:
                    return opcode + 5;
                case 2:
                    return opcode + 6;
                case 4:
                    return opcode + 7;
                case 5:
                    return opcode;
                case 6:
                    return opcode + 2;
                case 7:
                    return opcode + 1;
                case 8:
                    return opcode + 3;
                case 9:
                case 10:
                case 12:
                    return opcode + 4;
                default:
                    throw new AssertionError();
            }
        }
        switch (this.sort) {
            case 0:
                if (opcode != 172) {
                    throw new UnsupportedOperationException();
                }
                return Opcodes.RETURN;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return opcode;
            case 6:
                return opcode + 2;
            case 7:
                return opcode + 1;
            case 8:
                return opcode + 3;
            case 9:
            case 10:
            case 12:
                if (opcode != 21 && opcode != 54 && opcode != 172) {
                    throw new UnsupportedOperationException();
                }
                return opcode + 4;
            case 11:
                throw new UnsupportedOperationException();
            default:
                throw new AssertionError();
        }
    }

    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Type)) {
            return false;
        }
        Type other = (Type) object;
        if ((this.sort == 12 ? 10 : this.sort) != (other.sort == 12 ? 10 : other.sort)) {
            return false;
        }
        int begin = this.valueBegin;
        int end = this.valueEnd;
        int otherBegin = other.valueBegin;
        int otherEnd = other.valueEnd;
        if (end - begin != otherEnd - otherBegin) {
            return false;
        }
        int i = begin;
        int j = otherBegin;
        while (i < end) {
            if (this.valueBuffer.charAt(i) == other.valueBuffer.charAt(j)) {
                i++;
                j++;
            } else {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 13 * (this.sort == 12 ? 10 : this.sort);
        if (this.sort >= 9) {
            int end = this.valueEnd;
            for (int i = this.valueBegin; i < end; i++) {
                hashCode = 17 * (hashCode + this.valueBuffer.charAt(i));
            }
        }
        return hashCode;
    }

    public String toString() {
        return getDescriptor();
    }
}
