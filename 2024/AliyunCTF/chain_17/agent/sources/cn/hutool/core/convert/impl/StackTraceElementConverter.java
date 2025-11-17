package cn.hutool.core.convert.impl;

import ch.qos.logback.classic.encoder.JsonEncoder;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/StackTraceElementConverter.class */
public class StackTraceElementConverter extends AbstractConverter<StackTraceElement> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public StackTraceElement convertInternal(Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            String declaringClass = MapUtil.getStr(map, JsonEncoder.CLASS_NAME_ATTR_NAME);
            String methodName = MapUtil.getStr(map, JsonEncoder.METHOD_NAME_ATTR_NAME);
            String fileName = MapUtil.getStr(map, "fileName");
            Integer lineNumber = MapUtil.getInt(map, "lineNumber");
            return new StackTraceElement(declaringClass, methodName, fileName, ((Integer) ObjectUtil.defaultIfNull((int) lineNumber, 0)).intValue());
        }
        return null;
    }
}
