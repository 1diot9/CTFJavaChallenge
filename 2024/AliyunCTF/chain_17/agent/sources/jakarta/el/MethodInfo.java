package jakarta.el;

import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/MethodInfo.class */
public class MethodInfo {
    private final String name;
    private final Class<?>[] paramTypes;
    private final Class<?> returnType;

    public MethodInfo(String name, Class<?> returnType, Class<?>[] paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getReturnType() {
        return this.returnType;
    }

    public Class<?>[] getParamTypes() {
        return this.paramTypes;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.name == null ? 0 : this.name.hashCode());
        return (31 * ((31 * result) + Arrays.hashCode(this.paramTypes))) + (this.returnType == null ? 0 : this.returnType.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MethodInfo other = (MethodInfo) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (!Arrays.equals(this.paramTypes, other.paramTypes)) {
            return false;
        }
        if (this.returnType == null) {
            if (other.returnType != null) {
                return false;
            }
            return true;
        }
        if (!this.returnType.equals(other.returnType)) {
            return false;
        }
        return true;
    }
}
