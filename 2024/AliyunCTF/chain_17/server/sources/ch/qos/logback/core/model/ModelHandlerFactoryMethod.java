package ch.qos.logback.core.model;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/ModelHandlerFactoryMethod.class */
public interface ModelHandlerFactoryMethod {
    ModelHandlerBase make(Context context, ModelInterpretationContext modelInterpretationContext);
}
