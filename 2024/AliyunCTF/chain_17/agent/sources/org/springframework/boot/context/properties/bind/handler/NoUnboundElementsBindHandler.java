package org.springframework.boot.context.properties.bind.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.UnboundConfigurationPropertiesException;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/handler/NoUnboundElementsBindHandler.class */
public class NoUnboundElementsBindHandler extends AbstractBindHandler {
    private final Set<ConfigurationPropertyName> boundNames;
    private final Set<ConfigurationPropertyName> attemptedNames;
    private final Function<ConfigurationPropertySource, Boolean> filter;

    NoUnboundElementsBindHandler() {
        this(BindHandler.DEFAULT, configurationPropertySource -> {
            return true;
        });
    }

    public NoUnboundElementsBindHandler(BindHandler parent) {
        this(parent, configurationPropertySource -> {
            return true;
        });
    }

    public NoUnboundElementsBindHandler(BindHandler parent, Function<ConfigurationPropertySource, Boolean> filter) {
        super(parent);
        this.boundNames = new HashSet();
        this.attemptedNames = new HashSet();
        this.filter = filter;
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
        this.attemptedNames.add(name);
        return super.onStart(name, target, context);
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        this.boundNames.add(name);
        return super.onSuccess(name, target, context, result);
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        if (error instanceof UnboundConfigurationPropertiesException) {
            throw error;
        }
        return super.onFailure(name, target, context, error);
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public void onFinish(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) throws Exception {
        if (context.getDepth() == 0) {
            checkNoUnboundElements(name, context);
        }
    }

    private void checkNoUnboundElements(ConfigurationPropertyName name, BindContext context) {
        Set<ConfigurationProperty> unbound = new TreeSet<>();
        for (ConfigurationPropertySource source : context.getSources()) {
            if ((source instanceof IterableConfigurationPropertySource) && this.filter.apply(source).booleanValue()) {
                collectUnbound(name, unbound, (IterableConfigurationPropertySource) source);
            }
        }
        if (!unbound.isEmpty()) {
            throw new UnboundConfigurationPropertiesException(unbound);
        }
    }

    private void collectUnbound(ConfigurationPropertyName name, Set<ConfigurationProperty> unbound, IterableConfigurationPropertySource source) {
        IterableConfigurationPropertySource filtered = source.filter(candidate -> {
            return isUnbound(name, candidate);
        });
        for (ConfigurationPropertyName unboundName : filtered) {
            try {
                unbound.add(source.filter(candidate2 -> {
                    return isUnbound(name, candidate2);
                }).getConfigurationProperty(unboundName));
            } catch (Exception e) {
            }
        }
    }

    private boolean isUnbound(ConfigurationPropertyName name, ConfigurationPropertyName candidate) {
        return (!name.isAncestorOf(candidate) || this.boundNames.contains(candidate) || isOverriddenCollectionElement(candidate)) ? false : true;
    }

    private boolean isOverriddenCollectionElement(ConfigurationPropertyName candidate) {
        int lastIndex = candidate.getNumberOfElements() - 1;
        if (candidate.isLastElementIndexed()) {
            ConfigurationPropertyName propertyName = candidate.chop(lastIndex);
            return this.boundNames.contains(propertyName);
        }
        Indexed indexed = getIndexed(candidate);
        if (indexed != null) {
            String zeroethProperty = indexed.getName() + "[0]";
            if (this.boundNames.contains(ConfigurationPropertyName.of(zeroethProperty))) {
                String nestedZeroethProperty = zeroethProperty + "." + indexed.getNestedPropertyName();
                return isCandidateValidPropertyName(nestedZeroethProperty);
            }
            return false;
        }
        return false;
    }

    private boolean isCandidateValidPropertyName(String nestedZeroethProperty) {
        return this.attemptedNames.contains(ConfigurationPropertyName.of(nestedZeroethProperty));
    }

    private Indexed getIndexed(ConfigurationPropertyName candidate) {
        for (int i = 0; i < candidate.getNumberOfElements(); i++) {
            if (candidate.isNumericIndex(i)) {
                return new Indexed(candidate.chop(i).toString(), candidate.getElement(i + 1, ConfigurationPropertyName.Form.UNIFORM));
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/handler/NoUnboundElementsBindHandler$Indexed.class */
    public static final class Indexed {
        private final String name;
        private final String nestedPropertyName;

        private Indexed(String name, String nestedPropertyName) {
            this.name = name;
            this.nestedPropertyName = nestedPropertyName;
        }

        String getName() {
            return this.name;
        }

        String getNestedPropertyName() {
            return this.nestedPropertyName;
        }
    }
}
