package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/BeanNameUrlHandlerMapping.class */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {
    @Override // org.springframework.web.servlet.handler.AbstractDetectingUrlHandlerMapping
    protected String[] determineUrlsForHandler(String beanName) {
        List<String> urls = new ArrayList<>();
        if (beanName.startsWith("/")) {
            urls.add(beanName);
        }
        String[] aliases = obtainApplicationContext().getAliases(beanName);
        for (String alias : aliases) {
            if (alias.startsWith("/")) {
                urls.add(alias);
            }
        }
        return StringUtils.toStringArray(urls);
    }
}
