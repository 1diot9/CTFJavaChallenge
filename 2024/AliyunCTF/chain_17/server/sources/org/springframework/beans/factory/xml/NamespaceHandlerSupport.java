package org.springframework.beans.factory.xml;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.lang.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/NamespaceHandlerSupport.class */
public abstract class NamespaceHandlerSupport implements NamespaceHandler {
    private final Map<String, BeanDefinitionParser> parsers = new HashMap();
    private final Map<String, BeanDefinitionDecorator> decorators = new HashMap();
    private final Map<String, BeanDefinitionDecorator> attributeDecorators = new HashMap();

    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionParser parser = findParserForElement(element, parserContext);
        if (parser != null) {
            return parser.parse(element, parserContext);
        }
        return null;
    }

    @Nullable
    private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
        String localName = parserContext.getDelegate().getLocalName(element);
        BeanDefinitionParser parser = this.parsers.get(localName);
        if (parser == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
        }
        return parser;
    }

    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    @Nullable
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        BeanDefinitionDecorator decorator = findDecoratorForNode(node, parserContext);
        if (decorator != null) {
            return decorator.decorate(node, definition, parserContext);
        }
        return null;
    }

    @Nullable
    private BeanDefinitionDecorator findDecoratorForNode(Node node, ParserContext parserContext) {
        BeanDefinitionDecorator decorator = null;
        String localName = parserContext.getDelegate().getLocalName(node);
        if (node instanceof Element) {
            decorator = this.decorators.get(localName);
        } else if (node instanceof Attr) {
            decorator = this.attributeDecorators.get(localName);
        } else {
            parserContext.getReaderContext().fatal("Cannot decorate based on Nodes of type [" + node.getClass().getName() + "]", node);
        }
        if (decorator == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionDecorator for " + (node instanceof Element ? "element" : BeanDefinitionParserDelegate.QUALIFIER_ATTRIBUTE_ELEMENT) + " [" + localName + "]", node);
        }
        return decorator;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser) {
        this.parsers.put(elementName, parser);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void registerBeanDefinitionDecorator(String elementName, BeanDefinitionDecorator dec) {
        this.decorators.put(elementName, dec);
    }

    protected final void registerBeanDefinitionDecoratorForAttribute(String attrName, BeanDefinitionDecorator dec) {
        this.attributeDecorators.put(attrName, dec);
    }
}
