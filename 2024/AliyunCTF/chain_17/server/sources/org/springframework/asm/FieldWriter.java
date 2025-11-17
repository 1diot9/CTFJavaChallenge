package org.springframework.asm;

import org.springframework.asm.Attribute;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/FieldWriter.class */
public final class FieldWriter extends FieldVisitor {
    private final SymbolTable symbolTable;
    private final int accessFlags;
    private final int nameIndex;
    private final int descriptorIndex;
    private int signatureIndex;
    private int constantValueIndex;
    private AnnotationWriter lastRuntimeVisibleAnnotation;
    private AnnotationWriter lastRuntimeInvisibleAnnotation;
    private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
    private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
    private Attribute firstAttribute;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldWriter(final SymbolTable symbolTable, final int access, final String name, final String descriptor, final String signature, final Object constantValue) {
        super(Opcodes.ASM9);
        this.symbolTable = symbolTable;
        this.accessFlags = access;
        this.nameIndex = symbolTable.addConstantUtf8(name);
        this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
        if (signature != null) {
            this.signatureIndex = symbolTable.addConstantUtf8(signature);
        }
        if (constantValue != null) {
            this.constantValueIndex = symbolTable.addConstant(constantValue).index;
        }
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (visible) {
            AnnotationWriter create = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
            this.lastRuntimeVisibleAnnotation = create;
            return create;
        }
        AnnotationWriter create2 = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
        this.lastRuntimeInvisibleAnnotation = create2;
        return create2;
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (visible) {
            AnnotationWriter create = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
            this.lastRuntimeVisibleTypeAnnotation = create;
            return create;
        }
        AnnotationWriter create2 = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
        this.lastRuntimeInvisibleTypeAnnotation = create2;
        return create2;
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitAttribute(final Attribute attribute) {
        attribute.nextAttribute = this.firstAttribute;
        this.firstAttribute = attribute;
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitEnd() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeFieldInfoSize() {
        int size = 8;
        if (this.constantValueIndex != 0) {
            this.symbolTable.addConstantUtf8("ConstantValue");
            size = 8 + 8;
        }
        int size2 = size + Attribute.computeAttributesSize(this.symbolTable, this.accessFlags, this.signatureIndex) + AnnotationWriter.computeAnnotationsSize(this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
        if (this.firstAttribute != null) {
            size2 += this.firstAttribute.computeAttributesSize(this.symbolTable);
        }
        return size2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putFieldInfo(final ByteVector output) {
        boolean useSyntheticAttribute = this.symbolTable.getMajorVersion() < 49;
        int mask = useSyntheticAttribute ? Opcodes.ACC_SYNTHETIC : 0;
        output.putShort(this.accessFlags & (mask ^ (-1))).putShort(this.nameIndex).putShort(this.descriptorIndex);
        int attributesCount = 0;
        if (this.constantValueIndex != 0) {
            attributesCount = 0 + 1;
        }
        if ((this.accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && useSyntheticAttribute) {
            attributesCount++;
        }
        if (this.signatureIndex != 0) {
            attributesCount++;
        }
        if ((this.accessFlags & 131072) != 0) {
            attributesCount++;
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            attributesCount++;
        }
        if (this.firstAttribute != null) {
            attributesCount += this.firstAttribute.getAttributeCount();
        }
        output.putShort(attributesCount);
        if (this.constantValueIndex != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ConstantValue")).putInt(2).putShort(this.constantValueIndex);
        }
        Attribute.putAttributes(this.symbolTable, this.accessFlags, this.signatureIndex, output);
        AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, output);
        if (this.firstAttribute != null) {
            this.firstAttribute.putAttributes(this.symbolTable, output);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void collectAttributePrototypes(final Attribute.Set attributePrototypes) {
        attributePrototypes.addAttributes(this.firstAttribute);
    }
}
