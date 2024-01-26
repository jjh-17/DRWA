package com.a708.drwa.profile.batch;

import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.redis.RankMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankTasklet implements Tasklet {
    private final ProfileRepository profileRepository;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Rank Update Step Start");

        List<RankMember> allProfiles = (List<RankMember>) contribution
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("allProfiles");
        log.info("data={}", allProfiles);

        /**
         * Save in RDB
         */



        log.info("Rank Update Step End");

        return RepeatStatus.FINISHED;
    }
}
