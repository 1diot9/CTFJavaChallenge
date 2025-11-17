package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/AdviceModeImportSelector.class */
public abstract class AdviceModeImportSelector<A extends Annotation> implements ImportSelector {
    public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";

    @Nullable
    protected abstract String[] selectImports(AdviceMode adviceMode);

    protected String getAdviceModeAttributeName() {
        return DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME;
    }

    @Override // org.springframework.context.annotation.ImportSelector
    public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), AdviceModeImportSelector.class);
        Assert.state(annType != null, "Unresolvable type argument for AdviceModeImportSelector");
        AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
        if (attributes == null) {
            throw new IllegalArgumentException(String.format("@%s is not present on importing class '%s' as expected", annType.getSimpleName(), importingClassMetadata.getClassName()));
        }
        AdviceMode adviceMode = (AdviceMode) attributes.getEnum(getAdviceModeAttributeName());
        String[] imports = selectImports(adviceMode);
        if (imports == null) {
            throw new IllegalArgumentException("Unknown AdviceMode: " + adviceMode);
        }
        return imports;
    }
}
