package org.springframework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/MutablePropertyValues.class */
public class MutablePropertyValues implements PropertyValues, Serializable {
    private final List<PropertyValue> propertyValueList;

    @Nullable
    private Set<String> processedProperties;
    private volatile boolean converted;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList(0);
    }

    public MutablePropertyValues(@Nullable PropertyValues original) {
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList(pvs.length);
            for (PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
            return;
        }
        this.propertyValueList = new ArrayList(0);
    }

    public MutablePropertyValues(@Nullable Map<?, ?> original) {
        if (original != null) {
            this.propertyValueList = new ArrayList(original.size());
            original.forEach((attrName, attrValue) -> {
                this.propertyValueList.add(new PropertyValue(attrName.toString(), attrValue));
            });
        } else {
            this.propertyValueList = new ArrayList(0);
        }
    }

    public MutablePropertyValues(@Nullable List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList != null ? propertyValueList : new ArrayList<>();
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }

    public int size() {
        return this.propertyValueList.size();
    }

    public MutablePropertyValues addPropertyValues(@Nullable PropertyValues other) {
        if (other != null) {
            PropertyValue[] pvs = other.getPropertyValues();
            for (PropertyValue pv : pvs) {
                addPropertyValue(new PropertyValue(pv));
            }
        }
        return this;
    }

    public MutablePropertyValues addPropertyValues(@Nullable Map<?, ?> other) {
        if (other != null) {
            other.forEach((attrName, attrValue) -> {
                addPropertyValue(new PropertyValue(attrName.toString(), attrValue));
            });
        }
        return this;
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); i++) {
            PropertyValue currentPv = this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                setPropertyValueAt(mergeIfRequired(pv, currentPv), i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    public void addPropertyValue(String propertyName, Object propertyValue) {
        addPropertyValue(new PropertyValue(propertyName, propertyValue));
    }

    public MutablePropertyValues add(String propertyName, @Nullable Object propertyValue) {
        addPropertyValue(new PropertyValue(propertyName, propertyValue));
        return this;
    }

    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }

    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) value;
            if (mergeable.isMergeEnabled()) {
                Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv.getName(), merged);
            }
        }
        return newPv;
    }

    public void removePropertyValue(PropertyValue pv) {
        this.propertyValueList.remove(pv);
    }

    public void removePropertyValue(String propertyName) {
        this.propertyValueList.remove(getPropertyValue(propertyName));
    }

    @Override // org.springframework.beans.PropertyValues, java.lang.Iterable
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.propertyValueList).iterator();
    }

    @Override // org.springframework.beans.PropertyValues, java.lang.Iterable
    public Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(this.propertyValueList, 0);
    }

    @Override // org.springframework.beans.PropertyValues
    public Stream<PropertyValue> stream() {
        return this.propertyValueList.stream();
    }

    @Override // org.springframework.beans.PropertyValues
    public PropertyValue[] getPropertyValues() {
        return (PropertyValue[]) this.propertyValueList.toArray(new PropertyValue[0]);
    }

    @Override // org.springframework.beans.PropertyValues
    @Nullable
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    @Nullable
    public Object get(String propertyName) {
        PropertyValue pv = getPropertyValue(propertyName);
        if (pv != null) {
            return pv.getValue();
        }
        return null;
    }

    @Override // org.springframework.beans.PropertyValues
    public PropertyValues changesSince(PropertyValues old) {
        MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        }
        for (PropertyValue newPv : this.propertyValueList) {
            PropertyValue pvOld = old.getPropertyValue(newPv.getName());
            if (pvOld == null || !pvOld.equals(newPv)) {
                changes.addPropertyValue(newPv);
            }
        }
        return changes;
    }

    @Override // org.springframework.beans.PropertyValues
    public boolean contains(String propertyName) {
        return getPropertyValue(propertyName) != null || (this.processedProperties != null && this.processedProperties.contains(propertyName));
    }

    @Override // org.springframework.beans.PropertyValues
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

    public void registerProcessedProperty(String propertyName) {
        if (this.processedProperties == null) {
            this.processedProperties = new HashSet(4);
        }
        this.processedProperties.add(propertyName);
    }

    public void clearProcessedProperty(String propertyName) {
        if (this.processedProperties != null) {
            this.processedProperties.remove(propertyName);
        }
    }

    public void setConverted() {
        this.converted = true;
    }

    public boolean isConverted() {
        return this.converted;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MutablePropertyValues) {
                MutablePropertyValues that = (MutablePropertyValues) other;
                if (this.propertyValueList.equals(that.propertyValueList)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.propertyValueList.hashCode();
    }

    public String toString() {
        PropertyValue[] pvs = getPropertyValues();
        if (pvs.length > 0) {
            return "PropertyValues: length=" + pvs.length + "; " + StringUtils.arrayToDelimitedString(pvs, "; ");
        }
        return "PropertyValues: length=0";
    }
}
