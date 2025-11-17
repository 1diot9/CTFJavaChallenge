package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BeanDeserializer.class */
public class BeanDeserializer extends AbstractMapDeserializer {
    private Class _type;
    private HashMap _methodMap;
    private Method _readResolve;
    private Constructor _constructor;
    private Object[] _constructorArgs;

    public BeanDeserializer(Class cl) {
        this._type = cl;
        this._methodMap = getMethodMap(cl);
        this._readResolve = getReadResolve(cl);
        Constructor[] constructors = cl.getConstructors();
        int bestLength = Integer.MAX_VALUE;
        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getParameterTypes().length < bestLength) {
                this._constructor = constructors[i];
                bestLength = this._constructor.getParameterTypes().length;
            }
        }
        if (this._constructor != null) {
            this._constructor.setAccessible(true);
            Class[] params = this._constructor.getParameterTypes();
            this._constructorArgs = new Object[params.length];
            for (int i2 = 0; i2 < params.length; i2++) {
                this._constructorArgs[i2] = getParamArg(params[i2]);
            }
        }
    }

    protected static Object getParamArg(Class cl) {
        if (!cl.isPrimitive()) {
            return null;
        }
        if (Boolean.TYPE.equals(cl)) {
            return Boolean.FALSE;
        }
        if (Byte.TYPE.equals(cl)) {
            return (byte) 0;
        }
        if (Short.TYPE.equals(cl)) {
            return (short) 0;
        }
        if (Character.TYPE.equals(cl)) {
            return (char) 0;
        }
        if (Integer.TYPE.equals(cl)) {
            return 0;
        }
        if (Long.TYPE.equals(cl)) {
            return 0L;
        }
        if (Float.TYPE.equals(cl)) {
            return Double.valueOf(0.0d);
        }
        if (Double.TYPE.equals(cl)) {
            return Double.valueOf(0.0d);
        }
        throw new UnsupportedOperationException();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractMapDeserializer, com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._type;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            Object obj = instantiate();
            return readMap(in, obj);
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOExceptionWrapper(e2);
        }
    }

    public Object readMap(AbstractHessianInput in, Object obj) throws IOException {
        try {
            int ref = in.addRef(obj);
            while (!in.isEnd()) {
                Object key = in.readObject();
                Method method = (Method) this._methodMap.get(key);
                if (method != null) {
                    Object value = in.readObject(method.getParameterTypes()[0]);
                    method.invoke(obj, value);
                } else {
                    in.readObject();
                }
            }
            in.readMapEnd();
            Object resolve = resolve(obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOExceptionWrapper(e2);
        }
    }

    private Object resolve(Object obj) {
        try {
            if (this._readResolve != null) {
                return this._readResolve.invoke(obj, new Object[0]);
            }
        } catch (Exception e) {
        }
        return obj;
    }

    protected Object instantiate() throws Exception {
        return this._constructor.newInstance(this._constructorArgs);
    }

    protected Method getReadResolve(Class cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("readResolve") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    protected HashMap getMethodMap(Class cl) {
        HashMap methodMap = new HashMap();
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        Class[] paramTypes = method.getParameterTypes();
                        if (paramTypes.length == 1 && method.getReturnType().equals(Void.TYPE) && findGetter(methods, name, paramTypes[0]) != null) {
                            try {
                                method.setAccessible(true);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            String name2 = name.substring(3);
                            int j = 0;
                            while (j < name2.length() && Character.isUpperCase(name2.charAt(j))) {
                                j++;
                            }
                            if (j == 1) {
                                name2 = name2.substring(0, j).toLowerCase() + name2.substring(j);
                            } else if (j > 1) {
                                name2 = name2.substring(0, j - 1).toLowerCase() + name2.substring(j - 1);
                            }
                            methodMap.put(name2, method);
                        }
                    }
                }
            }
            cl = cl.getSuperclass();
        }
        return methodMap;
    }

    private Method findGetter(Method[] methods, String setterName, Class arg) {
        String getterName = BeanUtil.PREFIX_GETTER_GET + setterName.substring(3);
        for (Method method : methods) {
            if (method.getName().equals(getterName) && method.getReturnType().equals(arg)) {
                Class[] params = method.getParameterTypes();
                if (params.length == 0) {
                    return method;
                }
            }
        }
        return null;
    }
}
