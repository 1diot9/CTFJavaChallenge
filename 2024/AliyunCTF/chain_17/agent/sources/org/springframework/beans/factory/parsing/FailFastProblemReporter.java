package org.springframework.beans.factory.parsing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/FailFastProblemReporter.class */
public class FailFastProblemReporter implements ProblemReporter {
    private Log logger = LogFactory.getLog(getClass());

    public void setLogger(@Nullable Log logger) {
        this.logger = logger != null ? logger : LogFactory.getLog(getClass());
    }

    @Override // org.springframework.beans.factory.parsing.ProblemReporter
    public void fatal(Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }

    @Override // org.springframework.beans.factory.parsing.ProblemReporter
    public void error(Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }

    @Override // org.springframework.beans.factory.parsing.ProblemReporter
    public void warning(Problem problem) {
        this.logger.warn(problem, problem.getRootCause());
    }
}
