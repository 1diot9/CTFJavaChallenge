package jakarta.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/BeanNameELResolver.class */
public class BeanNameELResolver extends ELResolver {
    private final BeanNameResolver beanNameResolver;

    public BeanNameELResolver(BeanNameResolver beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String) property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            try {
                Object result = this.beanNameResolver.getBean(beanName);
                context.setPropertyResolved(base, property);
                return result;
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        ELException eLException;
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return;
        }
        String beanName = (String) property;
        boolean isResolved = context.isPropertyResolved();
        try {
            try {
                boolean isReadOnly = isReadOnly(context, base, property);
                context.setPropertyResolved(isResolved);
                if (isReadOnly) {
                    throw new PropertyNotWritableException(Util.message(context, "beanNameELResolver.beanReadOnly", beanName));
                }
                if (this.beanNameResolver.isNameResolved(beanName) || this.beanNameResolver.canCreateBean(beanName)) {
                    try {
                        this.beanNameResolver.setBeanValue(beanName, value);
                        context.setPropertyResolved(base, property);
                    } finally {
                    }
                }
            } finally {
            }
        } catch (Throwable th) {
            context.setPropertyResolved(isResolved);
            throw th;
        }
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String) property;
        try {
            if (this.beanNameResolver.isNameResolved(beanName)) {
                Class<?> result = this.beanNameResolver.getBean(beanName).getClass();
                context.setPropertyResolved(base, property);
                if (this.beanNameResolver.isReadOnly((String) property)) {
                    return null;
                }
                return result;
            }
            return null;
        } catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        }
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return false;
        }
        String beanName = (String) property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            try {
                boolean result = this.beanNameResolver.isReadOnly(beanName);
                context.setPropertyResolved(base, property);
                return result;
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
        return false;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return String.class;
    }
}
