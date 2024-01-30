package com.a708.drwa.ranking.controller;

import com.a708.drwa.ranking.dto.SearchByNicknameResponse;
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {
    private final RankingService rankingService;
    @GetMapping("/top20")
    public ResponseEntity<List<RankingMember>> top20Ranks(){
        List<RankingMember> results = rankingService.findTop20Ranks();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<SearchByNicknameResponse> nicknameWithNeighbors(@PathVariable String nickname){
        SearchByNicknameResponse results = rankingService.findTop10Bottom10(nickname);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     *
     * @param category
     * rank:food, rank:economy, rank:sports, rank:animal, rank:shopping, rank:love, rank:politics
     * rank:social, rank:person, rank:culture, rank:etc
     * @return
     */
    @GetMapping("/top20/{category}")
    public ResponseEntity<List<RankingMember>> top20RanksByCategory(@PathVariable String category){
        List<RankingMember> results = rankingService.findTop20ByCategory(category);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
