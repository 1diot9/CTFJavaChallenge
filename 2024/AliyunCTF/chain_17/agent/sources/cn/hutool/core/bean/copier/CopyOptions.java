package cn.hutool.core.bean.copier;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.TypeConverter;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/copier/CopyOptions.class */
public class CopyOptions implements Serializable {
    private static final long serialVersionUID = 1;
    protected Class<?> editable;
    protected boolean ignoreNullValue;
    private BiPredicate<Field, Object> propertiesFilter;
    protected boolean ignoreError;
    protected boolean ignoreCase;
    private Editor<String> fieldNameEditor;
    protected BiFunction<String, Object, Object> fieldValueEditor;
    protected boolean transientSupport;
    protected boolean override;
    private Set<String> ignoreKeySet;
    protected TypeConverter converter;

    public static CopyOptions create() {
        return new CopyOptions();
    }

    public static CopyOptions create(Class<?> editable, boolean ignoreNullValue, String... ignoreProperties) {
        return new CopyOptions(editable, ignoreNullValue, ignoreProperties);
    }

    public CopyOptions() {
        this.transientSupport = true;
        this.override = true;
        this.converter = (type, value) -> {
            if (null == value) {
                return null;
            }
            String name = value.getClass().getName();
            if (ArrayUtil.contains(new String[]{"cn.hutool.json.JSONObject", "cn.hutool.json.JSONArray"}, name)) {
                return ReflectUtil.invoke(value, "toBean", ObjectUtil.defaultIfNull((Class) type, Object.class));
            }
            return Convert.convertWithCheck(type, value, null, this.ignoreError);
        };
    }

    public CopyOptions(Class<?> editable, boolean ignoreNullValue, String... ignoreProperties) {
        this.transientSupport = true;
        this.override = true;
        this.converter = (type, value) -> {
            if (null == value) {
                return null;
            }
            String name = value.getClass().getName();
            if (ArrayUtil.contains(new String[]{"cn.hutool.json.JSONObject", "cn.hutool.json.JSONArray"}, name)) {
                return ReflectUtil.invoke(value, "toBean", ObjectUtil.defaultIfNull((Class) type, Object.class));
            }
            return Convert.convertWithCheck(type, value, null, this.ignoreError);
        };
        this.propertiesFilter = (f, v) -> {
            return true;
        };
        this.editable = editable;
        this.ignoreNullValue = ignoreNullValue;
        setIgnoreProperties(ignoreProperties);
    }

    public CopyOptions setEditable(Class<?> editable) {
        this.editable = editable;
        return this;
    }

    public CopyOptions setIgnoreNullValue(boolean ignoreNullVall) {
        this.ignoreNullValue = ignoreNullVall;
        return this;
    }

    public CopyOptions ignoreNullValue() {
        return setIgnoreNullValue(true);
    }

    public CopyOptions setPropertiesFilter(BiPredicate<Field, Object> propertiesFilter) {
        this.propertiesFilter = propertiesFilter;
        return this;
    }

    public CopyOptions setIgnoreProperties(String... ignoreProperties) {
        this.ignoreKeySet = CollUtil.newHashSet(ignoreProperties);
        return this;
    }

    public <P, R> CopyOptions setIgnoreProperties(Func1<P, R>... funcs) {
        this.ignoreKeySet = ArrayUtil.mapToSet(funcs, LambdaUtil::getFieldName);
        return this;
    }

    public CopyOptions setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
        return this;
    }

    public CopyOptions ignoreError() {
        return setIgnoreError(true);
    }

    public CopyOptions setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

    public CopyOptions ignoreCase() {
        return setIgnoreCase(true);
    }

    public CopyOptions setFieldMapping(Map<String, String> fieldMapping) {
        return setFieldNameEditor(key -> {
            return (String) fieldMapping.getOrDefault(key, key);
        });
    }

    public CopyOptions setFieldNameEditor(Editor<String> fieldNameEditor) {
        this.fieldNameEditor = fieldNameEditor;
        return this;
    }

    public CopyOptions setFieldValueEditor(BiFunction<String, Object, Object> fieldValueEditor) {
        this.fieldValueEditor = fieldValueEditor;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object editFieldValue(String fieldName, Object fieldValue) {
        return null != this.fieldValueEditor ? this.fieldValueEditor.apply(fieldName, fieldValue) : fieldValue;
    }

    public CopyOptions setTransientSupport(boolean transientSupport) {
        this.transientSupport = transientSupport;
        return this;
    }

    public CopyOptions setOverride(boolean override) {
        this.override = override;
        return this;
    }

    public CopyOptions setConverter(TypeConverter converter) {
        this.converter = converter;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object convertField(Type targetType, Object fieldValue) {
        return null != this.converter ? this.converter.convert(targetType, fieldValue) : fieldValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String editFieldName(String fieldName) {
        return null != this.fieldNameEditor ? this.fieldNameEditor.edit(fieldName) : fieldName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean testPropertyFilter(Field field, Object value) {
        return null == this.propertiesFilter || this.propertiesFilter.test(field, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean testKeyFilter(Object key) {
        return CollUtil.isEmpty((Collection<?>) this.ignoreKeySet) || false == this.ignoreKeySet.contains(key);
    }
}
