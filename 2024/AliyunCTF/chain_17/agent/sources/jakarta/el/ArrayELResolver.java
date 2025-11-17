package jakarta.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ArrayELResolver.class */
public class ArrayELResolver extends ELResolver {
    private final boolean readOnly;

    public ArrayELResolver() {
        this.readOnly = false;
    }

    public ArrayELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            try {
                int idx = coerce(property);
                checkBounds(base, idx);
            } catch (IllegalArgumentException e) {
            }
            if (this.readOnly) {
                return null;
            }
            return base.getClass().getComponentType();
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            int idx = coerce(property);
            if (idx < 0 || idx >= Array.getLength(base)) {
                return null;
            }
            return Array.get(base, idx);
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            if (this.readOnly) {
                throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
            }
            int idx = coerce(property);
            checkBounds(base, idx);
            if (value != null && !Util.isAssignableFrom(value.getClass(), base.getClass().getComponentType())) {
                throw new ClassCastException(Util.message(context, "objectNotAssignable", value.getClass().getName(), base.getClass().getComponentType().getName()));
            }
            Array.set(base, idx, value);
        }
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            try {
                int idx = coerce(property);
                checkBounds(base, idx);
            } catch (IllegalArgumentException e) {
            }
        }
        return this.readOnly;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base != null && base.getClass().isArray()) {
            return Integer.class;
        }
        return null;
    }

    private static void checkBounds(Object base, int idx) {
        if (idx < 0 || idx >= Array.getLength(base)) {
            throw new PropertyNotFoundException(new ArrayIndexOutOfBoundsException(idx).getMessage());
        }
    }

    private static int coerce(Object property) {
        if (property instanceof Number) {
            return ((Number) property).intValue();
        }
        if (property instanceof Character) {
            return ((Character) property).charValue();
        }
        if (property instanceof Boolean) {
            return ((Boolean) property).booleanValue() ? 1 : 0;
        }
        if (property instanceof String) {
            return Integer.parseInt((String) property);
        }
        throw new IllegalArgumentException(property != null ? property.toString() : "null");
    }
}
