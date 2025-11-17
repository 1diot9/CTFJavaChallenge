package cn.hutool.core.convert.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/ArrayConverter.class */
public class ArrayConverter extends AbstractConverter<Object> {
    private static final long serialVersionUID = 1;
    private final Class<?> targetType;
    private final Class<?> targetComponentType;
    private boolean ignoreElementError;

    public ArrayConverter(Class<?> targetType) {
        this(targetType, false);
    }

    public ArrayConverter(Class<?> targetType, boolean ignoreElementError) {
        targetType = null == targetType ? Object[].class : targetType;
        if (targetType.isArray()) {
            this.targetType = targetType;
            this.targetComponentType = targetType.getComponentType();
        } else {
            this.targetComponentType = targetType;
            this.targetType = ArrayUtil.getArrayType(targetType);
        }
        this.ignoreElementError = ignoreElementError;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    protected Object convertInternal2(Object value) {
        return value.getClass().isArray() ? convertArrayToArray(value) : convertObjectToArray(value);
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Object> getTargetType() {
        return this.targetType;
    }

    public void setIgnoreElementError(boolean ignoreElementError) {
        this.ignoreElementError = ignoreElementError;
    }

    private Object convertArrayToArray(Object array) {
        Class<?> valueComponentType = ArrayUtil.getComponentType(array);
        if (valueComponentType == this.targetComponentType) {
            return array;
        }
        int len = ArrayUtil.length(array);
        Object result = Array.newInstance(this.targetComponentType, len);
        for (int i = 0; i < len; i++) {
            Array.set(result, i, convertComponentType(Array.get(array, i)));
        }
        return result;
    }

    private Object convertObjectToArray(Object value) {
        Object result;
        if (value instanceof CharSequence) {
            if (this.targetComponentType == Character.TYPE || this.targetComponentType == Character.class) {
                return convertArrayToArray(value.toString().toCharArray());
            }
            if (this.targetComponentType == Byte.TYPE) {
                String str = value.toString();
                if (Base64.isBase64(str)) {
                    return Base64.decode(value.toString());
                }
                return str.getBytes();
            }
            String[] strings = StrUtil.splitToArray((CharSequence) value.toString(), ',');
            return convertArrayToArray(strings);
        }
        if (value instanceof List) {
            List<?> list = (List) value;
            result = Array.newInstance(this.targetComponentType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(result, i, convertComponentType(list.get(i)));
            }
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection) value;
            result = Array.newInstance(this.targetComponentType, collection.size());
            int i2 = 0;
            for (Object element : collection) {
                Array.set(result, i2, convertComponentType(element));
                i2++;
            }
        } else if (value instanceof Iterable) {
            List<?> list2 = IterUtil.toList((Iterable) value);
            result = Array.newInstance(this.targetComponentType, list2.size());
            for (int i3 = 0; i3 < list2.size(); i3++) {
                Array.set(result, i3, convertComponentType(list2.get(i3)));
            }
        } else if (value instanceof Iterator) {
            List<?> list3 = IterUtil.toList((Iterator) value);
            result = Array.newInstance(this.targetComponentType, list3.size());
            for (int i4 = 0; i4 < list3.size(); i4++) {
                Array.set(result, i4, convertComponentType(list3.get(i4)));
            }
        } else if ((value instanceof Number) && Byte.TYPE == this.targetComponentType) {
            result = ByteUtil.numberToBytes((Number) value);
        } else if ((value instanceof Serializable) && Byte.TYPE == this.targetComponentType) {
            result = ObjectUtil.serialize(value);
        } else {
            result = convertToSingleElementArray(value);
        }
        return result;
    }

    private Object[] convertToSingleElementArray(Object value) {
        Object[] singleElementArray = ArrayUtil.newArray(this.targetComponentType, 1);
        singleElementArray[0] = convertComponentType(value);
        return singleElementArray;
    }

    private Object convertComponentType(Object value) {
        return Convert.convertWithCheck(this.targetComponentType, value, null, this.ignoreElementError);
    }
}
