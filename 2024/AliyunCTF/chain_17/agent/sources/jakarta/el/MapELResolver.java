package jakarta.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/MapELResolver.class */
public class MapELResolver extends ELResolver {
    private static final Class<?> UNMODIFIABLE = Collections.unmodifiableMap(new HashMap()).getClass();
    private final boolean readOnly;

    public MapELResolver() {
        this.readOnly = false;
    }

    public MapELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            Map<?, ?> map = (Map) base;
            if (this.readOnly || map.getClass() == UNMODIFIABLE) {
                return null;
            }
            return Object.class;
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            return ((Map) base).get(property);
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            if (this.readOnly) {
                throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
            }
            try {
                Map<Object, Object> map = (Map) base;
                map.put(property, value);
            } catch (UnsupportedOperationException e) {
                throw new PropertyNotWritableException(e);
            }
        }
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            return this.readOnly || UNMODIFIABLE.equals(base.getClass());
        }
        return this.readOnly;
    }

    @Override // jakarta.el.ELResolver
    @Deprecated(forRemoval = true, since = "EL 5.0")
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof Map) {
            List<FeatureDescriptor> feats = new ArrayList<>();
            for (Object key : ((Map) base).keySet()) {
                FeatureDescriptor desc = new FeatureDescriptor();
                desc.setDisplayName(key.toString());
                desc.setShortDescription("");
                desc.setExpert(false);
                desc.setHidden(false);
                desc.setName(key.toString());
                desc.setPreferred(true);
                desc.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
                desc.setValue("type", key.getClass());
                feats.add(desc);
            }
            return feats.iterator();
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof Map) {
            return Object.class;
        }
        return null;
    }
}
