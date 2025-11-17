package org.springframework.context.config;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/config/AbstractPropertyLoadingBeanDefinitionParser.class */
abstract class AbstractPropertyLoadingBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    protected boolean shouldGenerateId() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    public void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String location = element.getAttribute("location");
        if (StringUtils.hasLength(location)) {
            String[] locations = StringUtils.commaDelimitedListToStringArray(parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location));
            builder.addPropertyValue("locations", locations);
        }
        String propertiesRef = element.getAttribute("properties-ref");
        if (StringUtils.hasLength(propertiesRef)) {
            builder.addPropertyReference(JsonEncoder.CONTEXT_PROPERTIES_ATTR_NAME, propertiesRef);
        }
        String fileEncoding = element.getAttribute("file-encoding");
        if (StringUtils.hasLength(fileEncoding)) {
            builder.addPropertyValue("fileEncoding", fileEncoding);
        }
        String order = element.getAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE);
        if (StringUtils.hasLength(order)) {
            builder.addPropertyValue(AbstractBeanDefinition.ORDER_ATTRIBUTE, Integer.valueOf(order));
        }
        builder.addPropertyValue("ignoreResourceNotFound", Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));
        builder.addPropertyValue("localOverride", Boolean.valueOf(element.getAttribute("local-override")));
        builder.setRole(2);
    }
}
