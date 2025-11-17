package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianSerializerInput.class */
public class HessianSerializerInput extends HessianInput {
    public HessianSerializerInput(InputStream is) {
        super(is);
    }

    public HessianSerializerInput() {
    }

    protected Object readObjectImpl(Class cl) throws IOException {
        try {
            Object obj = cl.newInstance();
            if (this._refs == null) {
                this._refs = new ArrayList();
            }
            this._refs.add(obj);
            HashMap fieldMap = getFieldMap(cl);
            int code = read();
            while (code >= 0 && code != 122) {
                this._peek = code;
                Object key = readObject();
                Field field = (Field) fieldMap.get(key);
                if (field != null) {
                    Object value = readObject(field.getType());
                    field.set(obj, value);
                } else {
                    readObject();
                }
                code = read();
            }
            if (code != 122) {
                throw expect(BeanDefinitionParserDelegate.MAP_ELEMENT, code);
            }
            try {
                Method method = cl.getMethod("readResolve", new Class[0]);
                return method.invoke(obj, new Object[0]);
            } catch (Exception e) {
                return obj;
            }
        } catch (IOException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new IOExceptionWrapper(e3);
        }
    }

    protected HashMap getFieldMap(Class cl) {
        HashMap fieldMap = new HashMap();
        while (cl != null) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fieldMap.put(field.getName(), field);
                }
            }
            cl = cl.getSuperclass();
        }
        return fieldMap;
    }
}
