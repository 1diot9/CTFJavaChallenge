package org.springframework.asm;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/TypePath.class */
public final class TypePath {
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    private final byte[] typePathContainer;
    private final int typePathOffset;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypePath(final byte[] typePathContainer, final int typePathOffset) {
        this.typePathContainer = typePathContainer;
        this.typePathOffset = typePathOffset;
    }

    public int getLength() {
        return this.typePathContainer[this.typePathOffset];
    }

    public int getStep(final int index) {
        return this.typePathContainer[this.typePathOffset + (2 * index) + 1];
    }

    public int getStepArgument(final int index) {
        return this.typePathContainer[this.typePathOffset + (2 * index) + 2];
    }

    public static TypePath fromString(final String typePath) {
        int typeArg;
        char c;
        if (typePath == null || typePath.length() == 0) {
            return null;
        }
        int typePathLength = typePath.length();
        ByteVector output = new ByteVector(typePathLength);
        output.putByte(0);
        int typePathIndex = 0;
        while (typePathIndex < typePathLength) {
            int i = typePathIndex;
            typePathIndex++;
            char c2 = typePath.charAt(i);
            if (c2 == '[') {
                output.put11(0, 0);
            } else if (c2 == '.') {
                output.put11(1, 0);
            } else if (c2 == '*') {
                output.put11(2, 0);
            } else if (c2 >= '0' && c2 <= '9') {
                int i2 = c2;
                while (true) {
                    typeArg = i2 - 48;
                    if (typePathIndex >= typePathLength) {
                        break;
                    }
                    int i3 = typePathIndex;
                    typePathIndex++;
                    c = typePath.charAt(i3);
                    if (c < '0' || c > '9') {
                        break;
                    }
                    i2 = (typeArg * 10) + c;
                }
                if (c != ';') {
                    throw new IllegalArgumentException();
                }
                output.put11(3, typeArg);
            } else {
                throw new IllegalArgumentException();
            }
        }
        output.data[0] = (byte) (output.length / 2);
        return new TypePath(output.data, 0);
    }

    public String toString() {
        int length = getLength();
        StringBuilder result = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            switch (getStep(i)) {
                case 0:
                    result.append('[');
                    break;
                case 1:
                    result.append('.');
                    break;
                case 2:
                    result.append('*');
                    break;
                case 3:
                    result.append(getStepArgument(i)).append(';');
                    break;
                default:
                    throw new AssertionError();
            }
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void put(final TypePath typePath, final ByteVector output) {
        if (typePath == null) {
            output.putByte(0);
        } else {
            int length = (typePath.typePathContainer[typePath.typePathOffset] * 2) + 1;
            output.putByteArray(typePath.typePathContainer, typePath.typePathOffset, length);
        }
    }
}
