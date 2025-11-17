package cn.hutool.system;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/JvmSpecInfo.class */
public class JvmSpecInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private final String JAVA_VM_SPECIFICATION_NAME = SystemUtil.get("java.vm.specification.name", false);
    private final String JAVA_VM_SPECIFICATION_VERSION = SystemUtil.get("java.vm.specification.version", false);
    private final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtil.get("java.vm.specification.vendor", false);

    public final String getName() {
        return this.JAVA_VM_SPECIFICATION_NAME;
    }

    public final String getVersion() {
        return this.JAVA_VM_SPECIFICATION_VERSION;
    }

    public final String getVendor() {
        return this.JAVA_VM_SPECIFICATION_VENDOR;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        SystemUtil.append(builder, "JavaVM Spec. Name:    ", getName());
        SystemUtil.append(builder, "JavaVM Spec. Version: ", getVersion());
        SystemUtil.append(builder, "JavaVM Spec. Vendor:  ", getVendor());
        return builder.toString();
    }
}
