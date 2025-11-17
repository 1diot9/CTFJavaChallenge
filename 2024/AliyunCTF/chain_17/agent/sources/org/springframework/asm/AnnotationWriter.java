package org.springframework.asm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/AnnotationWriter.class */
public final class AnnotationWriter extends AnnotationVisitor {
    private final SymbolTable symbolTable;
    private final boolean useNamedValues;
    private final ByteVector annotation;
    private final int numElementValuePairsOffset;
    private int numElementValuePairs;
    private final AnnotationWriter previousAnnotation;
    private AnnotationWriter nextAnnotation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationWriter(final SymbolTable symbolTable, final boolean useNamedValues, final ByteVector annotation, final AnnotationWriter previousAnnotation) {
        super(Opcodes.ASM9);
        this.symbolTable = symbolTable;
        this.useNamedValues = useNamedValues;
        this.annotation = annotation;
        this.numElementValuePairsOffset = annotation.length == 0 ? -1 : annotation.length - 2;
        this.previousAnnotation = previousAnnotation;
        if (previousAnnotation != null) {
            previousAnnotation.nextAnnotation = this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationWriter create(final SymbolTable symbolTable, final String descriptor, final AnnotationWriter previousAnnotation) {
        ByteVector annotation = new ByteVector();
        annotation.putShort(symbolTable.addConstantUtf8(descriptor)).putShort(0);
        return new AnnotationWriter(symbolTable, true, annotation, previousAnnotation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationWriter create(final SymbolTable symbolTable, final int typeRef, final TypePath typePath, final String descriptor, final AnnotationWriter previousAnnotation) {
        ByteVector typeAnnotation = new ByteVector();
        TypeReference.putTarget(typeRef, typeAnnotation);
        TypePath.put(typePath, typeAnnotation);
        typeAnnotation.putShort(symbolTable.addConstantUtf8(descriptor)).putShort(0);
        return new AnnotationWriter(symbolTable, true, typeAnnotation, previousAnnotation);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visit(final String name, final Object value) {
        this.numElementValuePairs++;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        if (value instanceof String) {
            this.annotation.put12(115, this.symbolTable.addConstantUtf8((String) value));
            return;
        }
        if (value instanceof Byte) {
            this.annotation.put12(66, this.symbolTable.addConstantInteger(((Byte) value).byteValue()).index);
            return;
        }
        if (value instanceof Boolean) {
            int booleanValue = ((Boolean) value).booleanValue() ? 1 : 0;
            this.annotation.put12(90, this.symbolTable.addConstantInteger(booleanValue).index);
            return;
        }
        if (value instanceof Character) {
            this.annotation.put12(67, this.symbolTable.addConstantInteger(((Character) value).charValue()).index);
            return;
        }
        if (value instanceof Short) {
            this.annotation.put12(83, this.symbolTable.addConstantInteger(((Short) value).shortValue()).index);
            return;
        }
        if (value instanceof Type) {
            this.annotation.put12(99, this.symbolTable.addConstantUtf8(((Type) value).getDescriptor()));
            return;
        }
        if (value instanceof byte[]) {
            byte[] byteArray = (byte[]) value;
            this.annotation.put12(91, byteArray.length);
            for (byte byteValue : byteArray) {
                this.annotation.put12(66, this.symbolTable.addConstantInteger(byteValue).index);
            }
            return;
        }
        if (value instanceof boolean[]) {
            boolean[] booleanArray = (boolean[]) value;
            this.annotation.put12(91, booleanArray.length);
            int length = booleanArray.length;
            for (int i = 0; i < length; i++) {
                boolean booleanValue2 = booleanArray[i];
                this.annotation.put12(90, this.symbolTable.addConstantInteger(booleanValue2 ? 1 : 0).index);
            }
            return;
        }
        if (value instanceof short[]) {
            short[] shortArray = (short[]) value;
            this.annotation.put12(91, shortArray.length);
            for (short shortValue : shortArray) {
                this.annotation.put12(83, this.symbolTable.addConstantInteger(shortValue).index);
            }
            return;
        }
        if (value instanceof char[]) {
            char[] charArray = (char[]) value;
            this.annotation.put12(91, charArray.length);
            for (char charValue : charArray) {
                this.annotation.put12(67, this.symbolTable.addConstantInteger(charValue).index);
            }
            return;
        }
        if (value instanceof int[]) {
            int[] intArray = (int[]) value;
            this.annotation.put12(91, intArray.length);
            for (int intValue : intArray) {
                this.annotation.put12(73, this.symbolTable.addConstantInteger(intValue).index);
            }
            return;
        }
        if (value instanceof long[]) {
            long[] longArray = (long[]) value;
            this.annotation.put12(91, longArray.length);
            for (long longValue : longArray) {
                this.annotation.put12(74, this.symbolTable.addConstantLong(longValue).index);
            }
            return;
        }
        if (value instanceof float[]) {
            float[] floatArray = (float[]) value;
            this.annotation.put12(91, floatArray.length);
            for (float floatValue : floatArray) {
                this.annotation.put12(70, this.symbolTable.addConstantFloat(floatValue).index);
            }
            return;
        }
        if (value instanceof double[]) {
            double[] doubleArray = (double[]) value;
            this.annotation.put12(91, doubleArray.length);
            for (double doubleValue : doubleArray) {
                this.annotation.put12(68, this.symbolTable.addConstantDouble(doubleValue).index);
            }
            return;
        }
        Symbol symbol = this.symbolTable.addConstant(value);
        this.annotation.put12(".s.IFJDCS".charAt(symbol.tag), symbol.index);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnum(final String name, final String descriptor, final String value) {
        this.numElementValuePairs++;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(101, this.symbolTable.addConstantUtf8(descriptor)).putShort(this.symbolTable.addConstantUtf8(value));
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        this.numElementValuePairs++;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(64, this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        return new AnnotationWriter(this.symbolTable, true, this.annotation, null);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitArray(final String name) {
        this.numElementValuePairs++;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(91, 0);
        return new AnnotationWriter(this.symbolTable, false, this.annotation, null);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnd() {
        if (this.numElementValuePairsOffset != -1) {
            byte[] data = this.annotation.data;
            data[this.numElementValuePairsOffset] = (byte) (this.numElementValuePairs >>> 8);
            data[this.numElementValuePairsOffset + 1] = (byte) this.numElementValuePairs;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeAnnotationsSize(final String attributeName) {
        if (attributeName != null) {
            this.symbolTable.addConstantUtf8(attributeName);
        }
        int attributeSize = 8;
        AnnotationWriter annotationWriter = this;
        while (true) {
            AnnotationWriter annotationWriter2 = annotationWriter;
            if (annotationWriter2 != null) {
                attributeSize += annotationWriter2.annotation.length;
                annotationWriter = annotationWriter2.previousAnnotation;
            } else {
                return attributeSize;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeAnnotationsSize(final AnnotationWriter lastRuntimeVisibleAnnotation, final AnnotationWriter lastRuntimeInvisibleAnnotation, final AnnotationWriter lastRuntimeVisibleTypeAnnotation, final AnnotationWriter lastRuntimeInvisibleTypeAnnotation) {
        int size = 0;
        if (lastRuntimeVisibleAnnotation != null) {
            size = 0 + lastRuntimeVisibleAnnotation.computeAnnotationsSize("RuntimeVisibleAnnotations");
        }
        if (lastRuntimeInvisibleAnnotation != null) {
            size += lastRuntimeInvisibleAnnotation.computeAnnotationsSize("RuntimeInvisibleAnnotations");
        }
        if (lastRuntimeVisibleTypeAnnotation != null) {
            size += lastRuntimeVisibleTypeAnnotation.computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
        }
        if (lastRuntimeInvisibleTypeAnnotation != null) {
            size += lastRuntimeInvisibleTypeAnnotation.computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putAnnotations(final int attributeNameIndex, final ByteVector output) {
        int attributeLength = 2;
        int numAnnotations = 0;
        AnnotationWriter firstAnnotation = null;
        for (AnnotationWriter annotationWriter = this; annotationWriter != null; annotationWriter = annotationWriter.previousAnnotation) {
            annotationWriter.visitEnd();
            attributeLength += annotationWriter.annotation.length;
            numAnnotations++;
            firstAnnotation = annotationWriter;
        }
        output.putShort(attributeNameIndex);
        output.putInt(attributeLength);
        output.putShort(numAnnotations);
        AnnotationWriter annotationWriter2 = firstAnnotation;
        while (true) {
            AnnotationWriter annotationWriter3 = annotationWriter2;
            if (annotationWriter3 != null) {
                output.putByteArray(annotationWriter3.annotation.data, 0, annotationWriter3.annotation.length);
                annotationWriter2 = annotationWriter3.nextAnnotation;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putAnnotations(final SymbolTable symbolTable, final AnnotationWriter lastRuntimeVisibleAnnotation, final AnnotationWriter lastRuntimeInvisibleAnnotation, final AnnotationWriter lastRuntimeVisibleTypeAnnotation, final AnnotationWriter lastRuntimeInvisibleTypeAnnotation, final ByteVector output) {
        if (lastRuntimeVisibleAnnotation != null) {
            lastRuntimeVisibleAnnotation.putAnnotations(symbolTable.addConstantUtf8("RuntimeVisibleAnnotations"), output);
        }
        if (lastRuntimeInvisibleAnnotation != null) {
            lastRuntimeInvisibleAnnotation.putAnnotations(symbolTable.addConstantUtf8("RuntimeInvisibleAnnotations"), output);
        }
        if (lastRuntimeVisibleTypeAnnotation != null) {
            lastRuntimeVisibleTypeAnnotation.putAnnotations(symbolTable.addConstantUtf8("RuntimeVisibleTypeAnnotations"), output);
        }
        if (lastRuntimeInvisibleTypeAnnotation != null) {
            lastRuntimeInvisibleTypeAnnotation.putAnnotations(symbolTable.addConstantUtf8("RuntimeInvisibleTypeAnnotations"), output);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeParameterAnnotationsSize(final String attributeName, final AnnotationWriter[] annotationWriters, final int annotableParameterCount) {
        int attributeSize = 7 + (2 * annotableParameterCount);
        for (int i = 0; i < annotableParameterCount; i++) {
            AnnotationWriter annotationWriter = annotationWriters[i];
            attributeSize += annotationWriter == null ? 0 : annotationWriter.computeAnnotationsSize(attributeName) - 8;
        }
        return attributeSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putParameterAnnotations(final int attributeNameIndex, final AnnotationWriter[] annotationWriters, final int annotableParameterCount, final ByteVector output) {
        int attributeLength = 1 + (2 * annotableParameterCount);
        for (int i = 0; i < annotableParameterCount; i++) {
            AnnotationWriter annotationWriter = annotationWriters[i];
            attributeLength += annotationWriter == null ? 0 : annotationWriter.computeAnnotationsSize(null) - 8;
        }
        output.putShort(attributeNameIndex);
        output.putInt(attributeLength);
        output.putByte(annotableParameterCount);
        for (int i2 = 0; i2 < annotableParameterCount; i2++) {
            AnnotationWriter firstAnnotation = null;
            int numAnnotations = 0;
            for (AnnotationWriter annotationWriter2 = annotationWriters[i2]; annotationWriter2 != null; annotationWriter2 = annotationWriter2.previousAnnotation) {
                annotationWriter2.visitEnd();
                numAnnotations++;
                firstAnnotation = annotationWriter2;
            }
            output.putShort(numAnnotations);
            AnnotationWriter annotationWriter3 = firstAnnotation;
            while (true) {
                AnnotationWriter annotationWriter4 = annotationWriter3;
                if (annotationWriter4 != null) {
                    output.putByteArray(annotationWriter4.annotation.data, 0, annotationWriter4.annotation.length);
                    annotationWriter3 = annotationWriter4.nextAnnotation;
                }
            }
        }
    }
}
