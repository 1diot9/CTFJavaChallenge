package org.springframework.validation.support;

import org.springframework.lang.Nullable;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindingResult;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/support/BindingAwareConcurrentModel.class */
public class BindingAwareConcurrentModel extends ConcurrentModel {
    @Override // org.springframework.ui.ConcurrentModel, java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    @Nullable
    public Object put(String key, @Nullable Object value) {
        removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    private void removeBindingResultIfNecessary(String key, @Nullable Object value) {
        if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            String resultKey = BindingResult.MODEL_KEY_PREFIX + key;
            BindingResult result = (BindingResult) get(resultKey);
            if (result != null && result.getTarget() != value) {
                remove(resultKey);
            }
        }
    }
}
