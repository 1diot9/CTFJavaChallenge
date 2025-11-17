package cn.hutool.core.bean.copier.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.map.FuncKeyMap;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/copier/provider/BeanValueProvider.class */
public class BeanValueProvider implements ValueProvider<String> {
    private final Object source;
    private final boolean ignoreError;
    final Map<String, PropDesc> sourcePdMap;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 71185235:
                if (implMethodName.equals("lambda$new$9d0d83f1$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/Function") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/bean/copier/provider/BeanValueProvider") && lambda.getImplMethodSignature().equals("(ZLcn/hutool/core/lang/Editor;Ljava/lang/Object;)Ljava/lang/String;")) {
                    boolean booleanValue = ((Boolean) lambda.getCapturedArg(0)).booleanValue();
                    Editor editor = (Editor) lambda.getCapturedArg(1);
                    return key -> {
                        if (booleanValue && (key instanceof CharSequence)) {
                            key = key.toString().toLowerCase();
                        }
                        if (null != editor) {
                            key = editor.edit(key.toString());
                        }
                        return key.toString();
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public BeanValueProvider(Object bean, boolean ignoreCase, boolean ignoreError) {
        this(bean, ignoreCase, ignoreError, null);
    }

    public BeanValueProvider(Object bean, boolean ignoreCase, boolean ignoreError, Editor<String> keyEditor) {
        this.source = bean;
        this.ignoreError = ignoreError;
        Map<String, PropDesc> sourcePdMap = BeanUtil.getBeanDesc(this.source.getClass()).getPropMap(ignoreCase);
        this.sourcePdMap = new FuncKeyMap(new HashMap(sourcePdMap.size(), 1.0f), (Function) ((Serializable) key -> {
            if (ignoreCase && (key instanceof CharSequence)) {
                key = key.toString().toLowerCase();
            }
            if (null != keyEditor) {
                key = keyEditor.edit(key.toString());
            }
            return key.toString();
        }));
        this.sourcePdMap.putAll(sourcePdMap);
    }

    @Override // cn.hutool.core.bean.copier.ValueProvider
    public Object value(String key, Type valueType) {
        PropDesc sourcePd = getPropDesc(key, valueType);
        Object result = null;
        if (null != sourcePd) {
            result = sourcePd.getValue(this.source, valueType, this.ignoreError);
        }
        return result;
    }

    @Override // cn.hutool.core.bean.copier.ValueProvider
    public boolean containsKey(String key) {
        PropDesc sourcePd = getPropDesc(key, null);
        return null != sourcePd && sourcePd.isReadable(false);
    }

    private PropDesc getPropDesc(String key, Type valueType) {
        PropDesc sourcePd = this.sourcePdMap.get(key);
        if (null == sourcePd && (null == valueType || Boolean.class == valueType || Boolean.TYPE == valueType)) {
            sourcePd = this.sourcePdMap.get(StrUtil.upperFirstAndAddPre(key, ch.qos.logback.core.joran.util.beans.BeanUtil.PREFIX_GETTER_IS));
        }
        return sourcePd;
    }
}
