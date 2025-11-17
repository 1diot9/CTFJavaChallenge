package cn.hutool.core.convert.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/MapConverter.class */
public class MapConverter extends AbstractConverter<Map<?, ?>> {
    private static final long serialVersionUID = 1;
    private final Type mapType;
    private final Type keyType;
    private final Type valueType;

    public MapConverter(Type mapType) {
        this(mapType, TypeUtil.getTypeArgument(mapType, 0), TypeUtil.getTypeArgument(mapType, 1));
    }

    public MapConverter(Type mapType, Type keyType, Type valueType) {
        this.mapType = mapType;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    public Map<?, ?> convertInternal2(Object value) {
        Map map;
        Type[] typeArguments;
        if (value instanceof Map) {
            Class<?> valueClass = value.getClass();
            if (valueClass.equals(this.mapType) && null != (typeArguments = TypeUtil.getTypeArguments(valueClass)) && 2 == typeArguments.length && Objects.equals(this.keyType, typeArguments[0]) && Objects.equals(this.valueType, typeArguments[1])) {
                return (Map) value;
            }
            map = MapUtil.createMap(TypeUtil.getClass(this.mapType));
            convertMapToMap((Map) value, map);
        } else if (BeanUtil.isBean(value.getClass())) {
            Map map2 = BeanUtil.beanToMap(value, new String[0]);
            map = convertInternal2((Object) map2);
        } else {
            throw new UnsupportedOperationException(StrUtil.format("Unsupport toMap value type: {}", value.getClass().getName()));
        }
        return map;
    }

    private void convertMapToMap(Map<?, ?> srcMap, Map<Object, Object> targetMap) {
        ConverterRegistry convert = ConverterRegistry.getInstance();
        srcMap.forEach((key, value) -> {
            targetMap.put(TypeUtil.isUnknown(this.keyType) ? key : convert.convert(this.keyType, key), TypeUtil.isUnknown(this.valueType) ? value : convert.convert(this.valueType, value));
        });
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Map<?, ?>> getTargetType() {
        return TypeUtil.getClass(this.mapType);
    }
}
