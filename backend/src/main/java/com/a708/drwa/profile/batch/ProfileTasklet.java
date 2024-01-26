package com.a708.drwa.profile.batch;

import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.service.ProfileService;
import com.a708.drwa.rank.redis.RankMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileTasklet implements Tasklet {
    private final ProfileService profileService;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("[Start Profile Search Process]");

        List<RankMember> allRankMembers = profileService.findAll();
        log.info("[End Profile Search Process]");

        contribution
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("allProfiles", allRankMembers);

        return RepeatStatus.FINISHED;
    }
}
