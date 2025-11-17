package org.springframework.boot.autoconfigure.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.batch.core.JobExecution;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationListener;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/JobExecutionExitCodeGenerator.class */
public class JobExecutionExitCodeGenerator implements ApplicationListener<JobExecutionEvent>, ExitCodeGenerator {
    private final List<JobExecution> executions = new CopyOnWriteArrayList();

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(JobExecutionEvent event) {
        this.executions.add(event.getJobExecution());
    }

    @Override // org.springframework.boot.ExitCodeGenerator
    public int getExitCode() {
        for (JobExecution execution : this.executions) {
            if (execution.getStatus().ordinal() > 0) {
                return execution.getStatus().ordinal();
            }
        }
        return 0;
    }
}
