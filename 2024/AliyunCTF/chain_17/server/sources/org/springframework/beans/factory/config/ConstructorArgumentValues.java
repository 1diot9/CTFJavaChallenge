package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ConstructorArgumentValues.class */
public class ConstructorArgumentValues {
    private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap();
    private final List<ValueHolder> genericArgumentValues = new ArrayList();

    public ConstructorArgumentValues() {
    }

    public ConstructorArgumentValues(ConstructorArgumentValues original) {
        addArgumentValues(original);
    }

    public void addArgumentValues(@Nullable ConstructorArgumentValues other) {
        if (other != null) {
            other.indexedArgumentValues.forEach((index, argValue) -> {
                addOrMergeIndexedArgumentValue(index, argValue.copy());
            });
            other.genericArgumentValues.stream().filter(valueHolder -> {
                return !this.genericArgumentValues.contains(valueHolder);
            }).forEach(valueHolder2 -> {
                addOrMergeGenericArgumentValue(valueHolder2.copy());
            });
        }
    }

    public void addIndexedArgumentValue(int index, @Nullable Object value) {
        addIndexedArgumentValue(index, new ValueHolder(value));
    }

    public void addIndexedArgumentValue(int index, @Nullable Object value, String type) {
        addIndexedArgumentValue(index, new ValueHolder(value, type));
    }

