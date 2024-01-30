package com.a708.drwa.profile.batch;

import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.ranking.domain.RankingMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankTasklet implements Tasklet {
    private final ProfileRepository profileRepository;
    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Rank Update Step Start");

        List<RankingMember> allRankingMembers = (List<RankingMember>) contribution
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("allProfiles");
        log.info("data={}", allRankingMembers);

        /**
         * Save in RDB
         */
        List<Profile> profiles = profileRepository.findAll();
        for (RankingMember rankingMember : allRankingMembers){
            Optional<Profile> matchingProfile = profiles.stream()
                    .filter(p -> p.getMember().getId().equals(rankingMember.getMemberId()))
                    .findFirst();
            matchingProfile.ifPresent(profile -> profile.updatePoint(rankingMember.getPoint()));
        }


        log.info("Rank Update Step End");

        return RepeatStatus.FINISHED;
    }
}
