package org.springframework.web.jsf.el;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELResolver;
import jakarta.el.PropertyNotWritableException;
import jakarta.faces.context.FacesContext;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/jsf/el/SpringBeanFacesELResolver.class */
public class SpringBeanFacesELResolver extends ELResolver {
    @Override // jakarta.el.ELResolver
    @Nullable
    public Object getValue(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getBean(beanName);
            }
            return null;
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    @Nullable
    public Class<?> getType(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getType(beanName);
            }
            return null;
        }
        return null;
    }

    @Override // jakarta.el.ELResolver
    public void setValue(ELContext elContext, @Nullable Object base, Object property, Object value) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                if (value == wac.getBean(beanName)) {
                    elContext.setPropertyResolved(true);
                    return;
                }
                throw new PropertyNotWritableException("Variable '" + beanName + "' refers to a Spring bean which by definition is not writable");
            }
        }
    }

    @Override // jakarta.el.ELResolver
    public boolean isReadOnly(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // jakarta.el.ELResolver
    @Nullable
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, @Nullable Object base) {
        return null;
    }

    @Override // jakarta.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext elContext, @Nullable Object base) {
        return Object.class;
    }

    protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}
