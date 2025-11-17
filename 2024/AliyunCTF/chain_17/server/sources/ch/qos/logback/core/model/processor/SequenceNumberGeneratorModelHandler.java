package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.SequenceNumberGeneratorModel;
import ch.qos.logback.core.spi.SequenceNumberGenerator;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/SequenceNumberGeneratorModelHandler.class */
public class SequenceNumberGeneratorModelHandler extends ModelHandlerBase {
    SequenceNumberGenerator sequenceNumberGenerator;
    private boolean inError;

    public SequenceNumberGeneratorModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new SequenceNumberGeneratorModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<SequenceNumberGeneratorModel> getSupportedModelClass() {
        return SequenceNumberGeneratorModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        SequenceNumberGeneratorModel sequenceNumberGeneratorModel = (SequenceNumberGeneratorModel) model;
        String className = sequenceNumberGeneratorModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addWarn("Missing className. This should have been caught earlier.");
            this.inError = true;
            return;
        }
        String className2 = mic.getImport(className);
        try {
            addInfo("About to instantiate SequenceNumberGenerator of type [" + className2 + "]");
            this.sequenceNumberGenerator = (SequenceNumberGenerator) OptionHelper.instantiateByClassName(className2, (Class<?>) SequenceNumberGenerator.class, this.context);
            this.sequenceNumberGenerator.setContext(this.context);
            mic.pushObject(this.sequenceNumberGenerator);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create a SequenceNumberGenerator of type [" + className2 + "].", e);
            throw new ModelHandlerException(e);
        }
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.sequenceNumberGenerator) {
            addWarn("The object at the of the stack is not the hook pushed earlier.");
            return;
        }
        mic.popObject();
        addInfo("Registering " + String.valueOf(o) + " with context.");
        this.context.setSequenceNumberGenerator(this.sequenceNumberGenerator);
    }
}
