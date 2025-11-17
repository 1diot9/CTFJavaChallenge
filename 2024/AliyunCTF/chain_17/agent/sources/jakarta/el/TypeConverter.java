package jakarta.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/TypeConverter.class */
public abstract class TypeConverter extends ELResolver {
    @Override // jakarta.el.ELResolver
    public abstract <T> T convertToType(ELContext eLContext, Object obj, Class<T> cls);

    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }
}
