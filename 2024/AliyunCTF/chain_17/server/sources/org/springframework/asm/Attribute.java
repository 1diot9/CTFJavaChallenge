package org.springframework.asm;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/Attribute.class */
public class Attribute {
    public final String type;
    private byte[] content;
    Attribute nextAttribute;

    /* JADX INFO: Access modifiers changed from: protected */
    public Attribute(final String type) {
        this.type = type;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return new Label[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Attribute read(final ClassReader classReader, final int offset, final int length, final char[] charBuffer, final int codeAttributeOffset, final Label[] labels) {
        Attribute attribute = new Attribute(this.type);
        attribute.content = new byte[length];
        System.arraycopy(classReader.classFileBuffer, offset, attribute.content, 0, length);
        return attribute;
    }

    protected ByteVector write(final ClassWriter classWriter, final byte[] code, final int codeLength, final int maxStack, final int maxLocals) {
        return new ByteVector(this.content);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getAttributeCount() {
        int count = 0;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                count++;
                attribute = attribute2.nextAttribute;
            } else {
                return count;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int computeAttributesSize(final SymbolTable symbolTable) {
        return computeAttributesSize(symbolTable, null, 0, -1, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int computeAttributesSize(final SymbolTable symbolTable, final byte[] code, final int codeLength, final int maxStack, final int maxLocals) {
        ClassWriter classWriter = symbolTable.classWriter;
        int size = 0;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                symbolTable.addConstantUtf8(attribute2.type);
                size += 6 + attribute2.write(classWriter, code, codeLength, maxStack, maxLocals).length;
                attribute = attribute2.nextAttribute;
            } else {
                return size;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeAttributesSize(final SymbolTable symbolTable, final int accessFlags, final int signatureIndex) {
        int size = 0;
        if ((accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && symbolTable.getMajorVersion() < 49) {
            symbolTable.addConstantUtf8("Synthetic");
            size = 0 + 6;
        }
        if (signatureIndex != 0) {
            symbolTable.addConstantUtf8("Signature");
            size += 8;
        }
        if ((accessFlags & 131072) != 0) {
            symbolTable.addConstantUtf8("Deprecated");
            size += 6;
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void putAttributes(final SymbolTable symbolTable, final ByteVector output) {
        putAttributes(symbolTable, null, 0, -1, -1, output);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void putAttributes(final SymbolTable symbolTable, final byte[] code, final int codeLength, final int maxStack, final int maxLocals, final ByteVector output) {
        ClassWriter classWriter = symbolTable.classWriter;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                ByteVector attributeContent = attribute2.write(classWriter, code, codeLength, maxStack, maxLocals);
                output.putShort(symbolTable.addConstantUtf8(attribute2.type)).putInt(attributeContent.length);
                output.putByteArray(attributeContent.data, 0, attributeContent.length);
                attribute = attribute2.nextAttribute;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putAttributes(final SymbolTable symbolTable, final int accessFlags, final int signatureIndex, final ByteVector output) {
        if ((accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && symbolTable.getMajorVersion() < 49) {
            output.putShort(symbolTable.addConstantUtf8("Synthetic")).putInt(0);
        }
        if (signatureIndex != 0) {
            output.putShort(symbolTable.addConstantUtf8("Signature")).putInt(2).putShort(signatureIndex);
        }
        if ((accessFlags & 131072) != 0) {
            output.putShort(symbolTable.addConstantUtf8("Deprecated")).putInt(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/Attribute$Set.class */
    public static final class Set {
        private static final int SIZE_INCREMENT = 6;
        private int size;
        private Attribute[] data = new Attribute[6];

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addAttributes(final Attribute attributeList) {
            Attribute attribute = attributeList;
            while (true) {
                Attribute attribute2 = attribute;
                if (attribute2 != null) {
                    if (!contains(attribute2)) {
                        add(attribute2);
                    }
                    attribute = attribute2.nextAttribute;
                } else {
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Attribute[] toArray() {
            Attribute[] result = new Attribute[this.size];
            System.arraycopy(this.data, 0, result, 0, this.size);
            return result;
        }

        private boolean contains(final Attribute attribute) {
            for (int i = 0; i < this.size; i++) {
                if (this.data[i].type.equals(attribute.type)) {
                    return true;
                }
            }
            return false;
        }

        private void add(final Attribute attribute) {
            if (this.size >= this.data.length) {
                Attribute[] newData = new Attribute[this.data.length + 6];
                System.arraycopy(this.data, 0, newData, 0, this.size);
                this.data = newData;
            }
            Attribute[] attributeArr = this.data;
            int i = this.size;
            this.size = i + 1;
            attributeArr[i] = attribute;
        }
    }
}
