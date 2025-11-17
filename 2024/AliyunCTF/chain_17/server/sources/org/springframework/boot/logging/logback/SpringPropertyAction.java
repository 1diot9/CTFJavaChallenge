package org.springframework.boot.logging.logback;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringPropertyAction.class */
class SpringPropertyAction extends BaseModelAction {
    private static final String SOURCE_ATTRIBUTE = "source";
    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

    @Override // ch.qos.logback.core.joran.action.BaseModelAction
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        SpringPropertyModel model = new SpringPropertyModel();
        model.setName(attributes.getValue("name"));
        model.setSource(attributes.getValue(SOURCE_ATTRIBUTE));
        model.setScope(attributes.getValue("scope"));
        model.setDefaultValue(attributes.getValue(DEFAULT_VALUE_ATTRIBUTE));
        return model;
    }
}
