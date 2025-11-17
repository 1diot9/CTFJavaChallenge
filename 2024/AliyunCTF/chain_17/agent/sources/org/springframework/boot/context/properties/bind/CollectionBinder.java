package org.springframework.boot.context.properties.bind;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.IndexedElementsBinder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/CollectionBinder.class */
public class CollectionBinder extends IndexedElementsBinder<Collection<Object>> {
    @Override // org.springframework.boot.context.properties.bind.AggregateBinder
    protected /* bridge */ /* synthetic */ Object merge(Supplier existing, Object additional) {
        return merge((Supplier<Collection<Object>>) existing, (Collection<Object>) additional);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CollectionBinder(Binder.Context context) {
        super(context);
    }

    @Override // org.springframework.boot.context.properties.bind.AggregateBinder
    protected Object bindAggregate(ConfigurationPropertyName name, Bindable<?> target, AggregateElementBinder elementBinder) {
        ResolvableType aggregateType = ResolvableType.forClassWithGenerics((Class<?>) List.class, target.getType().asCollection().getGenerics());
        ResolvableType elementType = target.getType().asCollection().getGeneric(new int[0]);
        IndexedElementsBinder.IndexedCollectionSupplier result = new IndexedElementsBinder.IndexedCollectionSupplier(() -> {
            return CollectionFactory.createCollection(List.class, elementType.resolve(), 0);
        });
        bindIndexed(name, target, elementBinder, aggregateType, elementType, result);
        if (result.wasSupplied()) {
            return result.get();
        }
        return null;
    }

    protected Collection<Object> merge(Supplier<Collection<Object>> existing, Collection<Object> additional) {
        Collection<Object> existingCollection = getExistingIfPossible(existing);
        if (existingCollection == null) {
            return additional;
        }
        try {
            existingCollection.clear();
            existingCollection.addAll(additional);
            return copyIfPossible(existingCollection);
        } catch (UnsupportedOperationException e) {
            return createNewCollection(additional);
        }
    }

    private Collection<Object> getExistingIfPossible(Supplier<Collection<Object>> existing) {
        try {
            return existing.get();
        } catch (Exception e) {
            return null;
        }
    }

    private Collection<Object> copyIfPossible(Collection<Object> collection) {
        try {
            return createNewCollection(collection);
        } catch (Exception e) {
            return collection;
        }
    }

    private Collection<Object> createNewCollection(Collection<Object> collection) {
        Collection<Object> result = CollectionFactory.createCollection(collection.getClass(), collection.size());
        result.addAll(collection);
        return result;
    }
}
