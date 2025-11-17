package org.apache.el.stream;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/StreamELResolverImpl.class */
public class StreamELResolverImpl extends ELResolver {
    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "Tomcat 10.1.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        if ("stream".equals(method) && params.length == 0) {
            if (base.getClass().isArray()) {
                context.setPropertyResolved(true);
                return new Stream(new ArrayIterator(base));
            }
            if (base instanceof Collection) {
                context.setPropertyResolved(true);
                Collection<Object> collection = (Collection) base;
                return new Stream(collection.iterator());
            }
            return null;
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/StreamELResolverImpl$ArrayIterator.class */
    private static class ArrayIterator implements Iterator<Object> {
        private final Object base;
        private final int size;
        private int index = 0;

        ArrayIterator(Object base) {
            this.base = base;
            this.size = Array.getLength(base);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.size > this.index;
        }

        @Override // java.util.Iterator
        public Object next() {
            try {
                Object obj = this.base;
                int i = this.index;
                this.index = i + 1;
                return Array.get(obj, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
