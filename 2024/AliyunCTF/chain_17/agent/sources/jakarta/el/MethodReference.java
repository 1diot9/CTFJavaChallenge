package jakarta.el;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/MethodReference.class */
public class MethodReference {
    private final Object base;
    private final MethodInfo methodInfo;
    private final Annotation[] annotations;
    private final Object[] evaluatedParameters;

    public MethodReference(Object base, MethodInfo methodInfo, Annotation[] annotations, Object[] evaluatedParameters) {
        this.base = base;
        this.methodInfo = methodInfo;
        this.annotations = annotations;
        this.evaluatedParameters = evaluatedParameters;
    }

    public Object getBase() {
        return this.base;
    }

    public MethodInfo getMethodInfo() {
        return this.methodInfo;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public Object[] getEvaluatedParameters() {
        return this.evaluatedParameters;
    }

    public int hashCode() {
        int result = (31 * 1) + Arrays.hashCode(this.annotations);
        return (31 * ((31 * ((31 * result) + (this.base == null ? 0 : this.base.hashCode()))) + Arrays.deepHashCode(this.evaluatedParameters))) + (this.methodInfo == null ? 0 : this.methodInfo.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MethodReference other = (MethodReference) obj;
        if (!Arrays.equals(this.annotations, other.annotations)) {
            return false;
        }
        if (this.base == null) {
            if (other.base != null) {
                return false;
            }
        } else if (!this.base.equals(other.base)) {
            return false;
        }
        if (!Arrays.deepEquals(this.evaluatedParameters, other.evaluatedParameters)) {
            return false;
        }
        if (this.methodInfo == null) {
            if (other.methodInfo != null) {
                return false;
            }
            return true;
        }
        if (!this.methodInfo.equals(other.methodInfo)) {
            return false;
        }
        return true;
    }
}
