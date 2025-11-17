package ch.qos.logback.core.model;

import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;

@PhaseIndicator(phase = ProcessingPhase.SECOND)
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/AppenderModel.class */
public class AppenderModel extends NamedComponentModel {
    private static final long serialVersionUID = 1096234203123945432L;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedComponentModel, ch.qos.logback.core.model.ComponentModel, ch.qos.logback.core.model.Model
    public AppenderModel makeNewInstance() {
        return new AppenderModel();
    }
}
