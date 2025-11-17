package org.springframework.boot.webservices.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.transform.TransformerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.destination.DestinationProvider;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.WebServiceMessageSender;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateBuilder.class */
public class WebServiceTemplateBuilder {
    private final boolean detectHttpMessageSender;
    private final Set<ClientInterceptor> interceptors;
    private final Set<WebServiceTemplateCustomizer> internalCustomizers;
    private final Set<WebServiceTemplateCustomizer> customizers;
    private final WebServiceMessageSenders messageSenders;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    private final DestinationProvider destinationProvider;
    private final Class<? extends TransformerFactory> transformerFactoryClass;
    private final WebServiceMessageFactory messageFactory;

    public WebServiceTemplateBuilder(WebServiceTemplateCustomizer... customizers) {
        this.detectHttpMessageSender = true;
        this.interceptors = null;
        this.internalCustomizers = null;
        this.customizers = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(customizers)));
        this.messageSenders = new WebServiceMessageSenders();
        this.marshaller = null;
        this.unmarshaller = null;
        this.destinationProvider = null;
        this.transformerFactoryClass = null;
        this.messageFactory = null;
    }

    private WebServiceTemplateBuilder(boolean detectHttpMessageSender, Set<ClientInterceptor> interceptors, Set<WebServiceTemplateCustomizer> internalCustomizers, Set<WebServiceTemplateCustomizer> customizers, WebServiceMessageSenders messageSenders, Marshaller marshaller, Unmarshaller unmarshaller, DestinationProvider destinationProvider, Class<? extends TransformerFactory> transformerFactoryClass, WebServiceMessageFactory messageFactory) {
        this.detectHttpMessageSender = detectHttpMessageSender;
        this.interceptors = interceptors;
        this.internalCustomizers = internalCustomizers;
        this.customizers = customizers;
        this.messageSenders = messageSenders;
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
        this.destinationProvider = destinationProvider;
        this.transformerFactoryClass = transformerFactoryClass;
        this.messageFactory = messageFactory;
    }

    public WebServiceTemplateBuilder detectHttpMessageSender(boolean detectHttpMessageSender) {
        return new WebServiceTemplateBuilder(detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder messageSenders(WebServiceMessageSender... messageSenders) {
        Assert.notNull(messageSenders, "MessageSenders must not be null");
        return messageSenders(Arrays.asList(messageSenders));
    }

    public WebServiceTemplateBuilder messageSenders(Collection<? extends WebServiceMessageSender> messageSenders) {
        Assert.notNull(messageSenders, "MessageSenders must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders.set(messageSenders), this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder additionalMessageSenders(WebServiceMessageSender... messageSenders) {
        Assert.notNull(messageSenders, "MessageSenders must not be null");
        return additionalMessageSenders(Arrays.asList(messageSenders));
    }

    public WebServiceTemplateBuilder additionalMessageSenders(Collection<? extends WebServiceMessageSender> messageSenders) {
        Assert.notNull(messageSenders, "MessageSenders must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders.add(messageSenders), this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder interceptors(ClientInterceptor... interceptors) {
        Assert.notNull(interceptors, "Interceptors must not be null");
        return interceptors(Arrays.asList(interceptors));
    }

    public WebServiceTemplateBuilder interceptors(Collection<? extends ClientInterceptor> interceptors) {
        Assert.notNull(interceptors, "Interceptors must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, append(Collections.emptySet(), (Collection) interceptors), this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder additionalInterceptors(ClientInterceptor... interceptors) {
        Assert.notNull(interceptors, "Interceptors must not be null");
        return additionalInterceptors(Arrays.asList(interceptors));
    }

    public WebServiceTemplateBuilder additionalInterceptors(Collection<? extends ClientInterceptor> interceptors) {
        Assert.notNull(interceptors, "Interceptors must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, append((Set) this.interceptors, (Collection) interceptors), this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder customizers(WebServiceTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public WebServiceTemplateBuilder customizers(Collection<? extends WebServiceTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, append(Collections.emptySet(), (Collection) customizers), this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder additionalCustomizers(WebServiceTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public WebServiceTemplateBuilder additionalCustomizers(Collection<? extends WebServiceTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, append((Set) this.customizers, (Collection) customizers), this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setCheckConnectionForFault(boolean checkConnectionForFault) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, append((Set<Set<WebServiceTemplateCustomizer>>) this.internalCustomizers, (Set<WebServiceTemplateCustomizer>) new CheckConnectionFaultCustomizer(checkConnectionForFault)), this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setCheckConnectionForError(boolean checkConnectionForError) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, append((Set<Set<WebServiceTemplateCustomizer>>) this.internalCustomizers, (Set<WebServiceTemplateCustomizer>) new CheckConnectionForErrorCustomizer(checkConnectionForError)), this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setWebServiceMessageFactory(WebServiceMessageFactory messageFactory) {
        Assert.notNull(messageFactory, "MessageFactory must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, messageFactory);
    }

    public WebServiceTemplateBuilder setUnmarshaller(Unmarshaller unmarshaller) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setMarshaller(Marshaller marshaller) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setFaultMessageResolver(FaultMessageResolver faultMessageResolver) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, append((Set<Set<WebServiceTemplateCustomizer>>) this.internalCustomizers, (Set<WebServiceTemplateCustomizer>) new FaultMessageResolverCustomizer(faultMessageResolver)), this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setTransformerFactoryClass(Class<? extends TransformerFactory> transformerFactoryClass) {
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, this.destinationProvider, transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplateBuilder setDefaultUri(String defaultUri) {
        Assert.hasText(defaultUri, "DefaultUri must not be empty");
        return setDestinationProvider(() -> {
            return URI.create(defaultUri);
        });
    }

    public WebServiceTemplateBuilder setDestinationProvider(DestinationProvider destinationProvider) {
        Assert.notNull(destinationProvider, "DestinationProvider must not be null");
        return new WebServiceTemplateBuilder(this.detectHttpMessageSender, this.interceptors, this.internalCustomizers, this.customizers, this.messageSenders, this.marshaller, this.unmarshaller, destinationProvider, this.transformerFactoryClass, this.messageFactory);
    }

    public WebServiceTemplate build() {
        return configure(new WebServiceTemplate());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends WebServiceTemplate> T build(Class<T> cls) {
        Assert.notNull(cls, "WebServiceTemplateClass must not be null");
        return (T) configure((WebServiceTemplate) BeanUtils.instantiateClass(cls));
    }

    public <T extends WebServiceTemplate> T configure(T webServiceTemplate) {
        Assert.notNull(webServiceTemplate, "WebServiceTemplate must not be null");
        configureMessageSenders(webServiceTemplate);
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        applyCustomizers(webServiceTemplate, this.internalCustomizers);
        PropertyMapper.Source from = map.from((PropertyMapper) this.marshaller);
        Objects.requireNonNull(webServiceTemplate);
        from.to(webServiceTemplate::setMarshaller);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.unmarshaller);
        Objects.requireNonNull(webServiceTemplate);
        from2.to(webServiceTemplate::setUnmarshaller);
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.destinationProvider);
        Objects.requireNonNull(webServiceTemplate);
        from3.to(webServiceTemplate::setDestinationProvider);
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.transformerFactoryClass);
        Objects.requireNonNull(webServiceTemplate);
        from4.to(webServiceTemplate::setTransformerFactoryClass);
        PropertyMapper.Source from5 = map.from((PropertyMapper) this.messageFactory);
        Objects.requireNonNull(webServiceTemplate);
        from5.to(webServiceTemplate::setMessageFactory);
        if (!CollectionUtils.isEmpty(this.interceptors)) {
            Set<ClientInterceptor> merged = new LinkedHashSet<>(this.interceptors);
            if (webServiceTemplate.getInterceptors() != null) {
                merged.addAll(Arrays.asList(webServiceTemplate.getInterceptors()));
            }
            webServiceTemplate.setInterceptors((ClientInterceptor[]) merged.toArray(new ClientInterceptor[0]));
        }
        applyCustomizers(webServiceTemplate, this.customizers);
        return webServiceTemplate;
    }

    private void applyCustomizers(WebServiceTemplate webServiceTemplate, Set<WebServiceTemplateCustomizer> customizers) {
        if (!CollectionUtils.isEmpty(customizers)) {
            for (WebServiceTemplateCustomizer customizer : customizers) {
                customizer.customize(webServiceTemplate);
            }
        }
    }

    private <T extends WebServiceTemplate> void configureMessageSenders(T webServiceTemplate) {
        if (this.messageSenders.isOnlyAdditional() && this.detectHttpMessageSender) {
            Set<WebServiceMessageSender> merged = append((Set<Set<WebServiceMessageSender>>) this.messageSenders.getMessageSenders(), (Set<WebServiceMessageSender>) new HttpWebServiceMessageSenderBuilder().build());
            webServiceTemplate.setMessageSenders((WebServiceMessageSender[]) merged.toArray(new WebServiceMessageSender[0]));
        } else if (!CollectionUtils.isEmpty(this.messageSenders.getMessageSenders())) {
            webServiceTemplate.setMessageSenders((WebServiceMessageSender[]) this.messageSenders.getMessageSenders().toArray(new WebServiceMessageSender[0]));
        }
    }

    private <T> Set<T> append(Set<T> set, T addition) {
        return append((Set) set, (Collection) Collections.singleton(addition));
    }

    private static <T> Set<T> append(Set<T> set, Collection<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        result.addAll(additions != null ? additions : Collections.emptyList());
        return Collections.unmodifiableSet(result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateBuilder$WebServiceMessageSenders.class */
    public static final class WebServiceMessageSenders {
        private final boolean onlyAdditional;
        private final Set<WebServiceMessageSender> messageSenders;

        private WebServiceMessageSenders() {
            this(true, Collections.emptySet());
        }

        private WebServiceMessageSenders(boolean onlyAdditional, Set<WebServiceMessageSender> messageSenders) {
            this.onlyAdditional = onlyAdditional;
            this.messageSenders = messageSenders;
        }

        boolean isOnlyAdditional() {
            return this.onlyAdditional;
        }

        Set<WebServiceMessageSender> getMessageSenders() {
            return this.messageSenders;
        }

        WebServiceMessageSenders set(Collection<? extends WebServiceMessageSender> messageSenders) {
            return new WebServiceMessageSenders(false, new LinkedHashSet(messageSenders));
        }

        WebServiceMessageSenders add(Collection<? extends WebServiceMessageSender> messageSenders) {
            return new WebServiceMessageSenders(this.onlyAdditional, WebServiceTemplateBuilder.append((Set) this.messageSenders, (Collection) messageSenders));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateBuilder$CheckConnectionFaultCustomizer.class */
    private static final class CheckConnectionFaultCustomizer implements WebServiceTemplateCustomizer {
        private final boolean checkConnectionFault;

        private CheckConnectionFaultCustomizer(boolean checkConnectionFault) {
            this.checkConnectionFault = checkConnectionFault;
        }

        @Override // org.springframework.boot.webservices.client.WebServiceTemplateCustomizer
        public void customize(WebServiceTemplate webServiceTemplate) {
            webServiceTemplate.setCheckConnectionForFault(this.checkConnectionFault);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateBuilder$CheckConnectionForErrorCustomizer.class */
    private static final class CheckConnectionForErrorCustomizer implements WebServiceTemplateCustomizer {
        private final boolean checkConnectionForError;

        private CheckConnectionForErrorCustomizer(boolean checkConnectionForError) {
            this.checkConnectionForError = checkConnectionForError;
        }

        @Override // org.springframework.boot.webservices.client.WebServiceTemplateCustomizer
        public void customize(WebServiceTemplate webServiceTemplate) {
            webServiceTemplate.setCheckConnectionForError(this.checkConnectionForError);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateBuilder$FaultMessageResolverCustomizer.class */
    private static final class FaultMessageResolverCustomizer implements WebServiceTemplateCustomizer {
        private final FaultMessageResolver faultMessageResolver;

        private FaultMessageResolverCustomizer(FaultMessageResolver faultMessageResolver) {
            this.faultMessageResolver = faultMessageResolver;
        }

        @Override // org.springframework.boot.webservices.client.WebServiceTemplateCustomizer
        public void customize(WebServiceTemplate webServiceTemplate) {
            webServiceTemplate.setFaultMessageResolver(this.faultMessageResolver);
        }
    }
}
