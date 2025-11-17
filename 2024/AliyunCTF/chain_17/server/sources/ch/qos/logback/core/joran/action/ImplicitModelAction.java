package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ErrorCodes;
import java.util.Stack;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ImplicitModelAction.class */
public class ImplicitModelAction extends Action {
    Stack<ImplicitModel> currentImplicitModelStack = new Stack<>();

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) throws ActionException {
        ImplicitModel currentImplicitModel = new ImplicitModel();
        currentImplicitModel.setTag(name);
        String className = attributes.getValue("class");
        currentImplicitModel.setClassName(className);
        this.currentImplicitModelStack.push(currentImplicitModel);
        interpretationContext.pushModel(currentImplicitModel);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void body(SaxEventInterpretationContext ec, String body) {
        ImplicitModel implicitModel = this.currentImplicitModelStack.peek();
        implicitModel.addText(body);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(SaxEventInterpretationContext interpretationContext, String name) throws ActionException {
        ImplicitModel implicitModel = this.currentImplicitModelStack.peek();
        Model otherImplicitModel = interpretationContext.popModel();
        if (implicitModel != otherImplicitModel) {
            addError(String.valueOf(implicitModel) + " does not match " + String.valueOf(otherImplicitModel));
            return;
        }
        Model parentModel = interpretationContext.peekModel();
        if (parentModel != null) {
            parentModel.addSubModel(implicitModel);
        } else {
            addWarn(ErrorCodes.PARENT_MODEL_NOT_FOUND);
            addWarn(ErrorCodes.SKIPPING_IMPLICIT_MODEL_ADDITION);
        }
        this.currentImplicitModelStack.pop();
    }
}
