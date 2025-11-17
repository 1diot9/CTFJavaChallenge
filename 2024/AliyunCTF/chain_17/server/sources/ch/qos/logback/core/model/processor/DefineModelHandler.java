package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.DefineModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/DefineModelHandler.class */
public class DefineModelHandler extends ModelHandlerBase {
    boolean inError;
    PropertyDefiner definer;
    String propertyName;
    ActionUtil.Scope scope;

    public DefineModelHandler(Context context) {
        super(context);
    }

    public static DefineModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new DefineModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<DefineModel> getSupportedModelClass() {
        return DefineModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext interpretationContext, Model model) throws ModelHandlerException {
        this.definer = null;
        this.inError = false;
        this.propertyName = null;
        DefineModel defineModel = (DefineModel) model;
        this.propertyName = defineModel.getName();
        String scopeStr = defineModel.getScopeStr();
        this.scope = ActionUtil.stringToScope(scopeStr);
        if (OptionHelper.isNullOrEmpty(this.propertyName)) {
            addError("Missing property name for property definer. Near [" + model.getTag() + "] line " + model.getLineNumber());
            this.inError = true;
        }
        String className = defineModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addError("Missing class name for property definer. Near [" + model.getTag() + "] line " + model.getLineNumber());
            this.inError = true;
        } else {
            className = interpretationContext.getImport(className);
        }
        if (this.inError) {
            return;
        }
        try {
            addInfo("About to instantiate property definer of type [" + className + "]");
            this.definer = (PropertyDefiner) OptionHelper.instantiateByClassName(className, (Class<?>) PropertyDefiner.class, this.context);
            this.definer.setContext(this.context);
            interpretationContext.pushObject(this.definer);
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create an PropertyDefiner of type [" + className + "].", oops);
            throw new ModelHandlerException(oops);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext interpretationContext, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = interpretationContext.peekObject();
        if (o != this.definer) {
            addWarn("The object at the of the stack is not the property definer for property named [" + this.propertyName + "] pushed earlier.");
            return;
        }
        interpretationContext.popObject();
        if (this.definer instanceof LifeCycle) {
            ((LifeCycle) this.definer).start();
        }
        String propertyValue = this.definer.getPropertyValue();
        if (propertyValue != null) {
            addInfo("Setting property " + this.propertyName + "=" + propertyValue + " in scope " + String.valueOf(this.scope));
            ActionUtil.setProperty(interpretationContext, this.propertyName, propertyValue, this.scope);
        }
    }
}
