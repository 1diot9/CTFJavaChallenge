package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.IndexedElementsBinder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.ResolvableType;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ArrayBinder.class */
public class ArrayBinder extends IndexedElementsBinder<Object> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayBinder(Binder.Context context) {
        super(context);
    }

    @Override // org.springframework.boot.context.properties.bind.AggregateBinder
    protected Object bindAggregate(ConfigurationPropertyName name, Bindable<?> target, AggregateElementBinder elementBinder) {
        IndexedElementsBinder.IndexedCollectionSupplier result = new IndexedElementsBinder.IndexedCollectionSupplier(ArrayList::new);
        ResolvableType aggregateType = target.getType();
        ResolvableType elementType = target.getType().getComponentType();
        bindIndexed(name, target, elementBinder, aggregateType, elementType, result);
        if (result.wasSupplied()) {
            List<Object> list = (List) result.get();
            Object array = Array.newInstance(elementType.resolve(), list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.bind.AggregateBinder
    protected Object merge(Supplier<Object> existing, Object additional) {
        return additional;
    }
}
