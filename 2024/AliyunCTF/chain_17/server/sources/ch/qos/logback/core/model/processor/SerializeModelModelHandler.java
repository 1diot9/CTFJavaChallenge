package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.SerializeModelModel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/SerializeModelModelHandler.class */
public class SerializeModelModelHandler extends ModelHandlerBase {
    public SerializeModelModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new SerializeModelModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext modelInterpretationContext, Model model) throws ModelHandlerException {
        String fileStr;
        Object configuratorHint = modelInterpretationContext.getConfiguratorHint();
        if (configuratorHint != null && configuratorHint.getClass().getName().equals("ch.qos.logback.classic.joran.SerializedModelConfigurator")) {
            addInfo("Skipping model serialization as calling configurator is already model based.");
            return;
        }
        if (!(model instanceof SerializeModelModel)) {
            addWarn("Model parameter is not of type SerializeModelModel. Skipping serialization of model structure");
            return;
        }
        SerializeModelModel serializeModelModel = (SerializeModelModel) model;
        Model topModel = modelInterpretationContext.getTopModel();
        if (topModel == null) {
            addWarn("Could not find top most model. Skipping serialization of model structure.");
            return;
        }
        String fileStr2 = serializeModelModel.getFile();
        if (fileStr2 == null) {
            DateTimeFormatter dft = DateTimeFormatter.ofPattern(CoreConstants.FILE_TIMESTAMP_PATTERN);
            Instant now = Instant.now();
            String timestamp = dft.format(now);
            fileStr = "logback-" + timestamp + ".scmo";
            addInfo("For model serialization, using default file destination [" + fileStr + "]");
        } else {
            fileStr = modelInterpretationContext.subst(fileStr2);
        }
        writeModel(fileStr, topModel);
    }

    private void writeModel(String fileStr, Model firstModel) {
        addInfo("Serializing model to file [" + fileStr + "]");
        try {
            FileOutputStream fos = new FileOutputStream(fileStr);
            try {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(firstModel);
                oos.flush();
                oos.close();
                fos.close();
            } finally {
            }
        } catch (IOException e) {
            addError("IO failure while serializing Model [" + fileStr + "]");
        }
    }
}
