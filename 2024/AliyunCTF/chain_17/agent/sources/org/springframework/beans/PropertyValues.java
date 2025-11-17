package org.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyValues.class */
public interface PropertyValues extends Iterable<PropertyValue> {
    PropertyValue[] getPropertyValues();

    @Nullable
    PropertyValue getPropertyValue(String propertyName);

    PropertyValues changesSince(PropertyValues old);

    boolean contains(String propertyName);

    boolean isEmpty();

    @Override // java.lang.Iterable
    default Iterator<PropertyValue> iterator() {
        return Arrays.asList(getPropertyValues()).iterator();
    }

    @Override // java.lang.Iterable
    default Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(getPropertyValues(), 0);
    }

    default Stream<PropertyValue> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
