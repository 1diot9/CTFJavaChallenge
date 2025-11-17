package jakarta.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELResolver.class */
public abstract class ELResolver {
    public static final String TYPE = "type";
    public static final String RESOLVABLE_AT_DESIGN_TIME = "resolvableAtDesignTime";

    public abstract Object getValue(ELContext eLContext, Object obj, Object obj2);

    public abstract Class<?> getType(ELContext eLContext, Object obj, Object obj2);

    public abstract void setValue(ELContext eLContext, Object obj, Object obj2, Object obj3);

    public abstract boolean isReadOnly(ELContext eLContext, Object obj, Object obj2);

    public abstract Class<?> getCommonPropertyType(ELContext eLContext, Object obj);

    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        return null;
    }

    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public <T> T convertToType(ELContext context, Object obj, Class<T> type) {
        context.setPropertyResolved(false);
        return null;
    }
}
