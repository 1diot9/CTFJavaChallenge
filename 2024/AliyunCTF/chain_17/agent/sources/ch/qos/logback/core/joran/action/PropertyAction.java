package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.PropertyModel;
import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/PropertyAction.class */
public class PropertyAction extends BaseModelAction {
    static final String RESOURCE_ATTRIBUTE = "resource";

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected boolean validPreconditions(SaxEventInterpretationContext interpretationContext, String localName, Attributes attributes) {
        if ("substitutionProperty".equals(localName)) {
            addWarn("[substitutionProperty] element has been deprecated. Please use the [variable] element instead.");
            return true;
        }
        return true;
    }

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setName(attributes.getValue("name"));
        propertyModel.setValue(attributes.getValue("value"));
        propertyModel.setScopeStr(attributes.getValue("scope"));
        propertyModel.setFile(attributes.getValue("file"));
        propertyModel.setResource(attributes.getValue("resource"));
        return propertyModel;
    }
}
