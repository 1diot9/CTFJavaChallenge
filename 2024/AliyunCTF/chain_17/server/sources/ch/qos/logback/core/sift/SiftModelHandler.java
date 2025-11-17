package ch.qos.logback.core.sift;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import java.util.stream.Stream;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/SiftModelHandler.class */
public class SiftModelHandler extends ModelHandlerBase {
    static final String ONE_AND_ONLY_ONE_URL = "http://logback.qos.ch/codes.html#1andOnly1";

    public SiftModelHandler(Context context) {
        super(context);
    }

    public static SiftModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new SiftModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<SiftModel> getSupportedModelClass() {
        return SiftModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        SiftModel siftModel = (SiftModel) model;
        siftModel.markAsSkipped();
        long appenderModelCount = computeAppenderModelCount(siftModel);
        if (appenderModelCount == 0) {
            addError("No nested appenders found within the <sift> element in SiftingAppender.");
            return;
        }
        if (appenderModelCount > 1) {
            addError("Only and only one appender can be nested the <sift> element in SiftingAppender. See also http://logback.qos.ch/codes.html#1andOnly1");
            return;
        }
        Object o = mic.peekObject();
        if (o instanceof SiftingAppenderBase) {
            SiftingAppenderBase sa = (SiftingAppenderBase) o;
            String key = sa.getDiscriminatorKey();
            AppenderFactoryUsingSiftModel afusm = new AppenderFactoryUsingSiftModel(mic, siftModel, key);
            sa.setAppenderFactory(afusm);
            return;
        }
        addError("Unexpected object " + String.valueOf(o));
    }

    private long computeAppenderModelCount(SiftModel siftModel) {
        Stream<Model> stream = siftModel.getSubModels().stream();
        long count = stream.filter(m -> {
            return m instanceof AppenderModel;
        }).count();
        return count;
    }
}
