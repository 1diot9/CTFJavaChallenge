package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.ImportModel;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ImportAction.class */
public class ImportAction extends BaseModelAction {
    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, intercon, name, attributes);
        pv.validateClassAttribute();
        return pv.isValid();
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String localName, Attributes attributes) {
        ImportModel importModel = new ImportModel();
        importModel.setClassName(attributes.getValue("class"));
        return importModel;
    }
}
