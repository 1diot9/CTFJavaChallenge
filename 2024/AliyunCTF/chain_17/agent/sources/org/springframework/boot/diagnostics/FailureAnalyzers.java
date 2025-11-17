package org.springframework.boot.diagnostics;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/FailureAnalyzers.class */
final class FailureAnalyzers implements SpringBootExceptionReporter {
    private static final Log logger = LogFactory.getLog((Class<?>) FailureAnalyzers.class);
    private final SpringFactoriesLoader springFactoriesLoader;
    private final List<FailureAnalyzer> analyzers;

    public FailureAnalyzers(ConfigurableApplicationContext context) {
        this(context, SpringFactoriesLoader.forDefaultResourceLocation(context != null ? context.getClassLoader() : null));
    }

    FailureAnalyzers(ConfigurableApplicationContext context, SpringFactoriesLoader springFactoriesLoader) {
        this.springFactoriesLoader = springFactoriesLoader;
        this.analyzers = loadFailureAnalyzers(context, this.springFactoriesLoader);
    }

    private static List<FailureAnalyzer> loadFailureAnalyzers(ConfigurableApplicationContext context, SpringFactoriesLoader springFactoriesLoader) {
        List<FailureAnalyzer> analyzers = springFactoriesLoader.load(FailureAnalyzer.class, getArgumentResolver(context), SpringFactoriesLoader.FailureHandler.logging(logger));
        List<FailureAnalyzer> awareAnalyzers = analyzers.stream().filter(analyzer -> {
            return (analyzer instanceof BeanFactoryAware) || (analyzer instanceof EnvironmentAware);
        }).toList();
        if (!awareAnalyzers.isEmpty()) {
            String awareAnalyzerNames = StringUtils.collectionToCommaDelimitedString(awareAnalyzers.stream().map(analyzer2 -> {
                return analyzer2.getClass().getName();
            }).toList());
            logger.warn(LogMessage.format("FailureAnalyzers [%s] implement BeanFactoryAware or EnvironmentAware. Support for these interfaces on FailureAnalyzers is deprecated, and will be removed in a future release. Instead provide a constructor that accepts BeanFactory or Environment parameters.", awareAnalyzerNames));
            if (context == null) {
                logger.trace(LogMessage.format("Skipping [%s] due to missing context", awareAnalyzerNames));
                return analyzers.stream().filter(analyzer3 -> {
                    return !awareAnalyzers.contains(analyzer3);
                }).toList();
            }
            awareAnalyzers.forEach(analyzer4 -> {
                if (analyzer4 instanceof BeanFactoryAware) {
                    BeanFactoryAware beanFactoryAware = (BeanFactoryAware) analyzer4;
                    beanFactoryAware.setBeanFactory(context.getBeanFactory());
                }
                if (analyzer4 instanceof EnvironmentAware) {
                    EnvironmentAware environmentAware = (EnvironmentAware) analyzer4;
                    environmentAware.setEnvironment(context.getEnvironment());
                }
            });
        }
        return analyzers;
    }

    private static SpringFactoriesLoader.ArgumentResolver getArgumentResolver(ConfigurableApplicationContext context) {
        if (context == null) {
            return null;
        }
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(BeanFactory.class, context.getBeanFactory());
        return argumentResolver.and(Environment.class, context.getEnvironment());
    }

    @Override // org.springframework.boot.SpringBootExceptionReporter
    public boolean reportException(Throwable failure) {
        FailureAnalysis analysis = analyze(failure, this.analyzers);
        return report(analysis);
    }

    private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
        FailureAnalysis analysis;
        for (FailureAnalyzer analyzer : analyzers) {
            try {
                analysis = analyzer.analyze(failure);
            } catch (Throwable ex) {
                logger.trace(LogMessage.format("FailureAnalyzer %s failed", analyzer), ex);
            }
            if (analysis != null) {
                return analysis;
            }
        }
        return null;
    }

    private boolean report(FailureAnalysis analysis) {
        List<FailureAnalysisReporter> reporters = this.springFactoriesLoader.load(FailureAnalysisReporter.class);
        if (analysis == null || reporters.isEmpty()) {
            return false;
        }
        for (FailureAnalysisReporter reporter : reporters) {
            reporter.report(analysis);
        }
        return true;
    }
}
