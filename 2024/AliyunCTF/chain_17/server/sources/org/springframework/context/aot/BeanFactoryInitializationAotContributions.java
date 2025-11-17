package org.springframework.context.aot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/BeanFactoryInitializationAotContributions.class */
public class BeanFactoryInitializationAotContributions {
    private final List<BeanFactoryInitializationAotContribution> contributions;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanFactoryInitializationAotContributions(DefaultListableBeanFactory beanFactory) {
        this(beanFactory, AotServices.factoriesAndBeans(beanFactory));
    }

    BeanFactoryInitializationAotContributions(DefaultListableBeanFactory beanFactory, AotServices.Loader loader) {
        this.contributions = getContributions(beanFactory, getProcessors(loader));
    }

    private static List<BeanFactoryInitializationAotProcessor> getProcessors(AotServices.Loader loader) {
        List<BeanFactoryInitializationAotProcessor> processors = new ArrayList<>(loader.load(BeanFactoryInitializationAotProcessor.class).asList());
        processors.add(new RuntimeHintsBeanFactoryInitializationAotProcessor());
        return Collections.unmodifiableList(processors);
    }

    private List<BeanFactoryInitializationAotContribution> getContributions(DefaultListableBeanFactory beanFactory, List<BeanFactoryInitializationAotProcessor> processors) {
        List<BeanFactoryInitializationAotContribution> contributions = new ArrayList<>();
        for (BeanFactoryInitializationAotProcessor processor : processors) {
            BeanFactoryInitializationAotContribution contribution = processor.processAheadOfTime(beanFactory);
            if (contribution != null) {
                contributions.add(contribution);
            }
        }
        return Collections.unmodifiableList(contributions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
        for (BeanFactoryInitializationAotContribution contribution : this.contributions) {
            contribution.applyTo(generationContext, beanFactoryInitializationCode);
        }
    }
}
