package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CustomCollectionEditor.class */
public class CustomCollectionEditor extends PropertyEditorSupport {
    private final Class<? extends Collection> collectionType;
    private final boolean nullAsEmptyCollection;

    public CustomCollectionEditor(Class<? extends Collection> collectionType) {
        this(collectionType, false);
    }

    public CustomCollectionEditor(Class<? extends Collection> collectionType, boolean nullAsEmptyCollection) {
        Assert.notNull(collectionType, "Collection type is required");
        if (!Collection.class.isAssignableFrom(collectionType)) {
            throw new IllegalArgumentException("Collection type [" + collectionType.getName() + "] does not implement [java.util.Collection]");
        }
        this.collectionType = collectionType;
        this.nullAsEmptyCollection = nullAsEmptyCollection;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text);
    }

    public void setValue(@Nullable Object value) {
        if (value == null && this.nullAsEmptyCollection) {
            super.setValue(createCollection(this.collectionType, 0));
            return;
        }
        if (value == null || (this.collectionType.isInstance(value) && !alwaysCreateNewCollection())) {
            super.setValue(value);
            return;
        }
        if (value instanceof Collection) {
            Collection<?> source = (Collection) value;
            Collection<Object> target = createCollection(this.collectionType, source.size());
            for (Object elem : source) {
                target.add(convertElement(elem));
            }
            super.setValue(target);
            return;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            Collection<Object> target2 = createCollection(this.collectionType, length);
            for (int i = 0; i < length; i++) {
                target2.add(convertElement(Array.get(value, i)));
            }
            super.setValue(target2);
            return;
        }
        Collection<Object> target3 = createCollection(this.collectionType, 1);
        target3.add(convertElement(value));
        super.setValue(target3);
    }

    protected Collection<Object> createCollection(Class<? extends Collection> collectionType, int initialCapacity) {
        if (!collectionType.isInterface()) {
            try {
                return (Collection) ReflectionUtils.accessibleConstructor(collectionType, new Class[0]).newInstance(new Object[0]);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate collection class: " + collectionType.getName(), ex);
            }
        }
        if (List.class == collectionType) {
            return new ArrayList(initialCapacity);
        }
        if (SortedSet.class == collectionType) {
            return new TreeSet();
        }
        return new LinkedHashSet(initialCapacity);
    }

    protected boolean alwaysCreateNewCollection() {
        return false;
    }

    protected Object convertElement(Object element) {
        return element;
    }

    @Nullable
    public String getAsText() {
        return null;
    }
}
