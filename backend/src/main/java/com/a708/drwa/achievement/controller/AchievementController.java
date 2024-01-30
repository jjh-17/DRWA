package com.a708.drwa.achievement.controller;

import com.a708.drwa.achievement.dto.request.AddAchievementRequest;
import com.a708.drwa.achievement.dto.request.UpdateAchievementRequest;
import com.a708.drwa.achievement.dto.response.AchievementResponse;
import com.a708.drwa.achievement.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievement")
public class AchievementController {
    private final AchievementService achievementService;

    /**
     * For Admin
     */
    @GetMapping("/init")
    public ResponseEntity<Void> init(){
        achievementService.initAllTitle();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("clear-init")
    public ResponseEntity<Void> clearInit(){
        achievementService.clearAndInitAllTitle();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addAchievement(@RequestBody AddAchievementRequest addAchievementRequest){
        achievementService.addTitle(addAchievementRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateAchievement(@RequestBody UpdateAchievementRequest updateAchievementRequest){
        achievementService.updateTitle(updateAchievementRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * For User
     */

    @GetMapping("/check/{memberId}")
    public ResponseEntity<Void> checkGainAchievement(@PathVariable Integer memberId){
        achievementService.check(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<AchievementResponse>> myAchievements(@PathVariable Integer memberId){
        List<AchievementResponse> results = achievementService.findTitlesByMemberId(memberId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/representative/{memberId}")
    public ResponseEntity<AchievementResponse> myRepresentativeAchievement(@PathVariable Integer memberId){
        AchievementResponse result = achievementService.findRepresentativeTitleByMemberId(memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AchievementResponse>> findAllAchievements(){
        List<AchievementResponse> results = achievementService.findAllTitles();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


}
