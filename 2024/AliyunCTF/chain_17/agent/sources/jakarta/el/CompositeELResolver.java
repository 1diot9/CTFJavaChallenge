package jakarta.el;

import java.beans.FeatureDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/CompositeELResolver.class */
public class CompositeELResolver extends ELResolver {
    private static final Class<?> SCOPED_ATTRIBUTE_EL_RESOLVER;
    private static final Set<String> KNOWN_NON_TYPE_CONVERTING_RESOLVERS = new HashSet();
    private int resolversSize = 0;
    private ELResolver[] resolvers = new ELResolver[8];
    private int typeConvertersSize = 0;
    private ELResolver[] typeConverters = new ELResolver[0];

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("jakarta.servlet.jsp.el.ScopedAttributeELResolver");
        } catch (ClassNotFoundException e) {
        }
        SCOPED_ATTRIBUTE_EL_RESOLVER = clazz;
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(ArrayELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(BeanELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(BeanNameELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(ListELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(MapELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(ResourceBundleELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add(StaticFieldELResolver.class.getName());
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("jakarta.servlet.jsp.el.ImplicitObjectELResolver");
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("jakarta.servlet.jsp.el.ImportELResolver");
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("jakarta.servlet.jsp.el.NotFoundELResolver");
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("jakarta.servlet.jsp.el.ScopedAttributeELResolver");
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("org.apache.jasper.el.JasperELResolver$GraalBeanELResolver");
        KNOWN_NON_TYPE_CONVERTING_RESOLVERS.add("org.apache.el.stream.StreamELResolverImpl");
    }

    public void add(ELResolver elResolver) {
        Objects.requireNonNull(elResolver);
        if (this.resolversSize >= this.resolvers.length) {
            ELResolver[] nr = new ELResolver[this.resolversSize * 2];
            System.arraycopy(this.resolvers, 0, nr, 0, this.resolversSize);
            this.resolvers = nr;
        }
        ELResolver[] eLResolverArr = this.resolvers;
        int i = this.resolversSize;
        this.resolversSize = i + 1;
        eLResolverArr[i] = elResolver;
        if (KNOWN_NON_TYPE_CONVERTING_RESOLVERS.contains(elResolver.getClass().getName())) {
            return;
        }
        if (this.typeConvertersSize == 0) {
            this.typeConverters = new ELResolver[1];
        } else if (this.typeConvertersSize == this.typeConverters.length) {
            ELResolver[] expandedTypeConverters = new ELResolver[this.typeConvertersSize * 2];
            System.arraycopy(this.typeConverters, 0, expandedTypeConverters, 0, this.typeConvertersSize);
            this.typeConverters = expandedTypeConverters;
        }
        ELResolver[] eLResolverArr2 = this.typeConverters;
        int i2 = this.typeConvertersSize;
        this.typeConvertersSize = i2 + 1;
        eLResolverArr2[i2] = elResolver;
    }

    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            Object result = this.resolvers[i].getValue(context, base, property);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        context.setPropertyResolved(false);
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            Object obj = this.resolvers[i].invoke(context, base, method, paramTypes, params);
            if (context.isPropertyResolved()) {
                return obj;
            }
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Object value;
        context.setPropertyResolved(false);
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            Class<?> type = this.resolvers[i].getType(context, base, property);
            if (context.isPropertyResolved()) {
                if (SCOPED_ATTRIBUTE_EL_RESOLVER != null && SCOPED_ATTRIBUTE_EL_RESOLVER.isAssignableFrom(this.resolvers[i].getClass()) && (value = this.resolvers[i].getValue(context, base, property)) != null) {
                    return value.getClass();
                }
                return type;
            }
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        context.setPropertyResolved(false);
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            this.resolvers[i].setValue(context, base, property, value);
            if (context.isPropertyResolved()) {
                return;
            }
        }
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            boolean readOnly = this.resolvers[i].isReadOnly(context, base, property);
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }
        return false;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return new FeatureIterator(context, base, this.resolvers, this.resolversSize);
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        Class<?> commonType = null;
        int sz = this.resolversSize;
        for (int i = 0; i < sz; i++) {
            Class<?> type = this.resolvers[i].getCommonPropertyType(context, base);
            if (type != null && (commonType == null || commonType.isAssignableFrom(type))) {
                commonType = type;
            }
        }
        return commonType;
    }

    @Override // jakarta.el.ELResolver
    public <T> T convertToType(ELContext eLContext, Object obj, Class<T> cls) {
        eLContext.setPropertyResolved(false);
        int i = this.typeConvertersSize;
        for (int i2 = 0; i2 < i; i2++) {
            T t = (T) this.typeConverters[i2].convertToType(eLContext, obj, cls);
            if (eLContext.isPropertyResolved()) {
                return t;
            }
        }
        return null;
    }

    @Deprecated(forRemoval = true, since = "EL 5.0")
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/CompositeELResolver$FeatureIterator.class */
    private static final class FeatureIterator implements Iterator<FeatureDescriptor> {
        private final ELContext context;
        private final Object base;
        private final ELResolver[] resolvers;
        private final int size;
        private Iterator<FeatureDescriptor> itr;
        private int idx = 0;
        private FeatureDescriptor next;

        FeatureIterator(ELContext context, Object base, ELResolver[] resolvers, int size) {
            this.context = context;
            this.base = base;
            this.resolvers = resolvers;
            this.size = size;
            guaranteeIterator();
        }

        private void guaranteeIterator() {
            while (this.itr == null && this.idx < this.size) {
                this.itr = this.resolvers[this.idx].getFeatureDescriptors(this.context, this.base);
                this.idx++;
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (this.itr != null) {
                while (this.next == null && this.itr.hasNext()) {
                    this.next = this.itr.next();
                }
                if (this.next == null) {
                    this.itr = null;
                    guaranteeIterator();
                }
                return hasNext();
            }
            return false;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public FeatureDescriptor next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            FeatureDescriptor result = this.next;
            this.next = null;
            return result;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
