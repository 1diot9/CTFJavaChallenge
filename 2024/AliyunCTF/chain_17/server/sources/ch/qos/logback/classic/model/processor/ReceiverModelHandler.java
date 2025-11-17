package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/processor/ReceiverModelHandler.class */
public class ReceiverModelHandler extends ModelHandlerBase {
    private ReceiverBase receiver;
    private boolean inError;

    public ReceiverModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new ReceiverModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ReceiverModel> getSupportedModelClass() {
        return ReceiverModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        ReceiverModel receiverModel = (ReceiverModel) model;
        String className = receiverModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addError("Missing class name for receiver. ");
            this.inError = true;
            return;
        }
        String className2 = mic.getImport(className);
        try {
            addInfo("About to instantiate receiver of type [" + className2 + "]");
            this.receiver = (ReceiverBase) OptionHelper.instantiateByClassName(className2, (Class<?>) ReceiverBase.class, this.context);
            this.receiver.setContext(this.context);
            mic.pushObject(this.receiver);
        } catch (Exception ex) {
            this.inError = true;
            addError("Could not create a receiver of type [" + className2 + "].", ex);
            throw new ModelHandlerException(ex);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.receiver) {
            addWarn("The object at the of the stack is not the receiver pushed earlier.");
            return;
        }
        mic.popObject();
        addInfo("Registering receiver with context.");
        mic.getContext().register(this.receiver);
        this.receiver.start();
    }
}
