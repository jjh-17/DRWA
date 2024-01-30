package com.a708.drwa.title.service;

import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.service.MemberService;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.exception.ProfileErrorCode;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.title.domain.MemberTitle;
import com.a708.drwa.title.domain.Title;
import com.a708.drwa.title.dto.request.AddTitleRequest;
import com.a708.drwa.title.dto.request.UpdateTitleRequest;
import com.a708.drwa.title.dto.response.TitleResponse;
import com.a708.drwa.title.exception.TitleErrorCode;
import com.a708.drwa.title.repository.MemberTitleRepository;
import com.a708.drwa.title.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TitleService {
    private final TitleRepository titleRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MemberTitleRepository memberTitleRepository;

    @Transactional
    public void initAllTitle(){
        List<Title> allTitles = createAllTitles();
        titleRepository.saveAll(allTitles);
    }

    @Transactional
    public void clearAndInitAllTitle(){
        titleRepository.deleteAll();
        List<Title> allTitles = createAllTitles();
        titleRepository.saveAll(allTitles);
    }

    @Transactional
    public void addTitle(AddTitleRequest addTitleRequest){
        Title title = Title.builder()
                .name(addTitleRequest.getName())
                .description(addTitleRequest.getDescription())
                .build();
        titleRepository.save(title);
    }

    @Transactional
    public void updateTitle(UpdateTitleRequest updateTitleRequest){
        Title title = titleRepository.findById(updateTitleRequest.getId())
                .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_NOT_FOUND));
        title.updateTitle(updateTitleRequest.getName(), updateTitleRequest.getDescription());
    }

    @Transactional
    public void check(Integer memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND));

        // 승 관련 칭호 확인 후, 획득 가능하면 획득
        checkWinRelatedTitle(member, profile, 0);

        // 판수 관련 칭호 확인 후, 획득 가능하면 획득
        checkTotalGameCountRelatedTitle();
    }

    public List<TitleResponse> findAllTitles(){
        return titleRepository.findAll().stream()
                .map(title -> TitleResponse.builder()
                        .name(title.getName())
                        .description(title.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public List<TitleResponse> findTitlesByMemberId(Integer memberId) {
        return memberTitleRepository.findByMemberId(memberId)
                .stream()
                .map(memberTitle -> TitleResponse.builder()
                        .name(memberTitle.getTitle().getName())
                        .description(memberTitle.getTitle().getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public TitleResponse findRepresentativeTitleByMemberId(Integer memberId) {
        return memberTitleRepository.findByMemberId(memberId)
                .stream()
                .filter(memberTitle -> memberTitle.isSelected())
                .map(memberTitle -> TitleResponse.builder()
                        .name(memberTitle.getTitle().getName())
                        .description(memberTitle.getTitle().getDescription())
                        .build())
                .findFirst()
                .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_REPRESENTATIVE_NOT_FOUND));
    }


    private List<Title> createAllTitles() {
        return List.of(
            craeteTitle("청렴한 배심원", "배심원 100판 참여"),
            craeteTitle("토론 하수", "1승 달성"),
            craeteTitle("토론 중수", "10승 달성"),
            craeteTitle("토론 고수", "50승 달성"),
            craeteTitle("논리왕", "100승 달성")
        );
    }

    private Title craeteTitle(String name, String description){
        return Title.builder()
                .name(name)
                .description(description)
                .build();
    }

    private void checkTotalGameCountRelatedTitle() {
        /**
         * TODO "청렴한 배심원" 칭호 확인
         */
    }

    private void checkWinRelatedTitle(Member member, Profile profile, int winCount) {
        if(winCount >= 100){
            Title win100Title = titleRepository.findByName("논리왕")
                    .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_NOT_FOUND));

            MemberTitle memberTitle = MemberTitle.builder()
                    .member(member)
                    .title(win100Title)
                    .build();
            memberTitleRepository.save(memberTitle);
        } else if(winCount >= 50){
            Title win100Title = titleRepository.findByName("토론 고수")
                    .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_NOT_FOUND));

            MemberTitle memberTitle = MemberTitle.builder()
                    .member(member)
                    .title(win100Title)
                    .build();
            memberTitleRepository.save(memberTitle);
        } else if(winCount >= 10){
            Title win100Title = titleRepository.findByName("토론 중수")
                    .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_NOT_FOUND));

            MemberTitle memberTitle = MemberTitle.builder()
                    .member(member)
                    .title(win100Title)
                    .build();
            memberTitleRepository.save(memberTitle);

        } else if(winCount >= 1){
            Title win100Title = titleRepository.findByName("토론 하수")
                    .orElseThrow(() -> new GlobalException(TitleErrorCode.TITLE_NOT_FOUND));

            MemberTitle memberTitle = MemberTitle.builder()
                    .member(member)
                    .title(win100Title)
                    .build();
            memberTitleRepository.save(memberTitle);
        }
    }
}
