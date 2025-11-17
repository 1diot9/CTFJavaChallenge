package jakarta.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Objects;
import org.springframework.cglib.core.Constants;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/StaticFieldELResolver.class */
public class StaticFieldELResolver extends ELResolver {
    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Util.canAccess(null, field)) {
                    return field.get(null);
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            throw new PropertyNotWritableException(Util.message(context, "staticFieldELResolver.notWritable", name, clazz.getName()));
        }
    }

    @Override // jakarta.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (method instanceof String)) {
            context.setPropertyResolved(base, method);
            Class<?> clazz = ((ELClass) base).getKlass();
            String methodName = (String) method;
            if (Constants.CONSTRUCTOR_NAME.equals(methodName)) {
                Constructor<?> match = Util.findConstructor(context, clazz, paramTypes, params);
                Object[] parameters = Util.buildParameters(context, match.getParameterTypes(), match.isVarArgs(), params);
                try {
                    Object result = match.newInstance(parameters);
                    return result;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    Util.handleThrowable(cause);
                    throw new ELException(cause);
                } catch (ReflectiveOperationException e2) {
                    throw new ELException(e2);
                }
            }
            Method match2 = Util.findMethod(context, clazz, null, methodName, paramTypes, params);
            if (match2 == null) {
                throw new MethodNotFoundException(Util.message(context, "staticFieldELResolver.methodNotFound", methodName, clazz.getName()));
            }
            Object[] parameters2 = Util.buildParameters(context, match2.getParameterTypes(), match2.isVarArgs(), params);
            try {
                Object result2 = match2.invoke(null, parameters2);
                return result2;
            } catch (IllegalAccessException | IllegalArgumentException e3) {
                throw new ELException(e3);
            } catch (InvocationTargetException e4) {
                Throwable cause2 = e4.getCause();
                Util.handleThrowable(cause2);
                throw new ELException(cause2);
            }
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    if (Util.canAccess(null, field)) {
                        return null;
                    }
                }
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return true;
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
