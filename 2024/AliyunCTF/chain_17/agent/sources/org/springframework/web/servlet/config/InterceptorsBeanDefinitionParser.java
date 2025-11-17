package org.springframework.web.servlet.config;

import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/InterceptorsBeanDefinitionParser.class */
class InterceptorsBeanDefinitionParser implements BeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext context) {
        Object interceptorBean;
        context.pushContainingComponent(new CompositeComponentDefinition(element.getTagName(), context.extractSource(element)));
        RuntimeBeanReference pathMatcherRef = null;
        if (element.hasAttribute("path-matcher")) {
            pathMatcherRef = new RuntimeBeanReference(element.getAttribute("path-matcher"));
        }
        List<Element> interceptors = DomUtils.getChildElementsByTagName(element, "bean", "ref", "interceptor");
        for (Element interceptor : interceptors) {
            RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition((Class<?>) MappedInterceptor.class);
            mappedInterceptorDef.setSource(context.extractSource(interceptor));
            mappedInterceptorDef.setRole(2);
            ManagedList<String> includePatterns = null;
            ManagedList<String> excludePatterns = null;
            if ("interceptor".equals(interceptor.getLocalName())) {
                includePatterns = getIncludePatterns(interceptor, "mapping");
                excludePatterns = getIncludePatterns(interceptor, "exclude-mapping");
                Element beanElem = DomUtils.getChildElementsByTagName(interceptor, "bean", "ref").get(0);
                interceptorBean = context.getDelegate().parsePropertySubElement(beanElem, null);
            } else {
                interceptorBean = context.getDelegate().parsePropertySubElement(interceptor, null);
            }
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, includePatterns);
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, excludePatterns);
            mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(2, interceptorBean);
            if (pathMatcherRef != null) {
                mappedInterceptorDef.getPropertyValues().add("pathMatcher", pathMatcherRef);
            }
            String beanName = context.getReaderContext().registerWithGeneratedName(mappedInterceptorDef);
            context.registerComponent(new BeanComponentDefinition(mappedInterceptorDef, beanName));
        }
        context.popAndRegisterContainingComponent();
        return null;
    }

    private ManagedList<String> getIncludePatterns(Element interceptor, String elementName) {
        List<Element> paths = DomUtils.getChildElementsByTagName(interceptor, elementName);
        ManagedList<String> patterns = new ManagedList<>(paths.size());
        for (Element path : paths) {
            patterns.add(path.getAttribute("path"));
        }
        return patterns;
    }
}
