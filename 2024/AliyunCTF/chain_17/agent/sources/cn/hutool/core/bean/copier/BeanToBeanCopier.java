package cn.hutool.core.bean.copier;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/copier/BeanToBeanCopier.class */
public class BeanToBeanCopier<S, T> extends AbsCopier<S, T> {
    private final Type targetType;

    public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
        super(source, target, copyOptions);
        this.targetType = targetType;
    }

    @Override // cn.hutool.core.lang.copier.Copier
    public T copy() {
        Class<?> actualEditable = this.target.getClass();
        if (null != this.copyOptions.editable) {
            Assert.isTrue(this.copyOptions.editable.isInstance(this.target), "Target class [{}] not assignable to Editable class [{}]", actualEditable.getName(), this.copyOptions.editable.getName());
            actualEditable = this.copyOptions.editable;
        }
        Map<String, PropDesc> targetPropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
        Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(this.source.getClass()).getPropMap(this.copyOptions.ignoreCase);
        sourcePropDescMap.forEach((sFieldName, sDesc) -> {
            String sFieldName;
            PropDesc tDesc;
            if (null == sFieldName || false == sDesc.isReadable(this.copyOptions.transientSupport) || null == (sFieldName = this.copyOptions.editFieldName(sFieldName)) || false == this.copyOptions.testKeyFilter(sFieldName) || null == (tDesc = (PropDesc) targetPropDescMap.get(sFieldName)) || false == tDesc.isWritable(this.copyOptions.transientSupport)) {
                return;
            }
            Object sValue = sDesc.getValue(this.source);
            if (false == this.copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
                return;
            }
            Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
            tDesc.setValue(this.target, this.copyOptions.editFieldValue(sFieldName, this.copyOptions.convertField(fieldType, sValue)), this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
        });
        return this.target;
    }
}
