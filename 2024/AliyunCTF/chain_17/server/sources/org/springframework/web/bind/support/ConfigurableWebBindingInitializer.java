package org.springframework.web.bind.support;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/ConfigurableWebBindingInitializer.class */
public class ConfigurableWebBindingInitializer implements WebBindingInitializer {
    private boolean autoGrowNestedPaths = true;
    private boolean directFieldAccess = false;

    @Nullable
    private Boolean declarativeBinding;

    @Nullable
    private MessageCodesResolver messageCodesResolver;

    @Nullable
    private BindingErrorProcessor bindingErrorProcessor;

    @Nullable
    private Validator validator;

    @Nullable
    private ConversionService conversionService;

    @Nullable
    private PropertyEditorRegistrar[] propertyEditorRegistrars;

    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public final void setDirectFieldAccess(boolean directFieldAccess) {
        this.directFieldAccess = directFieldAccess;
    }

    public boolean isDirectFieldAccess() {
        return this.directFieldAccess;
    }

    public void setDeclarativeBinding(boolean declarativeBinding) {
        this.declarativeBinding = Boolean.valueOf(declarativeBinding);
    }

    public boolean isDeclarativeBinding() {
        if (this.declarativeBinding != null) {
            return this.declarativeBinding.booleanValue();
        }
        return false;
    }

    public final void setMessageCodesResolver(@Nullable MessageCodesResolver messageCodesResolver) {
        this.messageCodesResolver = messageCodesResolver;
    }

    @Nullable
    public final MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }

    public final void setBindingErrorProcessor(@Nullable BindingErrorProcessor bindingErrorProcessor) {
        this.bindingErrorProcessor = bindingErrorProcessor;
    }

    @Nullable
    public final BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }

    public final void setValidator(@Nullable Validator validator) {
        this.validator = validator;
    }

    @Nullable
    public final Validator getValidator() {
        return this.validator;
    }

    public final void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    public final ConversionService getConversionService() {
        return this.conversionService;
    }

    public final void setPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar) {
        this.propertyEditorRegistrars = new PropertyEditorRegistrar[]{propertyEditorRegistrar};
    }

    public final void setPropertyEditorRegistrars(@Nullable PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }

    @Nullable
    public final PropertyEditorRegistrar[] getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    @Override // org.springframework.web.bind.support.WebBindingInitializer
    public void initBinder(WebDataBinder binder) {
        Class<?> type;
        binder.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
        if (this.directFieldAccess) {
            binder.initDirectFieldAccess();
        }
        if (this.declarativeBinding != null) {
            binder.setDeclarativeBinding(this.declarativeBinding.booleanValue());
        }
        if (this.messageCodesResolver != null) {
            binder.setMessageCodesResolver(this.messageCodesResolver);
        }
        if (this.bindingErrorProcessor != null) {
            binder.setBindingErrorProcessor(this.bindingErrorProcessor);
        }
        if (this.validator != null && (type = getTargetType(binder)) != null && this.validator.supports(type)) {
            binder.setValidator(this.validator);
        }
        if (this.conversionService != null) {
            binder.setConversionService(this.conversionService);
        }
        if (this.propertyEditorRegistrars != null) {
            for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
                propertyEditorRegistrar.registerCustomEditors(binder);
            }
        }
    }

    @Nullable
    private static Class<?> getTargetType(WebDataBinder binder) {
        Class<?> type = null;
        if (binder.getTarget() != null) {
            type = binder.getTarget().getClass();
        } else if (binder.getTargetType() != null) {
            type = binder.getTargetType().resolve();
        }
        return type;
    }
}
