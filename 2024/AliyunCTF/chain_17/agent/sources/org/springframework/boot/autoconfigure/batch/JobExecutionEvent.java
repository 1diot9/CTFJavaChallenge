package org.springframework.boot.autoconfigure.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.context.ApplicationEvent;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/JobExecutionEvent.class */
public class JobExecutionEvent extends ApplicationEvent {
    private final JobExecution execution;

    public JobExecutionEvent(JobExecution execution) {
        super(execution);
        this.execution = execution;
    }

    public JobExecution getJobExecution() {
        return this.execution;
    }
}
