package org.springframework.boot.autoconfigure.batch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/JobLauncherApplicationRunner.class */
public class JobLauncherApplicationRunner implements ApplicationRunner, InitializingBean, Ordered, ApplicationEventPublisherAware {
    public static final int DEFAULT_ORDER = 0;
    private static final Log logger = LogFactory.getLog((Class<?>) JobLauncherApplicationRunner.class);
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private JobRegistry jobRegistry;
    private String jobName;
    private ApplicationEventPublisher publisher;
    private JobParametersConverter converter = new DefaultJobParametersConverter();
    private Collection<Job> jobs = Collections.emptySet();
    private int order = 0;

    public JobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository) {
        Assert.notNull(jobLauncher, "JobLauncher must not be null");
        Assert.notNull(jobExplorer, "JobExplorer must not be null");
        Assert.notNull(jobRepository, "JobRepository must not be null");
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Assert.isTrue(this.jobs.size() <= 1 || StringUtils.hasText(this.jobName), "Job name must be specified in case of multiple jobs");
        if (StringUtils.hasText(this.jobName)) {
            Assert.isTrue(isLocalJob(this.jobName) || isRegisteredJob(this.jobName), (Supplier<String>) () -> {
                return "No job found with name '" + this.jobName + "'";
            });
        }
    }

    @Deprecated(since = "3.0.10", forRemoval = true)
    public void validate() {
        afterPropertiesSet();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.ApplicationEventPublisherAware
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Autowired(required = false)
    public void setJobRegistry(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Autowired(required = false)
    public void setJobParametersConverter(JobParametersConverter converter) {
        this.converter = converter;
    }

    @Autowired(required = false)
    public void setJobs(Collection<Job> jobs) {
        this.jobs = jobs;
    }

    @Override // org.springframework.boot.ApplicationRunner
    public void run(ApplicationArguments args) throws Exception {
        String[] jobArguments = (String[]) args.getNonOptionArgs().toArray(new String[0]);
        run(jobArguments);
    }

    public void run(String... args) throws JobExecutionException {
        logger.info("Running default command line with: " + Arrays.asList(args));
        launchJobFromProperties(StringUtils.splitArrayElementsIntoProperties(args, "="));
    }

    protected void launchJobFromProperties(Properties properties) throws JobExecutionException {
        JobParameters jobParameters = this.converter.getJobParameters(properties);
        executeLocalJobs(jobParameters);
        executeRegisteredJobs(jobParameters);
    }

    private boolean isLocalJob(String jobName) {
        return this.jobs.stream().anyMatch(job -> {
            return job.getName().equals(jobName);
        });
    }

    private boolean isRegisteredJob(String jobName) {
        return this.jobRegistry != null && this.jobRegistry.getJobNames().contains(jobName);
    }

    private void executeLocalJobs(JobParameters jobParameters) throws JobExecutionException {
        for (Job job : this.jobs) {
            if (StringUtils.hasText(this.jobName) && !this.jobName.equals(job.getName())) {
                logger.debug(LogMessage.format("Skipped job: %s", job.getName()));
            } else {
                execute(job, jobParameters);
            }
        }
    }

    private void executeRegisteredJobs(JobParameters jobParameters) throws JobExecutionException {
        if (this.jobRegistry != null && StringUtils.hasText(this.jobName) && !isLocalJob(this.jobName)) {
            Job job = this.jobRegistry.getJob(this.jobName);
            execute(job, jobParameters);
        }
    }

    protected void execute(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobParameters parameters = getNextJobParameters(job, jobParameters);
        JobExecution execution = this.jobLauncher.run(job, parameters);
        if (this.publisher != null) {
            this.publisher.publishEvent((ApplicationEvent) new JobExecutionEvent(execution));
        }
    }

    private JobParameters getNextJobParameters(Job job, JobParameters jobParameters) {
        if (this.jobRepository != null && this.jobRepository.isJobInstanceExists(job.getName(), jobParameters)) {
            return getNextJobParametersForExisting(job, jobParameters);
        }
        if (job.getJobParametersIncrementer() == null) {
            return jobParameters;
        }
        JobParameters nextParameters = new JobParametersBuilder(jobParameters, this.jobExplorer).getNextJobParameters(job).toJobParameters();
        return merge(nextParameters, jobParameters);
    }

    private JobParameters getNextJobParametersForExisting(Job job, JobParameters jobParameters) {
        JobExecution lastExecution = this.jobRepository.getLastJobExecution(job.getName(), jobParameters);
        if (isStoppedOrFailed(lastExecution) && job.isRestartable()) {
            JobParameters previousIdentifyingParameters = new JobParameters(lastExecution.getJobParameters().getIdentifyingParameters());
            return merge(previousIdentifyingParameters, jobParameters);
        }
        return jobParameters;
    }

    private boolean isStoppedOrFailed(JobExecution execution) {
        BatchStatus status = execution != null ? execution.getStatus() : null;
        return status == BatchStatus.STOPPED || status == BatchStatus.FAILED;
    }

    private JobParameters merge(JobParameters parameters, JobParameters additionals) {
        Map<String, JobParameter<?>> merged = new LinkedHashMap<>();
        merged.putAll(parameters.getParameters());
        merged.putAll(additionals.getParameters());
        return new JobParameters(merged);
    }
}
