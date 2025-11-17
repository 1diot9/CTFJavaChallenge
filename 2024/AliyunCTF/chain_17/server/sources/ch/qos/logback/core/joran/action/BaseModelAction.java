package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/BaseModelAction.class */
public abstract class BaseModelAction extends Action {
    Model parentModel;
    Model currentModel;
    boolean inError = false;

    protected abstract Model buildCurrentModel(SaxEventInterpretationContext saxEventInterpretationContext, String str, Attributes attributes);

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext saxEventInterpretationContext, String name, Attributes attributes) throws ActionException {
        this.parentModel = null;
        this.inError = false;
        if (!validPreconditions(saxEventInterpretationContext, name, attributes)) {
            this.inError = true;
            return;
        }
        this.currentModel = buildCurrentModel(saxEventInterpretationContext, name, attributes);
        this.currentModel.setTag(name);
        if (!saxEventInterpretationContext.isModelStackEmpty()) {
            this.parentModel = saxEventInterpretationContext.peekModel();
        }
        int lineNumber = getLineNumber(saxEventInterpretationContext);
        this.currentModel.setLineNumber(lineNumber);
        saxEventInterpretationContext.pushModel(this.currentModel);
    }

    protected boolean validPreconditions(SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        return true;
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void body(SaxEventInterpretationContext ec, String body) throws ActionException {
        if (this.currentModel == null) {
            throw new ActionException("current model is null. Is <configuration> element missing?");
        }
        this.currentModel.addText(body);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext saxEventInterpretationContext, String name) throws ActionException {
        if (this.inError) {
            return;
        }
        Model m = saxEventInterpretationContext.peekModel();
        if (m != this.currentModel) {
            addWarn("The object " + String.valueOf(m) + "] at the top of the stack differs from the model [" + this.currentModel.idString() + "] pushed earlier.");
            addWarn("This is wholly unexpected.");
        }
        if (this.parentModel != null) {
            this.parentModel.addSubModel(this.currentModel);
            saxEventInterpretationContext.popModel();
        }
    }
}
