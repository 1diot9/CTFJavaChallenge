package org.springframework.validation.support;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/support/BindingAwareModelMap.class */
public class BindingAwareModelMap extends ExtendedModelMap {
    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object put(String key, @Nullable Object value) {
        removeBindingResultIfNecessary(key, value);
        return super.put((Object) key, value);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends String, ?> map) {
        map.forEach((v1, v2) -> {
            removeBindingResultIfNecessary(v1, v2);
        });
        super.putAll(map);
    }

    private void removeBindingResultIfNecessary(Object key, @Nullable Object value) {
        if (key instanceof String) {
            String attributeName = (String) key;
            if (!attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName;
                BindingResult bindingResult = (BindingResult) get(bindingResultKey);
                if (bindingResult != null && bindingResult.getTarget() != value) {
                    remove(bindingResultKey);
                }
            }
        }
    }
}
