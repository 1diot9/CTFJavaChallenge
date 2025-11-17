package org.springframework.beans.factory.parsing;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ProblemReporter.class */
public interface ProblemReporter {
    void fatal(Problem problem);

    void error(Problem problem);

    void warning(Problem problem);
}
