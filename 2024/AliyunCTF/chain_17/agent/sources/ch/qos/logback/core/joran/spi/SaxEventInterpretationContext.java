package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import java.util.Stack;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/SaxEventInterpretationContext.class */
public class SaxEventInterpretationContext extends ContextAwareBase implements PropertyContainer {
    Stack<Model> modelStack;
    SaxEventInterpreter saxEventInterpreter;

    public SaxEventInterpretationContext(Context context, SaxEventInterpreter saxEventInterpreter) {
        this.context = context;
        this.saxEventInterpreter = saxEventInterpreter;
        this.modelStack = new Stack<>();
    }

    public SaxEventInterpreter getSaxEventInterpreter() {
        return this.saxEventInterpreter;
    }

    public Model peekModel() {
        if (this.modelStack.isEmpty()) {
            return null;
        }
        return this.modelStack.peek();
    }

    public void pushModel(Model m) {
        this.modelStack.push(m);
    }

    public boolean isModelStackEmpty() {
        return this.modelStack.isEmpty();
    }

    public Model popModel() {
        return this.modelStack.pop();
    }

    public Stack<Model> getCopyOfModelStack() {
        Stack<Model> copy = new Stack<>();
        copy.addAll(this.modelStack);
        return copy;
    }

    @Override // ch.qos.logback.core.spi.PropertyContainer
    public String getProperty(String key) {
        return this.context.getProperty(key);
    }

    @Override // ch.qos.logback.core.spi.PropertyContainer
    public Map<String, String> getCopyOfPropertyMap() {
        return null;
    }

    public String subst(String value) {
        if (value == null) {
            return null;
        }
        try {
            return OptionHelper.substVars(value, this, this.context);
        } catch (ScanException | IllegalArgumentException e) {
            addError("Problem while parsing [" + value + "]", e);
            return value;
        }
    }
}