    public void addIndexedArgumentValue(int index, ValueHolder newValue) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        Assert.notNull(newValue, "ValueHolder must not be null");
        addOrMergeIndexedArgumentValue(Integer.valueOf(index), newValue);
    }

    private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
        ValueHolder currentValue = this.indexedArgumentValues.get(key);
        if (currentValue != null) {
            Object value = newValue.getValue();
            if (value instanceof Mergeable) {
                Mergeable mergeable = (Mergeable) value;
                if (mergeable.isMergeEnabled()) {
                    newValue.setValue(mergeable.merge(currentValue.getValue()));
                }
            }
        }
        this.indexedArgumentValues.put(key, newValue);
    }

    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(Integer.valueOf(index));
    }

    @Nullable
    public ValueHolder getIndexedArgumentValue(int index, @Nullable Class<?> requiredType) {
        return getIndexedArgumentValue(index, requiredType, null);
    }

    @Nullable
    public ValueHolder getIndexedArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = this.indexedArgumentValues.get(Integer.valueOf(index));
        if (valueHolder != null) {
            if (valueHolder.getType() == null || (requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
                if (valueHolder.getName() != null) {
                    if (requiredName != null) {
                        if (!requiredName.isEmpty() && !requiredName.equals(valueHolder.getName())) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                return valueHolder;
            }
            return null;
        }
        return null;
    }

    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return Collections.unmodifiableMap(this.indexedArgumentValues);
    }

    public void addGenericArgumentValue(@Nullable Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }

    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ValueHolder(value, type));
    }

    public void addGenericArgumentValue(ValueHolder newValue) {
        Assert.notNull(newValue, "ValueHolder must not be null");
        if (!this.genericArgumentValues.contains(newValue)) {
            addOrMergeGenericArgumentValue(newValue);
        }
    }

    private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
        if (newValue.getName() != null) {
            Iterator<ValueHolder> it = this.genericArgumentValues.iterator();
            while (it.hasNext()) {
                ValueHolder currentValue = it.next();
                if (newValue.getName().equals(currentValue.getName())) {
                    Object value = newValue.getValue();
                    if (value instanceof Mergeable) {
                        Mergeable mergeable = (Mergeable) value;
                        if (mergeable.isMergeEnabled()) {
                            newValue.setValue(mergeable.merge(currentValue.getValue()));
                        }
                    }
                    it.remove();
                }
            }
        }
        this.genericArgumentValues.add(newValue);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
        return getGenericArgumentValue(requiredType, null, null);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName) {
        return getGenericArgumentValue(requiredType, requiredName, null);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(@Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            if (usedValueHolders == null || !usedValueHolders.contains(valueHolder)) {
                if (valueHolder.getName() == null || (requiredName != null && (requiredName.isEmpty() || requiredName.equals(valueHolder.getName())))) {
                    if (valueHolder.getType() == null || (requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
                        if (requiredType == null || valueHolder.getType() != null || valueHolder.getName() != null || ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
                            return valueHolder;
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<ValueHolder> getGenericArgumentValues() {
        return Collections.unmodifiableList(this.genericArgumentValues);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, Class<?> requiredType) {
        return getArgumentValue(index, requiredType, null, null);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName) {
        return getArgumentValue(index, requiredType, requiredName, null);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType, requiredName);
        if (valueHolder == null) {
            valueHolder = getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
        }
        return valueHolder;
    }

    public boolean containsNamedArgument() {
        for (ValueHolder valueHolder : this.indexedArgumentValues.values()) {
            if (valueHolder.getName() != null) {
                return true;
            }
        }
        for (ValueHolder valueHolder2 : this.genericArgumentValues) {
            if (valueHolder2.getName() != null) {
                return true;
            }
        }
        return false;
    }

    public int getArgumentCount() {
        return this.indexedArgumentValues.size() + this.genericArgumentValues.size();
    }

    public boolean isEmpty() {
        return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
    }

    public void clear() {
        this.indexedArgumentValues.clear();
        this.genericArgumentValues.clear();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConstructorArgumentValues)) {
            return false;
        }
        ConstructorArgumentValues that = (ConstructorArgumentValues) other;
        if (this.genericArgumentValues.size() != that.genericArgumentValues.size() || this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
            return false;
        }
        Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
        Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            ValueHolder vh1 = it1.next();
            if (!vh1.contentEquals(it2.next())) {
                return false;
            }
        }
        for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
            ValueHolder vh12 = entry.getValue();
            ValueHolder vh2 = that.indexedArgumentValues.get(entry.getKey());
            if (vh2 == null || !vh12.contentEquals(vh2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 7;
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            hashCode = (31 * hashCode) + valueHolder.contentHashCode();
        }
        int hashCode2 = 29 * hashCode;
        for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
            hashCode2 = (31 * hashCode2) + (entry.getValue().contentHashCode() ^ entry.getKey().hashCode());
        }
        return hashCode2;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ConstructorArgumentValues$ValueHolder.class */
    public static class ValueHolder implements BeanMetadataElement {

        @Nullable
        private Object value;

        @Nullable
        private String type;

        @Nullable
        private String name;

        @Nullable
        private Object source;
        private boolean converted = false;

        @Nullable
        private Object convertedValue;

        public ValueHolder(@Nullable Object value) {
            this.value = value;
        }

        public ValueHolder(@Nullable Object value, @Nullable String type) {
            this.value = value;
            this.type = type;
        }

        public ValueHolder(@Nullable Object value, @Nullable String type, @Nullable String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }

        public void setValue(@Nullable Object value) {
            this.value = value;
        }

        @Nullable
        public Object getValue() {
            return this.value;
        }

        public void setType(@Nullable String type) {
            this.type = type;
        }

        @Nullable
        public String getType() {
            return this.type;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getName() {
            return this.name;
        }

        public void setSource(@Nullable Object source) {
            this.source = source;
        }

        @Override // org.springframework.beans.BeanMetadataElement
        @Nullable
        public Object getSource() {
            return this.source;
        }

        public synchronized boolean isConverted() {
            return this.converted;
        }

        public synchronized void setConvertedValue(@Nullable Object value) {
            this.converted = value != null;
            this.convertedValue = value;
        }

        @Nullable
        public synchronized Object getConvertedValue() {
            return this.convertedValue;
        }

        private boolean contentEquals(ValueHolder other) {
            return this == other || (ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type));
        }

        private int contentHashCode() {
            return ObjectUtils.nullSafeHash(this.value, this.type);
        }

        public ValueHolder copy() {
            ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
            copy.setSource(this.source);
            return copy;
        }
    }
}
