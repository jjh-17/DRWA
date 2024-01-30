package com.a708.drwa.profile.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Slf4j
@Component
public class BatchScheduler {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    BatchConfig batchConfig;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Scheduled(cron = "0 0 * * * *") // 1시간 마다
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("startDateTime", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(
                batchConfig.updateRankingJob(jobRepository, platformTransactionManager),
                jobParameters);
    }
}
