package cn.hutool.core.map;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/CaseInsensitiveMap.class */
public class CaseInsensitiveMap<K, V> extends FuncKeyMap<K, V> {
    private static final long serialVersionUID = 4043263744224569870L;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -2131258151:
                if (implMethodName.equals("lambda$new$eea40c49$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/Function") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/map/CaseInsensitiveMap") && lambda.getImplMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
                    return key -> {
                        if (key instanceof CharSequence) {
                            key = key.toString().toLowerCase();
                        }
                        return key;
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public CaseInsensitiveMap() {
        this(16);
    }

    public CaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public CaseInsensitiveMap(Map<? extends K, ? extends V> m) {
        this(0.75f, m);
    }

    public CaseInsensitiveMap(float loadFactor, Map<? extends K, ? extends V> m) {
        this(m.size(), loadFactor);
        putAll(m);
    }

    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        this(MapBuilder.create(new HashMap(initialCapacity, loadFactor)));
    }

    CaseInsensitiveMap(MapBuilder<K, V> emptyMapBuilder) {
        super(emptyMapBuilder.build(), (Function) ((Serializable) key -> {
            if (key instanceof CharSequence) {
                key = key.toString().toLowerCase();
            }
            return key;
        }));
    }
}
