package org.springframework.boot.info;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/JavaInfo.class */
public class JavaInfo {
    private final String version = System.getProperty("java.version");
    private final JavaVendorInfo vendor = new JavaVendorInfo();
    private final JavaRuntimeEnvironmentInfo runtime = new JavaRuntimeEnvironmentInfo();
    private final JavaVirtualMachineInfo jvm = new JavaVirtualMachineInfo();

    public String getVersion() {
        return this.version;
    }

    public JavaVendorInfo getVendor() {
        return this.vendor;
    }

    public JavaRuntimeEnvironmentInfo getRuntime() {
        return this.runtime;
    }

    public JavaVirtualMachineInfo getJvm() {
        return this.jvm;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/JavaInfo$JavaVendorInfo.class */
    public static class JavaVendorInfo {
        private final String name = System.getProperty("java.vendor");
        private final String version = System.getProperty("java.vendor.version");

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/JavaInfo$JavaRuntimeEnvironmentInfo.class */
    public static class JavaRuntimeEnvironmentInfo {
        private final String name = System.getProperty("java.runtime.name");
        private final String version = System.getProperty("java.runtime.version");

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/JavaInfo$JavaVirtualMachineInfo.class */
    public static class JavaVirtualMachineInfo {
        private final String name = System.getProperty("java.vm.name");
        private final String vendor = System.getProperty("java.vm.vendor");
        private final String version = System.getProperty("java.vm.version");

        public String getName() {
            return this.name;
        }

        public String getVendor() {
            return this.vendor;
        }

        public String getVersion() {
            return this.version;
        }
    }
}
