package org.springframework.context.annotation;

import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ComponentScanBeanDefinitionParser.class */
public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    private static final String RESOURCE_PATTERN_ATTRIBUTE = "resource-pattern";
    private static final String USE_DEFAULT_FILTERS_ATTRIBUTE = "use-default-filters";
    private static final String ANNOTATION_CONFIG_ATTRIBUTE = "annotation-config";
    private static final String NAME_GENERATOR_ATTRIBUTE = "name-generator";
    private static final String SCOPE_RESOLVER_ATTRIBUTE = "scope-resolver";
    private static final String SCOPED_PROXY_ATTRIBUTE = "scoped-proxy";
    private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";
    private static final String INCLUDE_FILTER_ELEMENT = "include-filter";
    private static final String FILTER_TYPE_ATTRIBUTE = "type";
    private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String basePackage = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
        String[] basePackages = StringUtils.tokenizeToStringArray(parserContext.getReaderContext().getEnvironment().resolvePlaceholders(basePackage), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        ClassPathBeanDefinitionScanner scanner = configureScanner(parserContext, element);
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);
        return null;
    }

    protected ClassPathBeanDefinitionScanner configureScanner(ParserContext parserContext, Element element) {
        boolean useDefaultFilters = true;
        if (element.hasAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE)) {
            useDefaultFilters = Boolean.parseBoolean(element.getAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE));
        }
        ClassPathBeanDefinitionScanner scanner = createScanner(parserContext.getReaderContext(), useDefaultFilters);
        scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
        scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());
        if (element.hasAttribute(RESOURCE_PATTERN_ATTRIBUTE)) {
            scanner.setResourcePattern(element.getAttribute(RESOURCE_PATTERN_ATTRIBUTE));
        }
        try {
            parseBeanNameGenerator(element, scanner);
        } catch (Exception ex) {
            parserContext.getReaderContext().error(ex.getMessage(), parserContext.extractSource(element), ex.getCause());
        }
        try {
            parseScope(element, scanner);
        } catch (Exception ex2) {
            parserContext.getReaderContext().error(ex2.getMessage(), parserContext.extractSource(element), ex2.getCause());
        }
        parseTypeFilters(element, scanner, parserContext);
        return scanner;
    }

    protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
        return new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters, readerContext.getEnvironment(), readerContext.getResourceLoader());
    }

    protected void registerComponents(XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element) {
        Object source = readerContext.extractSource(element);
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
        for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
        }
        boolean annotationConfig = true;
        if (element.hasAttribute(ANNOTATION_CONFIG_ATTRIBUTE)) {
            annotationConfig = Boolean.parseBoolean(element.getAttribute(ANNOTATION_CONFIG_ATTRIBUTE));
        }
        if (annotationConfig) {
            Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
            for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
                compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
            }
        }
        readerContext.fireComponentRegistered(compositeDef);
    }

    protected void parseBeanNameGenerator(Element element, ClassPathBeanDefinitionScanner scanner) {
        if (element.hasAttribute(NAME_GENERATOR_ATTRIBUTE)) {
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) instantiateUserDefinedStrategy(element.getAttribute(NAME_GENERATOR_ATTRIBUTE), BeanNameGenerator.class, scanner.getResourceLoader().getClassLoader());
            scanner.setBeanNameGenerator(beanNameGenerator);
        }
    }

    protected void parseScope(Element element, ClassPathBeanDefinitionScanner scanner) {
        if (element.hasAttribute(SCOPE_RESOLVER_ATTRIBUTE)) {
            if (element.hasAttribute(SCOPED_PROXY_ATTRIBUTE)) {
                throw new IllegalArgumentException("Cannot define both 'scope-resolver' and 'scoped-proxy' on <component-scan> tag");
            }
            ScopeMetadataResolver scopeMetadataResolver = (ScopeMetadataResolver) instantiateUserDefinedStrategy(element.getAttribute(SCOPE_RESOLVER_ATTRIBUTE), ScopeMetadataResolver.class, scanner.getResourceLoader().getClassLoader());
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }
        if (element.hasAttribute(SCOPED_PROXY_ATTRIBUTE)) {
            String mode = element.getAttribute(SCOPED_PROXY_ATTRIBUTE);
            boolean z = -1;
            switch (mode.hashCode()) {
                case -2110678425:
                    if (mode.equals("targetClass")) {
                        z = false;
                        break;
                    }
                    break;
                case -1598539174:
                    if (mode.equals("interfaces")) {
                        z = true;
                        break;
                    }
                    break;
                case 3521:
                    if (mode.equals("no")) {
                        z = 2;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    scanner.setScopedProxyMode(ScopedProxyMode.TARGET_CLASS);
                    return;
                case true:
                    scanner.setScopedProxyMode(ScopedProxyMode.INTERFACES);
                    return;
                case true:
                    scanner.setScopedProxyMode(ScopedProxyMode.NO);
                    return;
                default:
                    throw new IllegalArgumentException("scoped-proxy only supports 'no', 'interfaces' and 'targetClass'");
            }
        }
    }

    protected void parseTypeFilters(Element element, ClassPathBeanDefinitionScanner scanner, ParserContext parserContext) {
        ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
                String localName = parserContext.getDelegate().getLocalName(node);
                try {
                    if (INCLUDE_FILTER_ELEMENT.equals(localName)) {
                        TypeFilter typeFilter = createTypeFilter((Element) node, classLoader, parserContext);
                        scanner.addIncludeFilter(typeFilter);
                    } else if (EXCLUDE_FILTER_ELEMENT.equals(localName)) {
                        TypeFilter typeFilter2 = createTypeFilter((Element) node, classLoader, parserContext);
                        scanner.addExcludeFilter(typeFilter2);
                    }
                } catch (ClassNotFoundException ex) {
                    parserContext.getReaderContext().warning("Ignoring non-present type filter class: " + ex, parserContext.extractSource(element));
                } catch (Exception ex2) {
                    parserContext.getReaderContext().error(ex2.getMessage(), parserContext.extractSource(element), ex2.getCause());
                }
            }
        }
    }

    protected TypeFilter createTypeFilter(Element element, @Nullable ClassLoader classLoader, ParserContext parserContext) throws ClassNotFoundException {
        String filterType = element.getAttribute("type");
        String expression = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(element.getAttribute(FILTER_EXPRESSION_ATTRIBUTE));
        boolean z = -1;
        switch (filterType.hashCode()) {
            case -1555043537:
                if (filterType.equals("annotation")) {
                    z = false;
                    break;
                }
                break;
            case -1349088399:
                if (filterType.equals("custom")) {
                    z = 4;
                    break;
                }
                break;
            case -707561710:
                if (filterType.equals("aspectj")) {
                    z = 2;
                    break;
                }
                break;
            case 108392519:
                if (filterType.equals("regex")) {
                    z = 3;
                    break;
                }
                break;
            case 1025902281:
                if (filterType.equals("assignable")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return new AnnotationTypeFilter(ClassUtils.forName(expression, classLoader));
            case true:
                return new AssignableTypeFilter(ClassUtils.forName(expression, classLoader));
            case true:
                return new AspectJTypeFilter(expression, classLoader);
            case true:
                return new RegexPatternTypeFilter(Pattern.compile(expression));
            case true:
                Class<?> filterClass = ClassUtils.forName(expression, classLoader);
                if (!TypeFilter.class.isAssignableFrom(filterClass)) {
                    throw new IllegalArgumentException("Class is not assignable to [" + TypeFilter.class.getName() + "]: " + expression);
                }
                return (TypeFilter) BeanUtils.instantiateClass(filterClass);
            default:
                throw new IllegalArgumentException("Unsupported filter type: " + filterType);
        }
    }

    private Object instantiateUserDefinedStrategy(String className, Class<?> strategyType, @Nullable ClassLoader classLoader) {
        try {
            Object result = ReflectionUtils.accessibleConstructor(ClassUtils.forName(className, classLoader), new Class[0]).newInstance(new Object[0]);
            if (!strategyType.isAssignableFrom(result.getClass())) {
                throw new IllegalArgumentException("Provided class name must be an implementation of " + strategyType);
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class [" + className + "] for strategy [" + strategyType.getName() + "] not found", ex);
        } catch (Throwable ex2) {
            throw new IllegalArgumentException("Unable to instantiate class [" + className + "] for strategy [" + strategyType.getName() + "]: a zero-argument constructor is required", ex2);
        }
    }
}
