package ch.qos.logback.core.sift;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/SiftProcessor.class */
public class SiftProcessor<E> extends DefaultProcessor {
    public SiftProcessor(Context context, ModelInterpretationContext mic) {
        super(mic.getContext(), mic);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModelInterpretationContext getModelInterpretationContext() {
        return this.mic;
    }
}
