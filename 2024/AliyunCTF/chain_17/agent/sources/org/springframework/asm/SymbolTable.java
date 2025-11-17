package org.springframework.asm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/SymbolTable.class */
public final class SymbolTable {
    final ClassWriter classWriter;
    private final ClassReader sourceClassReader;
    private int majorVersion;
    private String className;
    private int entryCount;
    private Entry[] entries;
    private int constantPoolCount;
    private ByteVector constantPool;
    private int bootstrapMethodCount;
    private ByteVector bootstrapMethods;
    private int typeCount;
    private Entry[] typeTable;
    private int labelCount;
    private LabelEntry[] labelTable;
    private LabelEntry[] labelEntries;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SymbolTable(final ClassWriter classWriter) {
        this.classWriter = classWriter;
        this.sourceClassReader = null;
        this.entries = new Entry[256];
        this.constantPoolCount = 1;
        this.constantPool = new ByteVector();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SymbolTable(final ClassWriter classWriter, final ClassReader classReader) {
        this.classWriter = classWriter;
        this.sourceClassReader = classReader;
        byte[] inputBytes = classReader.classFileBuffer;
        int constantPoolOffset = classReader.getItem(1) - 1;
        int constantPoolLength = classReader.header - constantPoolOffset;
        this.constantPoolCount = classReader.getItemCount();
        this.constantPool = new ByteVector(constantPoolLength);
        this.constantPool.putByteArray(inputBytes, constantPoolOffset, constantPoolLength);
        this.entries = new Entry[this.constantPoolCount * 2];
        char[] charBuffer = new char[classReader.getMaxStringLength()];
        boolean hasBootstrapMethods = false;
        int i = 1;
        while (true) {
            int itemIndex = i;
            if (itemIndex < this.constantPoolCount) {
                int itemOffset = classReader.getItem(itemIndex);
                byte b = inputBytes[itemOffset - 1];
                switch (b) {
                    case 1:
                        addConstantUtf8(itemIndex, classReader.readUtf(itemIndex, charBuffer));
                        break;
                    case 2:
                    case 13:
                    case 14:
                    default:
                        throw new IllegalArgumentException();
                    case 3:
                    case 4:
                        addConstantIntegerOrFloat(itemIndex, b, classReader.readInt(itemOffset));
                        break;
                    case 5:
                    case 6:
                        addConstantLongOrDouble(itemIndex, b, classReader.readLong(itemOffset));
                        break;
                    case 7:
                    case 8:
                    case 16:
                    case 19:
                    case 20:
                        addConstantUtf8Reference(itemIndex, b, classReader.readUTF8(itemOffset, charBuffer));
                        break;
                    case 9:
                    case 10:
                    case 11:
                        int nameAndTypeItemOffset = classReader.getItem(classReader.readUnsignedShort(itemOffset + 2));
                        addConstantMemberReference(itemIndex, b, classReader.readClass(itemOffset, charBuffer), classReader.readUTF8(nameAndTypeItemOffset, charBuffer), classReader.readUTF8(nameAndTypeItemOffset + 2, charBuffer));
                        break;
                    case 12:
                        addConstantNameAndType(itemIndex, classReader.readUTF8(itemOffset, charBuffer), classReader.readUTF8(itemOffset + 2, charBuffer));
                        break;
                    case 15:
                        int memberRefItemOffset = classReader.getItem(classReader.readUnsignedShort(itemOffset + 1));
                        int nameAndTypeItemOffset2 = classReader.getItem(classReader.readUnsignedShort(memberRefItemOffset + 2));
                        addConstantMethodHandle(itemIndex, classReader.readByte(itemOffset), classReader.readClass(memberRefItemOffset, charBuffer), classReader.readUTF8(nameAndTypeItemOffset2, charBuffer), classReader.readUTF8(nameAndTypeItemOffset2 + 2, charBuffer));
                        break;
                    case 17:
                    case 18:
                        hasBootstrapMethods = true;
                        int nameAndTypeItemOffset3 = classReader.getItem(classReader.readUnsignedShort(itemOffset + 2));
                        addConstantDynamicOrInvokeDynamicReference(b, itemIndex, classReader.readUTF8(nameAndTypeItemOffset3, charBuffer), classReader.readUTF8(nameAndTypeItemOffset3 + 2, charBuffer), classReader.readUnsignedShort(itemOffset));
                        break;
                }
                i = itemIndex + ((b == 5 || b == 6) ? 2 : 1);
            } else {
                if (hasBootstrapMethods) {
                    copyBootstrapMethods(classReader, charBuffer);
                    return;
                }
                return;
            }
        }
    }

    private void copyBootstrapMethods(final ClassReader classReader, final char[] charBuffer) {
        int hashCode;
        byte[] inputBytes = classReader.classFileBuffer;
        int currentAttributeOffset = classReader.getFirstAttributeOffset();
        int i = classReader.readUnsignedShort(currentAttributeOffset - 2);
        while (true) {
            if (i <= 0) {
                break;
            }
            String attributeName = classReader.readUTF8(currentAttributeOffset, charBuffer);
            if ("BootstrapMethods".equals(attributeName)) {
                this.bootstrapMethodCount = classReader.readUnsignedShort(currentAttributeOffset + 6);
                break;
            } else {
                currentAttributeOffset += 6 + classReader.readInt(currentAttributeOffset + 2);
                i--;
            }
        }
        if (this.bootstrapMethodCount > 0) {
            int bootstrapMethodsOffset = currentAttributeOffset + 8;
            int bootstrapMethodsLength = classReader.readInt(currentAttributeOffset + 2) - 2;
            this.bootstrapMethods = new ByteVector(bootstrapMethodsLength);
            this.bootstrapMethods.putByteArray(inputBytes, bootstrapMethodsOffset, bootstrapMethodsLength);
            int currentOffset = bootstrapMethodsOffset;
            for (int i2 = 0; i2 < this.bootstrapMethodCount; i2++) {
                int offset = currentOffset - bootstrapMethodsOffset;
                int bootstrapMethodRef = classReader.readUnsignedShort(currentOffset);
                int currentOffset2 = currentOffset + 2;
                int numBootstrapArguments = classReader.readUnsignedShort(currentOffset2);
                currentOffset = currentOffset2 + 2;
                int hashCode2 = classReader.readConst(bootstrapMethodRef, charBuffer).hashCode();
                while (true) {
                    hashCode = hashCode2;
                    int i3 = numBootstrapArguments;
                    numBootstrapArguments--;
                    if (i3 > 0) {
                        int bootstrapArgument = classReader.readUnsignedShort(currentOffset);
                        currentOffset += 2;
                        hashCode2 = hashCode ^ classReader.readConst(bootstrapArgument, charBuffer).hashCode();
                    }
                }
                add(new Entry(i2, 64, offset, hashCode & Integer.MAX_VALUE));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassReader getSource() {
        return this.sourceClassReader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMajorVersion() {
        return this.majorVersion;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getClassName() {
        return this.className;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int setMajorVersionAndClassName(final int majorVersion, final String className) {
        this.majorVersion = majorVersion;
        this.className = className;
        return addConstantClass(className).index;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getConstantPoolCount() {
        return this.constantPoolCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getConstantPoolLength() {
        return this.constantPool.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putConstantPool(final ByteVector output) {
        output.putShort(this.constantPoolCount).putByteArray(this.constantPool.data, 0, this.constantPool.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeBootstrapMethodsSize() {
        if (this.bootstrapMethods != null) {
            addConstantUtf8("BootstrapMethods");
            return 8 + this.bootstrapMethods.length;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putBootstrapMethods(final ByteVector output) {
        if (this.bootstrapMethods != null) {
            output.putShort(addConstantUtf8("BootstrapMethods")).putInt(this.bootstrapMethods.length + 2).putShort(this.bootstrapMethodCount).putByteArray(this.bootstrapMethods.data, 0, this.bootstrapMethods.length);
        }
    }

    private Entry get(final int hashCode) {
        return this.entries[hashCode % this.entries.length];
    }

    private Entry put(final Entry entry) {
        if (this.entryCount > (this.entries.length * 3) / 4) {
            int currentCapacity = this.entries.length;
            int newCapacity = (currentCapacity * 2) + 1;
            Entry[] newEntries = new Entry[newCapacity];
            for (int i = currentCapacity - 1; i >= 0; i--) {
                Entry entry2 = this.entries[i];
                while (true) {
                    Entry currentEntry = entry2;
                    if (currentEntry != null) {
                        int newCurrentEntryIndex = currentEntry.hashCode % newCapacity;
                        Entry nextEntry = currentEntry.next;
                        currentEntry.next = newEntries[newCurrentEntryIndex];
                        newEntries[newCurrentEntryIndex] = currentEntry;
                        entry2 = nextEntry;
                    }
                }
            }
            this.entries = newEntries;
        }
        this.entryCount++;
        int index = entry.hashCode % this.entries.length;
        entry.next = this.entries[index];
        this.entries[index] = entry;
        return entry;
    }

    private void add(final Entry entry) {
        this.entryCount++;
        int index = entry.hashCode % this.entries.length;
        entry.next = this.entries[index];
        this.entries[index] = entry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstant(final Object value) {
        if (value instanceof Integer) {
            return addConstantInteger(((Integer) value).intValue());
        }
        if (value instanceof Byte) {
            return addConstantInteger(((Byte) value).intValue());
        }
        if (value instanceof Character) {
            return addConstantInteger(((Character) value).charValue());
        }
        if (value instanceof Short) {
            return addConstantInteger(((Short) value).intValue());
        }
        if (value instanceof Boolean) {
            return addConstantInteger(((Boolean) value).booleanValue() ? 1 : 0);
        }
        if (value instanceof Float) {
            return addConstantFloat(((Float) value).floatValue());
        }
        if (value instanceof Long) {
            return addConstantLong(((Long) value).longValue());
        }
        if (value instanceof Double) {
            return addConstantDouble(((Double) value).doubleValue());
        }
        if (value instanceof String) {
            return addConstantString((String) value);
        }
        if (value instanceof Type) {
            Type type = (Type) value;
            int typeSort = type.getSort();
            if (typeSort == 10) {
                return addConstantClass(type.getInternalName());
            }
            if (typeSort == 11) {
                return addConstantMethodType(type.getDescriptor());
            }
            return addConstantClass(type.getDescriptor());
        }
        if (value instanceof Handle) {
            Handle handle = (Handle) value;
            return addConstantMethodHandle(handle.getTag(), handle.getOwner(), handle.getName(), handle.getDesc(), handle.isInterface());
        }
        if (value instanceof ConstantDynamic) {
            ConstantDynamic constantDynamic = (ConstantDynamic) value;
            return addConstantDynamic(constantDynamic.getName(), constantDynamic.getDescriptor(), constantDynamic.getBootstrapMethod(), constantDynamic.getBootstrapMethodArgumentsUnsafe());
        }
        throw new IllegalArgumentException("value " + value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantClass(final String value) {
        return addConstantUtf8Reference(7, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantFieldref(final String owner, final String name, final String descriptor) {
        return addConstantMemberReference(9, owner, name, descriptor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantMethodref(final String owner, final String name, final String descriptor, final boolean isInterface) {
        int tag = isInterface ? 11 : 10;
        return addConstantMemberReference(tag, owner, name, descriptor);
    }

    private Entry addConstantMemberReference(final int tag, final String owner, final String name, final String descriptor) {
        int hashCode = hash(tag, owner, name, descriptor);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == tag && entry2.hashCode == hashCode && entry2.owner.equals(owner) && entry2.name.equals(name) && entry2.value.equals(descriptor)) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                this.constantPool.put122(tag, addConstantClass(owner).index, addConstantNameAndType(name, descriptor));
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, tag, owner, name, descriptor, 0L, hashCode));
            }
        }
    }

    private void addConstantMemberReference(final int index, final int tag, final String owner, final String name, final String descriptor) {
        add(new Entry(index, tag, owner, name, descriptor, 0L, hash(tag, owner, name, descriptor)));
    }

    Symbol addConstantString(final String value) {
        return addConstantUtf8Reference(8, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantInteger(final int value) {
        return addConstantIntegerOrFloat(3, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantFloat(final float value) {
        return addConstantIntegerOrFloat(4, Float.floatToRawIntBits(value));
    }

    private Symbol addConstantIntegerOrFloat(final int tag, final int value) {
        int hashCode = hash(tag, value);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == tag && entry2.hashCode == hashCode && entry2.data == value) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                this.constantPool.putByte(tag).putInt(value);
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, tag, value, hashCode));
            }
        }
    }

    private void addConstantIntegerOrFloat(final int index, final int tag, final int value) {
        add(new Entry(index, tag, value, hash(tag, value)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantLong(final long value) {
        return addConstantLongOrDouble(5, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantDouble(final double value) {
        return addConstantLongOrDouble(6, Double.doubleToRawLongBits(value));
    }

    private Symbol addConstantLongOrDouble(final int tag, final long value) {
        int hashCode = hash(tag, value);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == tag && entry2.hashCode == hashCode && entry2.data == value) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                int index = this.constantPoolCount;
                this.constantPool.putByte(tag).putLong(value);
                this.constantPoolCount += 2;
                return put(new Entry(index, tag, value, hashCode));
            }
        }
    }

    private void addConstantLongOrDouble(final int index, final int tag, final long value) {
        add(new Entry(index, tag, value, hash(tag, value)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addConstantNameAndType(final String name, final String descriptor) {
        int hashCode = hash(12, name, descriptor);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 12 && entry2.hashCode == hashCode && entry2.name.equals(name) && entry2.value.equals(descriptor)) {
                    return entry2.index;
                }
                entry = entry2.next;
            } else {
                this.constantPool.put122(12, addConstantUtf8(name), addConstantUtf8(descriptor));
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, 12, name, descriptor, hashCode)).index;
            }
        }
    }

    private void addConstantNameAndType(final int index, final String name, final String descriptor) {
        add(new Entry(index, 12, name, descriptor, hash(12, name, descriptor)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addConstantUtf8(final String value) {
        int hashCode = hash(1, value);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 1 && entry2.hashCode == hashCode && entry2.value.equals(value)) {
                    return entry2.index;
                }
                entry = entry2.next;
            } else {
                this.constantPool.putByte(1).putUTF8(value);
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, 1, value, hashCode)).index;
            }
        }
    }

    private void addConstantUtf8(final int index, final String value) {
        add(new Entry(index, 1, value, hash(1, value)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantMethodHandle(final int referenceKind, final String owner, final String name, final String descriptor, final boolean isInterface) {
        int hashCode = hash(15, owner, name, descriptor, referenceKind);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 15 && entry2.hashCode == hashCode && entry2.data == referenceKind && entry2.owner.equals(owner) && entry2.name.equals(name) && entry2.value.equals(descriptor)) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                if (referenceKind <= 4) {
                    this.constantPool.put112(15, referenceKind, addConstantFieldref(owner, name, descriptor).index);
                } else {
                    this.constantPool.put112(15, referenceKind, addConstantMethodref(owner, name, descriptor, isInterface).index);
                }
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, 15, owner, name, descriptor, referenceKind, hashCode));
            }
        }
    }

    private void addConstantMethodHandle(final int index, final int referenceKind, final String owner, final String name, final String descriptor) {
        int hashCode = hash(15, owner, name, descriptor, referenceKind);
        add(new Entry(index, 15, owner, name, descriptor, referenceKind, hashCode));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantMethodType(final String methodDescriptor) {
        return addConstantUtf8Reference(16, methodDescriptor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantDynamic(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        Symbol bootstrapMethod = addBootstrapMethod(bootstrapMethodHandle, bootstrapMethodArguments);
        return addConstantDynamicOrInvokeDynamicReference(17, name, descriptor, bootstrapMethod.index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantInvokeDynamic(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        Symbol bootstrapMethod = addBootstrapMethod(bootstrapMethodHandle, bootstrapMethodArguments);
        return addConstantDynamicOrInvokeDynamicReference(18, name, descriptor, bootstrapMethod.index);
    }

    private Symbol addConstantDynamicOrInvokeDynamicReference(final int tag, final String name, final String descriptor, final int bootstrapMethodIndex) {
        int hashCode = hash(tag, name, descriptor, bootstrapMethodIndex);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == tag && entry2.hashCode == hashCode && entry2.data == bootstrapMethodIndex && entry2.name.equals(name) && entry2.value.equals(descriptor)) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                this.constantPool.put122(tag, bootstrapMethodIndex, addConstantNameAndType(name, descriptor));
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, tag, null, name, descriptor, bootstrapMethodIndex, hashCode));
            }
        }
    }

    private void addConstantDynamicOrInvokeDynamicReference(final int tag, final int index, final String name, final String descriptor, final int bootstrapMethodIndex) {
        int hashCode = hash(tag, name, descriptor, bootstrapMethodIndex);
        add(new Entry(index, tag, null, name, descriptor, bootstrapMethodIndex, hashCode));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantModule(final String moduleName) {
        return addConstantUtf8Reference(19, moduleName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol addConstantPackage(final String packageName) {
        return addConstantUtf8Reference(20, packageName);
    }

    private Symbol addConstantUtf8Reference(final int tag, final String value) {
        int hashCode = hash(tag, value);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == tag && entry2.hashCode == hashCode && entry2.value.equals(value)) {
                    return entry2;
                }
                entry = entry2.next;
            } else {
                this.constantPool.put12(tag, addConstantUtf8(value));
                int i = this.constantPoolCount;
                this.constantPoolCount = i + 1;
                return put(new Entry(i, tag, value, hashCode));
            }
        }
    }

    private void addConstantUtf8Reference(final int index, final int tag, final String value) {
        add(new Entry(index, tag, value, hash(tag, value)));
    }

    Symbol addBootstrapMethod(final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        ByteVector bootstrapMethodsAttribute = this.bootstrapMethods;
        if (bootstrapMethodsAttribute == null) {
            ByteVector byteVector = new ByteVector();
            this.bootstrapMethods = byteVector;
            bootstrapMethodsAttribute = byteVector;
        }
        int numBootstrapArguments = bootstrapMethodArguments.length;
        int[] bootstrapMethodArgumentIndexes = new int[numBootstrapArguments];
        for (int i = 0; i < numBootstrapArguments; i++) {
            bootstrapMethodArgumentIndexes[i] = addConstant(bootstrapMethodArguments[i]).index;
        }
        int bootstrapMethodOffset = bootstrapMethodsAttribute.length;
        bootstrapMethodsAttribute.putShort(addConstantMethodHandle(bootstrapMethodHandle.getTag(), bootstrapMethodHandle.getOwner(), bootstrapMethodHandle.getName(), bootstrapMethodHandle.getDesc(), bootstrapMethodHandle.isInterface()).index);
        bootstrapMethodsAttribute.putShort(numBootstrapArguments);
        for (int i2 = 0; i2 < numBootstrapArguments; i2++) {
            bootstrapMethodsAttribute.putShort(bootstrapMethodArgumentIndexes[i2]);
        }
        int bootstrapMethodlength = bootstrapMethodsAttribute.length - bootstrapMethodOffset;
        int hashCode = bootstrapMethodHandle.hashCode();
        for (Object bootstrapMethodArgument : bootstrapMethodArguments) {
            hashCode ^= bootstrapMethodArgument.hashCode();
        }
        return addBootstrapMethod(bootstrapMethodOffset, bootstrapMethodlength, hashCode & Integer.MAX_VALUE);
    }

    private Symbol addBootstrapMethod(final int offset, final int length, final int hashCode) {
        byte[] bootstrapMethodsData = this.bootstrapMethods.data;
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 64 && entry2.hashCode == hashCode) {
                    int otherOffset = (int) entry2.data;
                    boolean isSameBootstrapMethod = true;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        if (bootstrapMethodsData[offset + i] == bootstrapMethodsData[otherOffset + i]) {
                            i++;
                        } else {
                            isSameBootstrapMethod = false;
                            break;
                        }
                    }
                    if (isSameBootstrapMethod) {
                        this.bootstrapMethods.length = offset;
                        return entry2;
                    }
                }
                entry = entry2.next;
            } else {
                int i2 = this.bootstrapMethodCount;
                this.bootstrapMethodCount = i2 + 1;
                return put(new Entry(i2, 64, offset, hashCode));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Symbol getType(final int typeIndex) {
        return this.typeTable[typeIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Label getForwardUninitializedLabel(final int typeIndex) {
        return this.labelTable[(int) this.typeTable[typeIndex].data].label;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addType(final String value) {
        int hashCode = hash(128, value);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 128 && entry2.hashCode == hashCode && entry2.value.equals(value)) {
                    return entry2.index;
                }
                entry = entry2.next;
            } else {
                return addTypeInternal(new Entry(this.typeCount, 128, value, hashCode));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addUninitializedType(final String value, final int bytecodeOffset) {
        int hashCode = hash(Opcodes.LOR, value, bytecodeOffset);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 129 && entry2.hashCode == hashCode && entry2.data == bytecodeOffset && entry2.value.equals(value)) {
                    return entry2.index;
                }
                entry = entry2.next;
            } else {
                return addTypeInternal(new Entry(this.typeCount, Opcodes.LOR, value, bytecodeOffset, hashCode));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addForwardUninitializedType(final String value, final Label label) {
        int labelIndex = getOrAddLabelEntry(label).index;
        int hashCode = hash(130, value, labelIndex);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 130 && entry2.hashCode == hashCode && entry2.data == labelIndex && entry2.value.equals(value)) {
                    return entry2.index;
                }
                entry = entry2.next;
            } else {
                return addTypeInternal(new Entry(this.typeCount, 130, value, labelIndex, hashCode));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addMergedType(final int typeTableIndex1, final int typeTableIndex2) {
        long j;
        if (typeTableIndex1 < typeTableIndex2) {
            j = typeTableIndex1 | (typeTableIndex2 << 32);
        } else {
            j = typeTableIndex2 | (typeTableIndex1 << 32);
        }
        long data = j;
        int hashCode = hash(Opcodes.LXOR, typeTableIndex1 + typeTableIndex2);
        Entry entry = get(hashCode);
        while (true) {
            Entry entry2 = entry;
            if (entry2 != null) {
                if (entry2.tag == 131 && entry2.hashCode == hashCode && entry2.data == data) {
                    return entry2.info;
                }
                entry = entry2.next;
            } else {
                String type1 = this.typeTable[typeTableIndex1].value;
                String type2 = this.typeTable[typeTableIndex2].value;
                int commonSuperTypeIndex = addType(this.classWriter.getCommonSuperClass(type1, type2));
                put(new Entry(this.typeCount, Opcodes.LXOR, data, hashCode)).info = commonSuperTypeIndex;
                return commonSuperTypeIndex;
            }
        }
    }

    private int addTypeInternal(final Entry entry) {
        if (this.typeTable == null) {
            this.typeTable = new Entry[16];
        }
        if (this.typeCount == this.typeTable.length) {
            Entry[] newTypeTable = new Entry[2 * this.typeTable.length];
            System.arraycopy(this.typeTable, 0, newTypeTable, 0, this.typeTable.length);
            this.typeTable = newTypeTable;
        }
        Entry[] entryArr = this.typeTable;
        int i = this.typeCount;
        this.typeCount = i + 1;
        entryArr[i] = entry;
        return put(entry).index;
    }

    private LabelEntry getOrAddLabelEntry(final Label label) {
        LabelEntry labelEntry;
        if (this.labelEntries == null) {
            this.labelEntries = new LabelEntry[16];
            this.labelTable = new LabelEntry[16];
        }
        int hashCode = System.identityHashCode(label);
        LabelEntry labelEntry2 = this.labelEntries[hashCode % this.labelEntries.length];
        while (true) {
            labelEntry = labelEntry2;
            if (labelEntry == null || labelEntry.label == label) {
                break;
            }
            labelEntry2 = labelEntry.next;
        }
        if (labelEntry != null) {
            return labelEntry;
        }
        if (this.labelCount > (this.labelEntries.length * 3) / 4) {
            int currentCapacity = this.labelEntries.length;
            int newCapacity = (currentCapacity * 2) + 1;
            LabelEntry[] newLabelEntries = new LabelEntry[newCapacity];
            for (int i = currentCapacity - 1; i >= 0; i--) {
                LabelEntry labelEntry3 = this.labelEntries[i];
                while (true) {
                    LabelEntry currentEntry = labelEntry3;
                    if (currentEntry != null) {
                        int newCurrentEntryIndex = System.identityHashCode(currentEntry.label) % newCapacity;
                        LabelEntry nextEntry = currentEntry.next;
                        currentEntry.next = newLabelEntries[newCurrentEntryIndex];
                        newLabelEntries[newCurrentEntryIndex] = currentEntry;
                        labelEntry3 = nextEntry;
                    }
                }
            }
            this.labelEntries = newLabelEntries;
        }
        if (this.labelCount == this.labelTable.length) {
            LabelEntry[] newLabelTable = new LabelEntry[2 * this.labelTable.length];
            System.arraycopy(this.labelTable, 0, newLabelTable, 0, this.labelTable.length);
            this.labelTable = newLabelTable;
        }
        LabelEntry labelEntry4 = new LabelEntry(this.labelCount, label);
        int index = hashCode % this.labelEntries.length;
        labelEntry4.next = this.labelEntries[index];
        this.labelEntries[index] = labelEntry4;
        LabelEntry[] labelEntryArr = this.labelTable;
        int i2 = this.labelCount;
        this.labelCount = i2 + 1;
        labelEntryArr[i2] = labelEntry4;
        return labelEntry4;
    }

    private static int hash(final int tag, final int value) {
        return Integer.MAX_VALUE & (tag + value);
    }

    private static int hash(final int tag, final long value) {
        return Integer.MAX_VALUE & (tag + ((int) value) + ((int) (value >>> 32)));
    }

    private static int hash(final int tag, final String value) {
        return Integer.MAX_VALUE & (tag + value.hashCode());
    }

    private static int hash(final int tag, final String value1, final int value2) {
        return Integer.MAX_VALUE & (tag + value1.hashCode() + value2);
    }

    private static int hash(final int tag, final String value1, final String value2) {
        return Integer.MAX_VALUE & (tag + (value1.hashCode() * value2.hashCode()));
    }

    private static int hash(final int tag, final String value1, final String value2, final int value3) {
        return Integer.MAX_VALUE & (tag + (value1.hashCode() * value2.hashCode() * (value3 + 1)));
    }

    private static int hash(final int tag, final String value1, final String value2, final String value3) {
        return Integer.MAX_VALUE & (tag + (value1.hashCode() * value2.hashCode() * value3.hashCode()));
    }

    private static int hash(final int tag, final String value1, final String value2, final String value3, final int value4) {
        return Integer.MAX_VALUE & (tag + (value1.hashCode() * value2.hashCode() * value3.hashCode() * value4));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/SymbolTable$Entry.class */
    public static final class Entry extends Symbol {
        final int hashCode;
        Entry next;

        Entry(final int index, final int tag, final String owner, final String name, final String value, final long data, final int hashCode) {
            super(index, tag, owner, name, value, data);
            this.hashCode = hashCode;
        }

        Entry(final int index, final int tag, final String value, final int hashCode) {
            super(index, tag, null, null, value, 0L);
            this.hashCode = hashCode;
        }

        Entry(final int index, final int tag, final String value, final long data, final int hashCode) {
            super(index, tag, null, null, value, data);
            this.hashCode = hashCode;
        }

        Entry(final int index, final int tag, final String name, final String value, final int hashCode) {
            super(index, tag, null, name, value, 0L);
            this.hashCode = hashCode;
        }

        Entry(final int index, final int tag, final long data, final int hashCode) {
            super(index, tag, null, null, null, data);
            this.hashCode = hashCode;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/SymbolTable$LabelEntry.class */
    public static final class LabelEntry {
        final int index;
        final Label label;
        LabelEntry next;

        LabelEntry(final int index, final Label label) {
            this.index = index;
            this.label = label;
        }
    }
}
