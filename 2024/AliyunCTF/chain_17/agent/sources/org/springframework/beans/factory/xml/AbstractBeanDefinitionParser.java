package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/AbstractBeanDefinitionParser.class */
public abstract class AbstractBeanDefinitionParser implements BeanDefinitionParser {
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";

    @Nullable
    protected abstract AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext);

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public final BeanDefinition parse(Element element, ParserContext parserContext) {
        AbstractBeanDefinition definition = parseInternal(element, parserContext);
        if (definition != null && !parserContext.isNested()) {
            try {
                String id = resolveId(element, definition, parserContext);
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error("Id is required for element '" + parserContext.getDelegate().getLocalName(element) + "' when used as a top-level tag", element);
                }
                String[] aliases = null;
                if (shouldParseNameAsAliases()) {
                    String name = element.getAttribute("name");
                    if (StringUtils.hasLength(name)) {
                        aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
                    }
                }
                BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
                registerBeanDefinition(holder, parserContext.getRegistry());
                if (shouldFireEvents()) {
                    BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
                    postProcessComponentDefinition(componentDefinition);
                    parserContext.registerComponent(componentDefinition);
                }
            } catch (BeanDefinitionStoreException ex) {
                String msg = ex.getMessage();
                parserContext.getReaderContext().error(msg != null ? msg : ex.toString(), element);
                return null;
            }
        }
        return definition;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (shouldGenerateId()) {
            return parserContext.getReaderContext().generateBeanName(definition);
        }
        String id = element.getAttribute("id");
        if (!StringUtils.hasText(id) && shouldGenerateIdAsFallback()) {
            id = parserContext.getReaderContext().generateBeanName(definition);
        }
        return id;
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
    }

    protected boolean shouldGenerateId() {
        return false;
    }

    protected boolean shouldGenerateIdAsFallback() {
        return false;
    }

    protected boolean shouldParseNameAsAliases() {
        return true;
    }

    protected boolean shouldFireEvents() {
        return true;
    }

    protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
    }
}
