package com.a708.drwa.profile.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final ProfileTasklet profileTasklet;
    private final RankTasklet rankTasklet;

    @Bean
    public Job updateRankingJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("updateRankingJob", jobRepository)
                .start(getAllMemberProfiles(jobRepository, transactionManager))
                .next(updateRank(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step getAllMemberProfiles(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("getAllProfilesStep", jobRepository)
                .tasklet(profileTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step updateRank(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("updateRankStep", jobRepository)
                .tasklet(rankTasklet, transactionManager)
                .build();
    }



}
