package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BeanSerializer.class */
public class BeanSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(BeanSerializer.class.getName());
    private static final Object[] NULL_ARGS = new Object[0];
    private Method[] _methods;
    private String[] _names;
    private Object _writeReplaceFactory;
    private Method _writeReplace;

    public BeanSerializer(Class cl, ClassLoader loader) {
        introspectWriteReplace(cl, loader);
        ArrayList primitiveMethods = new ArrayList();
        ArrayList compoundMethods = new ArrayList();
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0) {
                    String name = method.getName();
                    if (name.startsWith(BeanUtil.PREFIX_GETTER_GET)) {
                        Class type = method.getReturnType();
                        if (!type.equals(Void.TYPE) && findSetter(methods, name, type) != null) {
                            method.setAccessible(true);
                            if (type.isPrimitive() || (type.getName().startsWith("java.lang.") && !type.equals(Object.class))) {
                                primitiveMethods.add(method);
                            } else {
                                compoundMethods.add(method);
                            }
                        }
                    }
                }
            }
            cl = cl.getSuperclass();
        }
        ArrayList methodList = new ArrayList();
        methodList.addAll(primitiveMethods);
        methodList.addAll(compoundMethods);
        Collections.sort(methodList, new MethodNameCmp());
        this._methods = new Method[methodList.size()];
        methodList.toArray(this._methods);
        this._names = new String[this._methods.length];
        for (int i = 0; i < this._methods.length; i++) {
            String name2 = this._methods[i].getName().substring(3);
            int j = 0;
            while (j < name2.length() && Character.isUpperCase(name2.charAt(j))) {
                j++;
            }
            if (j == 1) {
                name2 = name2.substring(0, j).toLowerCase() + name2.substring(j);
            } else if (j > 1) {
                name2 = name2.substring(0, j - 1).toLowerCase() + name2.substring(j - 1);
            }
            this._names[i] = name2;
        }
    }

    private void introspectWriteReplace(Class cl, ClassLoader loader) {
        try {
            String className = cl.getName() + "HessianSerializer";
            Class serializerClass = Class.forName(className, false, loader);
            Object serializerObject = serializerClass.newInstance();
            Method writeReplace = getWriteReplace(serializerClass, cl);
            if (writeReplace != null) {
                this._writeReplaceFactory = serializerObject;
                this._writeReplace = writeReplace;
                return;
            }
        } catch (ClassNotFoundException e) {
        } catch (Exception e2) {
            log.log(Level.FINER, e2.toString(), (Throwable) e2);
        }
        this._writeReplace = getWriteReplace(cl);
    }

    protected Method getWriteReplace(Class cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    protected Method getWriteReplace(Class cl, Class param) {
        while (cl != null) {
            for (Method method : cl.getDeclaredMethods()) {
                if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 1 && param.equals(method.getParameterTypes()[0])) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Object repl;
        if (out.addRef(obj)) {
            return;
        }
        Class cl = obj.getClass();
        try {
            if (this._writeReplace != null) {
                if (this._writeReplaceFactory != null) {
                    repl = this._writeReplace.invoke(this._writeReplaceFactory, obj);
                } else {
                    repl = this._writeReplace.invoke(obj, new Object[0]);
                }
                out.removeRef(obj);
                out.writeObject(repl);
                out.replaceRef(repl, obj);
                return;
            }
        } catch (Exception e) {
            log.log(Level.FINER, e.toString(), (Throwable) e);
        }
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            for (int i = 0; i < this._methods.length; i++) {
                Method method = this._methods[i];
                Object value = null;
                try {
                    value = this._methods[i].invoke(obj, (Object[]) null);
                } catch (Exception e2) {
                    log.log(Level.FINE, e2.toString(), (Throwable) e2);
                }
                out.writeString(this._names[i]);
                out.writeObject(value);
            }
            out.writeMapEnd();
            return;
        }
        if (ref == -1) {
            out.writeInt(this._names.length);
            for (int i2 = 0; i2 < this._names.length; i2++) {
                out.writeString(this._names[i2]);
            }
            out.writeObjectBegin(cl.getName());
        }
        for (int i3 = 0; i3 < this._methods.length; i3++) {
            Method method2 = this._methods[i3];
            Object value2 = null;
            try {
                value2 = this._methods[i3].invoke(obj, (Object[]) null);
            } catch (Exception e3) {
                log.log(Level.FINER, e3.toString(), (Throwable) e3);
            }
            out.writeObject(value2);
        }
    }

    private Method findSetter(Method[] methods, String getterName, Class arg) {
        String setterName = "set" + getterName.substring(3);
        for (Method method : methods) {
            if (method.getName().equals(setterName) && method.getReturnType().equals(Void.TYPE)) {
                Class[] params = method.getParameterTypes();
                if (params.length == 1 && params[0].equals(arg)) {
                    return method;
                }
            }
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BeanSerializer$MethodNameCmp.class */
    static class MethodNameCmp implements Comparator<Method> {
        MethodNameCmp() {
        }

        @Override // java.util.Comparator
        public int compare(Method a, Method b) {
            return a.getName().compareTo(b.getName());
        }
    }
}
