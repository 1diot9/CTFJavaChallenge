package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.ImportModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/ImportModelHandler.class */
public class ImportModelHandler extends ModelHandlerBase {
    public ImportModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new ImportModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<ImportModel> getSupportedModelClass() {
        return ImportModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        ImportModel importModel = (ImportModel) model;
        String className = importModel.getClassName();
        if (OptionHelper.isNullOrEmpty(className)) {
            addWarn("Empty className not allowed");
            return;
        }
        String stem = extractStem(className);
        if (stem == null) {
            addWarn("[" + className + "] could not be imported due to incorrect format");
        } else {
            intercon.addImport(stem, className);
        }
    }

    String extractStem(String className) {
        int lastDotIndex;
        if (className == null || (lastDotIndex = className.lastIndexOf(46)) == -1 || lastDotIndex + 1 == className.length()) {
            return null;
        }
        return className.substring(lastDotIndex + 1);
    }
}
