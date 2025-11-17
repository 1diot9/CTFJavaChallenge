package ch.qos.logback.classic.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareImpl;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/ContextInitializer.class */
public class ContextInitializer {
    public static final String AUTOCONFIG_FILE = "logback.xml";
    public static final String TEST_AUTOCONFIG_FILE = "logback-test.xml";
    public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
    final LoggerContext loggerContext;
    final ContextAware contextAware;
    String[] INTERNAL_CONFIGURATOR_CLASSNAME_LIST = {"ch.qos.logback.classic.joran.SerializedModelConfigurator", "ch.qos.logback.classic.util.DefaultJoranConfigurator", "ch.qos.logback.classic.BasicConfigurator"};
    Comparator<Configurator> rankComparator = new Comparator<Configurator>() { // from class: ch.qos.logback.classic.util.ContextInitializer.1
        @Override // java.util.Comparator
        public int compare(Configurator c1, Configurator c2) {
            ConfiguratorRank r1 = (ConfiguratorRank) c1.getClass().getAnnotation(ConfiguratorRank.class);
            ConfiguratorRank r2 = (ConfiguratorRank) c2.getClass().getAnnotation(ConfiguratorRank.class);
            int value1 = r1 == null ? 20 : r1.value();
            int value2 = r2 == null ? 20 : r2.value();
            int result = ContextInitializer.this.compareRankValue(value1, value2);
            return -result;
        }
    };

    public ContextInitializer(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
        this.contextAware = new ContextAwareImpl(loggerContext, this);
    }

    public void autoConfig() throws JoranException {
        autoConfig(Configurator.class.getClassLoader());
    }

    public void autoConfig(ClassLoader classLoader) throws JoranException {
        ClassLoader classLoader2 = Loader.systemClassloaderIfNull(classLoader);
        String versionStr = EnvUtil.logbackVersion();
        if (versionStr == null) {
            versionStr = CoreConstants.NA;
        }
        this.loggerContext.getStatusManager().add(new InfoStatus("This is logback-classic version " + versionStr, this.loggerContext));
        StatusListenerConfigHelper.installIfAsked(this.loggerContext);
        List<Configurator> configuratorList = ClassicEnvUtil.loadFromServiceLoader(Configurator.class, classLoader2);
        configuratorList.sort(this.rankComparator);
        if (configuratorList.isEmpty()) {
            this.contextAware.addInfo("No custom configurators were discovered as a service.");
        } else {
            printConfiguratorOrder(configuratorList);
        }
        Iterator<Configurator> it = configuratorList.iterator();
        while (it.hasNext()) {
            if (invokeConfigure(it.next()) == Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY) {
                return;
            }
        }
        for (String configuratorClassName : this.INTERNAL_CONFIGURATOR_CLASSNAME_LIST) {
            this.contextAware.addInfo("Trying to configure with " + configuratorClassName);
            Configurator c = instantiateConfiguratorByClassName(configuratorClassName, classLoader2);
            if (c != null && invokeConfigure(c) == Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY) {
                return;
            }
        }
    }

    private Configurator instantiateConfiguratorByClassName(String configuratorClassName, ClassLoader classLoader) {
        try {
            Class<?> classObj = classLoader.loadClass(configuratorClassName);
            return (Configurator) classObj.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ReflectiveOperationException e) {
            this.contextAware.addInfo("Instantiation failure: " + e.toString());
            return null;
        }
    }

    private Configurator.ExecutionStatus invokeConfigure(Configurator configurator) {
        try {
            long start = System.currentTimeMillis();
            this.contextAware.addInfo("Constructed configurator of type " + String.valueOf(configurator.getClass()));
            configurator.setContext(this.loggerContext);
            Configurator.ExecutionStatus status = configurator.configure(this.loggerContext);
            printDuration(start, configurator, status);
            return status;
        } catch (Exception e) {
            Object[] objArr = new Object[1];
            objArr[0] = configurator != null ? configurator.getClass().getCanonicalName() : "null";
            throw new LogbackException(String.format("Failed to initialize or to run Configurator: %s", objArr), e);
        }
    }

    private void printConfiguratorOrder(List<Configurator> configuratorList) {
        this.contextAware.addInfo("Here is a list of configurators discovered as a service, by rank: ");
        for (Configurator c : configuratorList) {
            this.contextAware.addInfo("  " + c.getClass().getName());
        }
        this.contextAware.addInfo("They will be invoked in order until ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY is returned.");
    }

    private void printDuration(long start, Configurator configurator, Configurator.ExecutionStatus executionStatus) {
        long end = System.currentTimeMillis();
        long diff = end - start;
        ContextAware contextAware = this.contextAware;
        String name = configurator.getClass().getName();
        String.valueOf(executionStatus);
        contextAware.addInfo(name + ".configure() call lasted " + diff + " milliseconds. ExecutionStatus=" + contextAware);
    }

    private Configurator.ExecutionStatus attemptConfigurationUsingJoranUsingReflexion(ClassLoader classLoader) {
        try {
            Class<?> djcClass = classLoader.loadClass("ch.qos.logback.classic.util.DefaultJoranConfigurator");
            Configurator c = (Configurator) djcClass.newInstance();
            c.setContext(this.loggerContext);
            return c.configure(this.loggerContext);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            this.contextAware.addError("unexpected exception while instantiating DefaultJoranConfigurator", e);
            return Configurator.ExecutionStatus.INVOKE_NEXT_IF_ANY;
        }
    }

    private int compareRankValue(int value1, int value2) {
        if (value1 > value2) {
            return 1;
        }
        if (value1 == value2) {
            return 0;
        }
        return -1;
    }
}
