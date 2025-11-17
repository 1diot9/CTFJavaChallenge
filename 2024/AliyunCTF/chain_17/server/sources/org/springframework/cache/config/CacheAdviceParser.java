package org.springframework.cache.config;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.cache.interceptor.NameMatchCacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/config/CacheAdviceParser.class */
public class CacheAdviceParser extends AbstractSingleBeanDefinitionParser {
    private static final String CACHEABLE_ELEMENT = "cacheable";
    private static final String CACHE_EVICT_ELEMENT = "cache-evict";
    private static final String CACHE_PUT_ELEMENT = "cache-put";
    private static final String METHOD_ATTRIBUTE = "method";
    private static final String DEFS_ELEMENT = "caching";

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected Class<?> getBeanClass(Element element) {
        return CacheInterceptor.class;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference("cacheManager", CacheNamespaceHandler.extractCacheManager(element));
        CacheNamespaceHandler.parseKeyGenerator(element, builder.getBeanDefinition());
        List<Element> cacheDefs = DomUtils.getChildElementsByTagName(element, DEFS_ELEMENT);
        if (!cacheDefs.isEmpty()) {
            List<RootBeanDefinition> attributeSourceDefinitions = parseDefinitionsSources(cacheDefs, parserContext);
            builder.addPropertyValue("cacheOperationSources", attributeSourceDefinitions);
        } else {
            builder.addPropertyValue("cacheOperationSources", new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource"));
        }
    }

    private List<RootBeanDefinition> parseDefinitionsSources(List<Element> definitions, ParserContext parserContext) {
        ManagedList<RootBeanDefinition> defs = new ManagedList<>(definitions.size());
        for (Element element : definitions) {
            defs.add(parseDefinitionSource(element, parserContext));
        }
        return defs;
    }

    private RootBeanDefinition parseDefinitionSource(Element definition, ParserContext parserContext) {
        Props prop = new Props(definition);
        ManagedMap<TypedStringValue, Collection<CacheOperation>> cacheOpMap = new ManagedMap<>();
        cacheOpMap.setSource(parserContext.extractSource(definition));
        List<Element> cacheableCacheMethods = DomUtils.getChildElementsByTagName(definition, CACHEABLE_ELEMENT);
        for (Element opElement : cacheableCacheMethods) {
            String name = prop.merge(opElement, parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(opElement));
            CacheableOperation.Builder builder = (CacheableOperation.Builder) prop.merge(opElement, parserContext.getReaderContext(), new CacheableOperation.Builder());
            builder.setUnless(getAttributeValue(opElement, "unless", ""));
            builder.setSync(Boolean.parseBoolean(getAttributeValue(opElement, "sync", "false")));
            Collection<CacheOperation> col = (Collection) cacheOpMap.computeIfAbsent(nameHolder, k -> {
                return new ArrayList(2);
            });
            col.add(builder.build());
        }
        List<Element> evictCacheMethods = DomUtils.getChildElementsByTagName(definition, CACHE_EVICT_ELEMENT);
        for (Element opElement2 : evictCacheMethods) {
            String name2 = prop.merge(opElement2, parserContext.getReaderContext());
            TypedStringValue nameHolder2 = new TypedStringValue(name2);
            nameHolder2.setSource(parserContext.extractSource(opElement2));
            CacheEvictOperation.Builder builder2 = (CacheEvictOperation.Builder) prop.merge(opElement2, parserContext.getReaderContext(), new CacheEvictOperation.Builder());
            String wide = opElement2.getAttribute("all-entries");
            if (StringUtils.hasText(wide)) {
                builder2.setCacheWide(Boolean.parseBoolean(wide.trim()));
            }
            String after = opElement2.getAttribute("before-invocation");
            if (StringUtils.hasText(after)) {
                builder2.setBeforeInvocation(Boolean.parseBoolean(after.trim()));
            }
            Collection<CacheOperation> col2 = (Collection) cacheOpMap.computeIfAbsent(nameHolder2, k2 -> {
                return new ArrayList(2);
            });
            col2.add(builder2.build());
        }
        List<Element> putCacheMethods = DomUtils.getChildElementsByTagName(definition, CACHE_PUT_ELEMENT);
        for (Element opElement3 : putCacheMethods) {
            String name3 = prop.merge(opElement3, parserContext.getReaderContext());
            TypedStringValue nameHolder3 = new TypedStringValue(name3);
            nameHolder3.setSource(parserContext.extractSource(opElement3));
            CachePutOperation.Builder builder3 = (CachePutOperation.Builder) prop.merge(opElement3, parserContext.getReaderContext(), new CachePutOperation.Builder());
            builder3.setUnless(getAttributeValue(opElement3, "unless", ""));
            Collection<CacheOperation> col3 = (Collection) cacheOpMap.computeIfAbsent(nameHolder3, k3 -> {
                return new ArrayList(2);
            });
            col3.add(builder3.build());
        }
        RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition((Class<?>) NameMatchCacheOperationSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource(definition));
        attributeSourceDefinition.getPropertyValues().add("nameMap", cacheOpMap);
        return attributeSourceDefinition;
    }

    private static String getAttributeValue(Element element, String attributeName, String defaultValue) {
        String attribute = element.getAttribute(attributeName);
        if (StringUtils.hasText(attribute)) {
            return attribute.trim();
        }
        return defaultValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/config/CacheAdviceParser$Props.class */
    public static class Props {
        private final String key;
        private final String keyGenerator;
        private final String cacheManager;
        private final String condition;
        private final String method;

        @Nullable
        private String[] caches;

        Props(Element root) {
            String defaultCache = root.getAttribute("cache");
            this.key = root.getAttribute("key");
            this.keyGenerator = root.getAttribute("key-generator");
            this.cacheManager = root.getAttribute("cache-manager");
            this.condition = root.getAttribute(IfAction.CONDITION_ATTRIBUTE);
            this.method = root.getAttribute(CacheAdviceParser.METHOD_ATTRIBUTE);
            if (StringUtils.hasText(defaultCache)) {
                this.caches = StringUtils.commaDelimitedListToStringArray(defaultCache.trim());
            }
        }

        <T extends CacheOperation.Builder> T merge(Element element, ReaderContext readerCtx, T builder) {
            String cache = element.getAttribute("cache");
            String[] localCaches = this.caches;
            if (StringUtils.hasText(cache)) {
                localCaches = StringUtils.commaDelimitedListToStringArray(cache.trim());
            }
            if (localCaches != null) {
                builder.setCacheNames(localCaches);
            } else {
                readerCtx.error("No cache specified for " + element.getNodeName(), element);
            }
            builder.setKey(CacheAdviceParser.getAttributeValue(element, "key", this.key));
            builder.setKeyGenerator(CacheAdviceParser.getAttributeValue(element, "key-generator", this.keyGenerator));
            builder.setCacheManager(CacheAdviceParser.getAttributeValue(element, "cache-manager", this.cacheManager));
            builder.setCondition(CacheAdviceParser.getAttributeValue(element, IfAction.CONDITION_ATTRIBUTE, this.condition));
            if (StringUtils.hasText(builder.getKey()) && StringUtils.hasText(builder.getKeyGenerator())) {
                throw new IllegalStateException("Invalid cache advice configuration on '" + element.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
            }
            return builder;
        }

        @Nullable
        String merge(Element element, ReaderContext readerCtx) {
            String method = element.getAttribute(CacheAdviceParser.METHOD_ATTRIBUTE);
            if (StringUtils.hasText(method)) {
                return method.trim();
            }
            if (StringUtils.hasText(this.method)) {
                return this.method;
            }
            readerCtx.error("No method specified for " + element.getNodeName(), element);
            return null;
        }
    }
}
