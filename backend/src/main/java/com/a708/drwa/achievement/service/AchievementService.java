package com.a708.drwa.achievement.service;

import com.a708.drwa.achievement.domain.Achievement;
import com.a708.drwa.achievement.domain.MemberAchievement;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordRepository;
import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.achievement.dto.request.AddAchievementRequest;
import com.a708.drwa.achievement.dto.request.UpdateAchievementRequest;
import com.a708.drwa.achievement.dto.response.AchievementResponse;
import com.a708.drwa.achievement.exception.AchievementErrorCode;
import com.a708.drwa.achievement.repository.MemberAchievementRepository;
import com.a708.drwa.achievement.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final MemberRepository memberRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final RecordRepository recordRepository;
    private final GameInfoRepository gameInfoRepository;

    @Transactional
    public void initAllTitle(){
        List<Achievement> allAchievements = createAllTitles();
        achievementRepository.saveAll(allAchievements);
    }

    @Transactional
    public void clearAndInitAllTitle(){
        achievementRepository.deleteAll();
        List<Achievement> allAchievements = createAllTitles();
        achievementRepository.saveAll(allAchievements);
    }

    @Transactional
    public void addTitle(AddAchievementRequest addAchievementRequest){
        Achievement achievement = Achievement.builder()
                .name(addAchievementRequest.getName())
                .description(addAchievementRequest.getDescription())
                .build();
        achievementRepository.save(achievement);
    }

    @Transactional
    public void updateTitle(UpdateAchievementRequest updateAchievementRequest){
        Achievement achievement = achievementRepository.findById(updateAchievementRequest.getId())
                .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));
        achievement.updateTitle(updateAchievementRequest.getName(), updateAchievementRequest.getDescription());
    }

    @Transactional
    public void check(Integer memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<MemberAchievement> memberAchievements = memberAchievementRepository.findByMemberId(member.getId());

        List<Achievement> currentAchievements = memberAchievements.stream()
                .map(memberAchievement -> achievementRepository
                        .findById(memberAchievement.getId())
                        .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND)))
                .collect(Collectors.toList());

        // 승 관련 칭호 확인 후, 획득 가능하면 획득
        checkWinRelatedTitle(member, 0, currentAchievements);

        // 판수 관련 칭호 확인 후, 획득 가능하면 획득
        checkTotalGameCountRelatedTitle(member, currentAchievements);
    }

    public List<AchievementResponse> findAllTitles(){
        return achievementRepository.findAll().stream()
                .map(achievement -> AchievementResponse.builder()
                        .name(achievement.getName())
                        .description(achievement.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public List<AchievementResponse> findTitlesByMemberId(Integer memberId) {
        return memberAchievementRepository.findByMemberId(memberId)
                .stream()
                .map(memberAchievement -> AchievementResponse.builder()
                        .name(memberAchievement.getAchievement().getName())
                        .description(memberAchievement.getAchievement().getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public AchievementResponse findRepresentativeTitleByMemberId(Integer memberId) {
        return memberAchievementRepository.findByMemberId(memberId)
                .stream()
                .filter(memberAchievement -> memberAchievement.isSelected())
                .map(memberAchievement -> AchievementResponse.builder()
                        .name(memberAchievement.getAchievement().getName())
                        .description(memberAchievement.getAchievement().getDescription())
                        .build())
                .findFirst()
                .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_REPRESENTATIVE_NOT_FOUND));
    }


    /**
     * TODO Refactor: 칭호 이름 ENUM 관리
     */
    private void checkTotalGameCountRelatedTitle(Member member, List<Achievement> currentAchievements) {
        boolean isAlreadyAchieve = currentAchievements.stream()
                .anyMatch(achievement -> achievement.getName().equals("청렴한 배심원"));
        if(isAlreadyAchieve) return ;

        List<Record> records = recordRepository.findByMemberId(member.getId());
        long totalJurorCount = records.stream()
                .filter(record -> record.getTeam().equals(Team.JUROR) && !record.getGameInfo().isPrivate())
                .count();

        if(totalJurorCount >= 100){
            Achievement goodJurorAchievement = achievementRepository.findByName("청렴한 배심원")
                    .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));

            MemberAchievement memberAchievement = MemberAchievement.builder()
                    .member(member)
                    .achievement(goodJurorAchievement)
                    .build();
            memberAchievementRepository.save(memberAchievement);
        }
    }

    private void checkWinRelatedTitle(Member member, int winCount, List<Achievement> currentAchievements) {
        if(winCount >= 100){
            boolean isAlreadyAchieve = currentAchievements.stream()
                    .anyMatch(achievement -> achievement.getName().equals("논리왕"));
            if(isAlreadyAchieve) return ;

            Achievement win100Achievement = achievementRepository.findByName("논리왕")
                    .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));

            MemberAchievement memberAchievement = MemberAchievement.builder()
                    .member(member)
                    .achievement(win100Achievement)
                    .build();
            memberAchievementRepository.save(memberAchievement);
        } else if(winCount >= 50){
            boolean isAlreadyAchieve = currentAchievements.stream()
                    .anyMatch(achievement -> achievement.getName().equals("토론 고수"));
            if(isAlreadyAchieve) return ;

            Achievement win50Achievement = achievementRepository.findByName("토론 고수")
                    .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));

            MemberAchievement memberAchievement = MemberAchievement.builder()
                    .member(member)
                    .achievement(win50Achievement)
                    .build();
            memberAchievementRepository.save(memberAchievement);
        } else if(winCount >= 10){
            boolean isAlreadyAchieve = currentAchievements.stream()
                    .anyMatch(achievement -> achievement.getName().equals("토론 중수"));
            if(isAlreadyAchieve) return ;

            Achievement win10Achievement = achievementRepository.findByName("토론 중수")
                    .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));

            MemberAchievement memberAchievement = MemberAchievement.builder()
                    .member(member)
                    .achievement(win10Achievement)
                    .build();
            memberAchievementRepository.save(memberAchievement);

        } else if(winCount >= 1){
            boolean isAlreadyAchieve = currentAchievements.stream()
                    .anyMatch(achievement -> achievement.getName().equals("토론 하수"));
            if(isAlreadyAchieve) return ;

            Achievement win1Achievement = achievementRepository.findByName("토론 하수")
                    .orElseThrow(() -> new GlobalException(AchievementErrorCode.TITLE_NOT_FOUND));

            MemberAchievement memberAchievement = MemberAchievement.builder()
                    .member(member)
                    .achievement(win1Achievement)
                    .build();
            memberAchievementRepository.save(memberAchievement);
        }
    }

    private List<Achievement> createAllTitles() {
        return List.of(
                craeteTitle("청렴한 배심원", "배심원 100판 참여"),
                craeteTitle("토론 하수", "1승 달성"),
                craeteTitle("토론 중수", "10승 달성"),
                craeteTitle("토론 고수", "50승 달성"),
                craeteTitle("논리왕", "100승 달성")
        );
    }

    private Achievement craeteTitle(String name, String description){
        return Achievement.builder()
                .name(name)
                .description(description)
                .build();
    }
}
