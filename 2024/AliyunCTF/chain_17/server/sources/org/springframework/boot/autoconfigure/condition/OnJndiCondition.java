package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import javax.naming.NamingException;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.util.StringUtils;

@Order(2147483627)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnJndiCondition.class */
class OnJndiCondition extends SpringBootCondition {
    OnJndiCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ConditionalOnJndi.class.getName()));
        String[] locations = annotationAttributes.getStringArray("value");
        try {
            return getMatchOutcome(locations);
        } catch (NoClassDefFoundError e) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJndi.class, new Object[0]).because("JNDI class not found"));
        }
    }

    private ConditionOutcome getMatchOutcome(String[] locations) {
        if (!isJndiAvailable()) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJndi.class, new Object[0]).notAvailable("JNDI environment"));
        }
        if (locations.length == 0) {
            return ConditionOutcome.match(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJndi.class, new Object[0]).available("JNDI environment"));
        }
        JndiLocator locator = getJndiLocator(locations);
        String location = locator.lookupFirstLocation();
        String details = "(" + StringUtils.arrayToCommaDelimitedString(locations) + ")";
        if (location != null) {
            return ConditionOutcome.match(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJndi.class, details).foundExactly("\"" + location + "\""));
        }
        return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJndi.class, details).didNotFind("any matching JNDI location").atAll());
    }

    protected boolean isJndiAvailable() {
        return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
    }

    protected JndiLocator getJndiLocator(String[] locations) {
        return new JndiLocator(locations);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnJndiCondition$JndiLocator.class */
    public static class JndiLocator extends JndiLocatorSupport {
        private final String[] locations;

        public JndiLocator(String[] locations) {
            this.locations = locations;
        }

        public String lookupFirstLocation() {
            for (String location : this.locations) {
                try {
                    lookup(location);
                    return location;
                } catch (NamingException e) {
                }
            }
            return null;
        }
    }
}
