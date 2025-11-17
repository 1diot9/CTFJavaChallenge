package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/SimpleUrlHandlerMapping.class */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {
    private final Map<String, Object> urlMap = new LinkedHashMap();

    public SimpleUrlHandlerMapping() {
    }

    public SimpleUrlHandlerMapping(Map<String, ?> urlMap) {
        setUrlMap(urlMap);
    }

    public SimpleUrlHandlerMapping(Map<String, ?> urlMap, int order) {
        setUrlMap(urlMap);
        setOrder(order);
    }

    public void setMappings(Properties mappings) {
        CollectionUtils.mergePropertiesIntoMap(mappings, this.urlMap);
    }

    public void setUrlMap(Map<String, ?> urlMap) {
        this.urlMap.putAll(urlMap);
    }

    public Map<String, ?> getUrlMap() {
        return this.urlMap;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws BeansException {
        super.initApplicationContext();
        registerHandlers(this.urlMap);
    }

    protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
        if (urlMap.isEmpty()) {
            this.logger.trace("No patterns in " + formatMappingName());
        } else {
            urlMap.forEach((url, handler) -> {
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                if (handler instanceof String) {
                    String handlerName = (String) handler;
                    handler = handlerName.trim();
                }
                registerHandler(url, handler);
            });
            logMappings();
        }
    }

    private void logMappings() {
        if (this.mappingsLogger.isDebugEnabled()) {
            Map<String, Object> map = new LinkedHashMap<>(getHandlerMap());
            if (getRootHandler() != null) {
                map.put("/", getRootHandler());
            }
            if (getDefaultHandler() != null) {
                map.put("/**", getDefaultHandler());
            }
            this.mappingsLogger.debug(formatMappingName() + " " + map);
            return;
        }
        if (this.logger.isDebugEnabled()) {
            List<String> patterns = new ArrayList<>();
            if (getRootHandler() != null) {
                patterns.add("/");
            }
            if (getDefaultHandler() != null) {
                patterns.add("/**");
            }
            patterns.addAll(getHandlerMap().keySet());
            this.logger.debug("Patterns " + patterns + " in " + formatMappingName());
        }
    }
}
