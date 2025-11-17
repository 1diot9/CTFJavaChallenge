package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.hook.DefaultShutdownHook;
import ch.qos.logback.core.hook.ShutdownHook;
import ch.qos.logback.core.hook.ShutdownHookBase;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ShutdownHookModel;
import ch.qos.logback.core.util.DynamicClassLoadingException;
import ch.qos.logback.core.util.IncompatibleClassException;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/ShutdownHookModelHandler.class */
public class ShutdownHookModelHandler extends ModelHandlerBase {
    static final String OLD_SHUTDOWN_HOOK_CLASSNAME = "ch.qos.logback.core.hook.DelayingShutdownHook";
    static final String DEFAULT_SHUTDOWN_HOOK_CLASSNAME = DefaultShutdownHook.class.getName();
    public static final String RENAME_WARNING = "ch.qos.logback.core.hook.DelayingShutdownHook was renamed as " + DEFAULT_SHUTDOWN_HOOK_CLASSNAME;
    boolean inError;
    ShutdownHook hook;

    public ShutdownHookModelHandler(Context context) {
        super(context);
        this.inError = false;
        this.hook = null;
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new ShutdownHookModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ShutdownHookModel> getSupportedModelClass() {
        return ShutdownHookModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) {
        String className;
        ShutdownHookModel shutdownHookModel = (ShutdownHookModel) model;
        String className2 = shutdownHookModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className2)) {
            className = DEFAULT_SHUTDOWN_HOOK_CLASSNAME;
            addInfo("Assuming className [" + className + "]");
        } else {
            className = mic.getImport(className2);
            if (className.equals(OLD_SHUTDOWN_HOOK_CLASSNAME)) {
                className = DEFAULT_SHUTDOWN_HOOK_CLASSNAME;
                addWarn(RENAME_WARNING);
                addWarn("Please use the new class name");
            }
        }
        addInfo("About to instantiate shutdown hook of type [" + className + "]");
        try {
            this.hook = (ShutdownHookBase) OptionHelper.instantiateByClassName(className, (Class<?>) ShutdownHookBase.class, this.context);
            this.hook.setContext(this.context);
            mic.pushObject(this.hook);
        } catch (DynamicClassLoadingException | IncompatibleClassException e) {
            addError("Could not create a shutdown hook of type [" + className + "].", e);
            this.inError = true;
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.hook) {
            addWarn("The object on the top the of the stack is not the hook object pushed earlier.");
            return;
        }
        Thread hookThread = new Thread(this.hook, "Logback shutdown hook [" + this.context.getName() + "]");
        addInfo("Registering shutdown hook with JVM runtime.");
        this.context.putObject(CoreConstants.SHUTDOWN_HOOK_THREAD, hookThread);
        Runtime.getRuntime().addShutdownHook(hookThread);
        mic.popObject();
    }
}
