package org.springframework.context.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/config/PropertyPlaceholderBeanDefinitionParser.class */
class PropertyPlaceholderBeanDefinitionParser extends AbstractPropertyLoadingBeanDefinitionParser {
    private static final String SYSTEM_PROPERTIES_MODE_ATTRIBUTE = "system-properties-mode";
    private static final String SYSTEM_PROPERTIES_MODE_DEFAULT = "ENVIRONMENT";

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected Class<?> getBeanClass(Element element) {
        if (SYSTEM_PROPERTIES_MODE_DEFAULT.equals(element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE))) {
            return PropertySourcesPlaceholderConfigurer.class;
        }
        return PropertyPlaceholderConfigurer.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.config.AbstractPropertyLoadingBeanDefinitionParser, org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    public void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue("ignoreUnresolvablePlaceholders", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
        String systemPropertiesModeName = element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE);
        if (StringUtils.hasLength(systemPropertiesModeName) && !systemPropertiesModeName.equals(SYSTEM_PROPERTIES_MODE_DEFAULT)) {
            builder.addPropertyValue("systemPropertiesModeName", "SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName);
        }
        if (element.hasAttribute("value-separator")) {
            builder.addPropertyValue("valueSeparator", element.getAttribute("value-separator"));
        }
        if (element.hasAttribute("trim-values")) {
            builder.addPropertyValue("trimValues", element.getAttribute("trim-values"));
        }
        if (element.hasAttribute("null-value")) {
            builder.addPropertyValue("nullValue", element.getAttribute("null-value"));
        }
    }
}
