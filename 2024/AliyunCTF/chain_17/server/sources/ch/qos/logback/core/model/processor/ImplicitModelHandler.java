package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ImcplicitActionDataForBasicProperty;
import ch.qos.logback.core.joran.action.ImplicitModelData;
import ch.qos.logback.core.joran.action.ImplicitModelDataForComplexProperty;
import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.model.ComponentModel;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/ImplicitModelHandler.class */
public class ImplicitModelHandler extends ModelHandlerBase {
    private final BeanDescriptionCache beanDescriptionCache;
    private ImplicitModelData implicitModelData;
    static final String PARENT_PROPPERTY_KEY = "parent";
    public static final String IGNORING_UNKNOWN_PROP = "Ignoring unknown property";
    boolean inError;

    public ImplicitModelHandler(Context context, BeanDescriptionCache beanDescriptionCache) {
        super(context);
        this.inError = false;
        this.beanDescriptionCache = beanDescriptionCache;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<? extends ImplicitModel> getSupportedModelClass() {
        return ImplicitModel.class;
    }

    public static ImplicitModelHandler makeInstance(Context context, ModelInterpretationContext mic) {
        BeanDescriptionCache beanDescriptionCache = mic.getBeanDescriptionCache();
        return new ImplicitModelHandler(context, beanDescriptionCache);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) {
        ImplicitModel implicitModel = (ImplicitModel) model;
        if (mic.isObjectStackEmpty()) {
            this.inError = true;
            return;
        }
        String nestedElementTagName = implicitModel.getTag();
        Object o = mic.peekObject();
        PropertySetter parentBean = new PropertySetter(this.beanDescriptionCache, o);
        parentBean.setContext(this.context);
        AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
        switch (aggregationType) {
            case NOT_FOUND:
                addWarn("Ignoring unknown property [" + nestedElementTagName + "] in [" + o.getClass().getName() + "]");
                this.inError = true;
                implicitModel.markAsSkipped();
                return;
            case AS_BASIC_PROPERTY:
            case AS_BASIC_PROPERTY_COLLECTION:
                ImcplicitActionDataForBasicProperty adBasicProperty = new ImcplicitActionDataForBasicProperty(parentBean, aggregationType, nestedElementTagName);
                this.implicitModelData = adBasicProperty;
                doBasicProperty(mic, model, adBasicProperty);
                return;
            case AS_COMPLEX_PROPERTY_COLLECTION:
            case AS_COMPLEX_PROPERTY:
                ImplicitModelDataForComplexProperty adComplex = new ImplicitModelDataForComplexProperty(parentBean, aggregationType, nestedElementTagName);
                this.implicitModelData = adComplex;
                doComplex(mic, implicitModel, adComplex);
                return;
            default:
                addError("PropertySetter.computeAggregationType returned " + String.valueOf(aggregationType));
                return;
        }
    }

    void doBasicProperty(ModelInterpretationContext interpretationContext, Model model, ImcplicitActionDataForBasicProperty actionData) {
        String finalBody = interpretationContext.subst(model.getBodyText());
        switch (actionData.aggregationType) {
            case AS_BASIC_PROPERTY:
                actionData.parentBean.setProperty(actionData.propertyName, finalBody);
                return;
            case AS_BASIC_PROPERTY_COLLECTION:
                actionData.parentBean.addBasicProperty(actionData.propertyName, finalBody);
                return;
            default:
                addError("Unexpected aggregationType " + String.valueOf(actionData.aggregationType));
                return;
        }
    }

    public void doComplex(ModelInterpretationContext interpretationContext, ComponentModel componentModel, ImplicitModelDataForComplexProperty actionData) {
        Class<?> componentClass;
        String className = componentModel.getClassName();
        String substClassName = interpretationContext.subst(className);
        String fqcn = interpretationContext.getImport(substClassName);
        try {
            if (!OptionHelper.isNullOrEmpty(fqcn)) {
                componentClass = Loader.loadClass(fqcn, this.context);
            } else {
                PropertySetter parentBean = actionData.parentBean;
                componentClass = parentBean.getClassNameViaImplicitRules(actionData.propertyName, actionData.getAggregationType(), interpretationContext.getDefaultNestedComponentRegistry());
            }
            if (componentClass == null) {
                actionData.inError = true;
                String errMsg = "Could not find an appropriate class for property [" + componentModel.getTag() + "]";
                addError(errMsg);
            } else {
                if (OptionHelper.isNullOrEmpty(fqcn)) {
                    addInfo("Assuming default type [" + componentClass.getName() + "] for [" + componentModel.getTag() + "] property");
                }
                actionData.setNestedComplexProperty(componentClass.getConstructor(new Class[0]).newInstance(new Object[0]));
                if (actionData.getNestedComplexProperty() instanceof ContextAware) {
                    ((ContextAware) actionData.getNestedComplexProperty()).setContext(this.context);
                }
                interpretationContext.pushObject(actionData.getNestedComplexProperty());
            }
        } catch (Exception oops) {
            actionData.inError = true;
            String msg = "Could not create component [" + componentModel.getTag() + "] of type [" + fqcn + "]";
            addError(msg, oops);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext intercon, Model model) {
        if (!this.inError && this.implicitModelData != null && !this.implicitModelData.inError && (this.implicitModelData instanceof ImplicitModelDataForComplexProperty)) {
            postHandleComplex(intercon, model, (ImplicitModelDataForComplexProperty) this.implicitModelData);
        }
    }

    private void postHandleComplex(ModelInterpretationContext mic, Model model, ImplicitModelDataForComplexProperty imdComplex) {
        PropertySetter nestedBean = new PropertySetter(this.beanDescriptionCache, imdComplex.getNestedComplexProperty());
        nestedBean.setContext(this.context);
        if (nestedBean.computeAggregationType("parent") == AggregationType.AS_COMPLEX_PROPERTY) {
            nestedBean.setComplexProperty("parent", imdComplex.parentBean.getObj());
        }
        Object nestedComplexProperty = imdComplex.getNestedComplexProperty();
        if ((nestedComplexProperty instanceof LifeCycle) && NoAutoStartUtil.notMarkedWithNoAutoStart(nestedComplexProperty)) {
            ((LifeCycle) nestedComplexProperty).start();
        }
        Object o = mic.peekObject();
        if (o != imdComplex.getNestedComplexProperty()) {
            addError("The object on the top the of the stack is not the component pushed earlier.");
            return;
        }
        mic.popObject();
        switch (imdComplex.aggregationType) {
            case AS_COMPLEX_PROPERTY_COLLECTION:
                imdComplex.parentBean.addComplexProperty(model.getTag(), imdComplex.getNestedComplexProperty());
                return;
            case AS_COMPLEX_PROPERTY:
                imdComplex.parentBean.setComplexProperty(model.getTag(), imdComplex.getNestedComplexProperty());
                return;
            default:
                addError("Unexpected aggregationType " + String.valueOf(imdComplex.aggregationType));
                return;
        }
    }
}
