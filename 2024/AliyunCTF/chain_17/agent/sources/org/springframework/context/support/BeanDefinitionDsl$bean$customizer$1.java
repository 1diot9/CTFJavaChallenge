package org.springframework.context.support;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.support.BeanDefinitionDsl;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 176, d1 = {"��\u0014\n��\n\u0002\u0010\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\u0010��\u001a\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "", "T", "", "bd", "Lorg/springframework/beans/factory/config/BeanDefinition;", "customize"})
@SourceDebugExtension({"SMAP\nBeanDefinitionDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl$bean$customizer$1\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1214:1\n1#2:1215\n*E\n"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/BeanDefinitionDsl$bean$customizer$1.class */
public final class BeanDefinitionDsl$bean$customizer$1 implements BeanDefinitionCustomizer {
    final /* synthetic */ BeanDefinitionDsl.Scope $scope;
    final /* synthetic */ Boolean $isLazyInit;
    final /* synthetic */ Boolean $isPrimary;
    final /* synthetic */ Boolean $isAutowireCandidate;
    final /* synthetic */ String $initMethodName;
    final /* synthetic */ String $destroyMethodName;
    final /* synthetic */ String $description;
    final /* synthetic */ BeanDefinitionDsl.Role $role;
    final /* synthetic */ Integer $order;

    public BeanDefinitionDsl$bean$customizer$1(BeanDefinitionDsl.Scope $scope, Boolean $isLazyInit, Boolean $isPrimary, Boolean $isAutowireCandidate, String $initMethodName, String $destroyMethodName, String $description, BeanDefinitionDsl.Role $role, Integer $order) {
        this.$scope = $scope;
        this.$isLazyInit = $isLazyInit;
        this.$isPrimary = $isPrimary;
        this.$isAutowireCandidate = $isAutowireCandidate;
        this.$initMethodName = $initMethodName;
        this.$destroyMethodName = $destroyMethodName;
        this.$description = $description;
        this.$role = $role;
        this.$order = $order;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionCustomizer
    public final void customize(@NotNull BeanDefinition bd) {
        Intrinsics.checkNotNullParameter(bd, "bd");
        if (this.$scope != null) {
            String lowerCase = this.$scope.name().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            bd.setScope(lowerCase);
        }
        Boolean bool = this.$isLazyInit;
        if (bool != null) {
            Boolean bool2 = this.$isLazyInit;
            bool.booleanValue();
            bd.setLazyInit(bool2.booleanValue());
        }
        Boolean bool3 = this.$isPrimary;
        if (bool3 != null) {
            Boolean bool4 = this.$isPrimary;
            bool3.booleanValue();
            bd.setPrimary(bool4.booleanValue());
        }
        Boolean bool5 = this.$isAutowireCandidate;
        if (bool5 != null) {
            Boolean bool6 = this.$isAutowireCandidate;
            bool5.booleanValue();
            bd.setAutowireCandidate(bool6.booleanValue());
        }
        if (this.$initMethodName != null) {
            bd.setInitMethodName(this.$initMethodName);
        }
        if (this.$destroyMethodName != null) {
            bd.setDestroyMethodName(this.$destroyMethodName);
        }
        if (this.$description != null) {
            bd.setDescription(this.$description);
        }
        if (this.$role != null) {
            bd.setRole(this.$role.ordinal());
        }
        Integer num = this.$order;
        if (num != null) {
            Integer num2 = this.$order;
            num.intValue();
            bd.setAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE, num2);
        }
    }
}
