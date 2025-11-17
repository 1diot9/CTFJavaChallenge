package org.springframework.boot.autoconfigure.condition;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnPropertyListCondition.class */
public class OnPropertyListCondition extends SpringBootCondition {
    private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);
    private final String propertyName;
    private final Supplier<ConditionMessage.Builder> messageBuilder;

    /* JADX INFO: Access modifiers changed from: protected */
    public OnPropertyListCondition(String propertyName, Supplier<ConditionMessage.Builder> messageBuilder) {
        this.propertyName = propertyName;
        this.messageBuilder = messageBuilder;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        BindResult<?> property = Binder.get(context.getEnvironment()).bind(this.propertyName, STRING_LIST);
        ConditionMessage.Builder messageBuilder = this.messageBuilder.get();
        return property.isBound() ? ConditionOutcome.match(messageBuilder.found(BeanDefinitionParserDelegate.PROPERTY_ELEMENT).items(this.propertyName)) : ConditionOutcome.noMatch(messageBuilder.didNotFind(BeanDefinitionParserDelegate.PROPERTY_ELEMENT).items(this.propertyName));
    }
}
