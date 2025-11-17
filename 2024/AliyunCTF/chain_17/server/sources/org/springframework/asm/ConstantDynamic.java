package org.springframework.asm;

import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/ConstantDynamic.class */
public final class ConstantDynamic {
    private final String name;
    private final String descriptor;
    private final Handle bootstrapMethod;
    private final Object[] bootstrapMethodArguments;

    public ConstantDynamic(final String name, final String descriptor, final Handle bootstrapMethod, final Object... bootstrapMethodArguments) {
        this.name = name;
        this.descriptor = descriptor;
        this.bootstrapMethod = bootstrapMethod;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    public Handle getBootstrapMethod() {
        return this.bootstrapMethod;
    }

    public int getBootstrapMethodArgumentCount() {
        return this.bootstrapMethodArguments.length;
    }

    public Object getBootstrapMethodArgument(final int index) {
        return this.bootstrapMethodArguments[index];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object[] getBootstrapMethodArgumentsUnsafe() {
        return this.bootstrapMethodArguments;
    }

    public int getSize() {
        char firstCharOfDescriptor = this.descriptor.charAt(0);
        return (firstCharOfDescriptor == 'J' || firstCharOfDescriptor == 'D') ? 2 : 1;
    }

    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ConstantDynamic)) {
            return false;
        }
        ConstantDynamic constantDynamic = (ConstantDynamic) object;
        return this.name.equals(constantDynamic.name) && this.descriptor.equals(constantDynamic.descriptor) && this.bootstrapMethod.equals(constantDynamic.bootstrapMethod) && Arrays.equals(this.bootstrapMethodArguments, constantDynamic.bootstrapMethodArguments);
    }

    public int hashCode() {
        return ((this.name.hashCode() ^ Integer.rotateLeft(this.descriptor.hashCode(), 8)) ^ Integer.rotateLeft(this.bootstrapMethod.hashCode(), 16)) ^ Integer.rotateLeft(Arrays.hashCode(this.bootstrapMethodArguments), 24);
    }

    public String toString() {
        return this.name + " : " + this.descriptor + " " + this.bootstrapMethod + " " + Arrays.toString(this.bootstrapMethodArguments);
    }
}
