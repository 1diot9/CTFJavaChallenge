package org.springframework.web.servlet.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/FreeMarkerConfigurerBeanDefinitionParser.class */
public class FreeMarkerConfigurerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcFreeMarkerConfigurer";

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    public String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<Element> childElements = DomUtils.getChildElementsByTagName(element, "template-loader-path");
        if (!childElements.isEmpty()) {
            List<String> locations = new ArrayList<>(childElements.size());
            for (Element childElement : childElements) {
                locations.add(childElement.getAttribute("location"));
            }
            builder.addPropertyValue("templateLoaderPaths", StringUtils.toStringArray(locations));
        }
    }
}
