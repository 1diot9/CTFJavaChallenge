package org.springframework.web.method.annotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.context.request.WebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/SessionAttributesHandler.class */
public class SessionAttributesHandler {
    private final Set<String> attributeNames = new HashSet();
    private final Set<Class<?>> attributeTypes = new HashSet();
    private final Set<String> knownAttributeNames = Collections.newSetFromMap(new ConcurrentHashMap(4));
    private final SessionAttributeStore sessionAttributeStore;

    public SessionAttributesHandler(Class<?> handlerType, SessionAttributeStore sessionAttributeStore) {
        Assert.notNull(sessionAttributeStore, "SessionAttributeStore may not be null");
        this.sessionAttributeStore = sessionAttributeStore;
        SessionAttributes ann = (SessionAttributes) AnnotatedElementUtils.findMergedAnnotation(handlerType, SessionAttributes.class);
        if (ann != null) {
            Collections.addAll(this.attributeNames, ann.names());
            Collections.addAll(this.attributeTypes, ann.types());
        }
        this.knownAttributeNames.addAll(this.attributeNames);
    }

    public boolean hasSessionAttributes() {
        return (this.attributeNames.isEmpty() && this.attributeTypes.isEmpty()) ? false : true;
    }

    public boolean isHandlerSessionAttribute(String attributeName, Class<?> attributeType) {
        Assert.notNull(attributeName, "Attribute name must not be null");
        if (this.attributeNames.contains(attributeName) || this.attributeTypes.contains(attributeType)) {
            this.knownAttributeNames.add(attributeName);
            return true;
        }
        return false;
    }

    public void storeAttributes(WebRequest request, Map<String, ?> attributes) {
        attributes.forEach((name, value) -> {
            if (value != null && isHandlerSessionAttribute(name, value.getClass())) {
                this.sessionAttributeStore.storeAttribute(request, name, value);
            }
        });
    }

    public Map<String, Object> retrieveAttributes(WebRequest request) {
        Map<String, Object> attributes = new HashMap<>();
        for (String name : this.knownAttributeNames) {
            Object value = this.sessionAttributeStore.retrieveAttribute(request, name);
            if (value != null) {
                attributes.put(name, value);
            }
        }
        return attributes;
    }

    public void cleanupAttributes(WebRequest request) {
        for (String attributeName : this.knownAttributeNames) {
            this.sessionAttributeStore.cleanupAttribute(request, attributeName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Object retrieveAttribute(WebRequest request, String attributeName) {
        return this.sessionAttributeStore.retrieveAttribute(request, attributeName);
    }
}
