package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.NamedModel;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/ContextNameModel.class */
public class ContextNameModel extends NamedModel {
    private static final long serialVersionUID = -1635653921915985666L;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.NamedModel, ch.qos.logback.core.model.Model
    public ContextNameModel makeNewInstance() {
        return new ContextNameModel();
    }
}
