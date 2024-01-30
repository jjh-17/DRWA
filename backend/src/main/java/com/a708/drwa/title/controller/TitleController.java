package com.a708.drwa.title.controller;

import com.a708.drwa.title.dto.request.AddTitleRequest;
import com.a708.drwa.title.dto.request.UpdateTitleRequest;
import com.a708.drwa.title.dto.response.TitleResponse;
import com.a708.drwa.title.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/title")
public class TitleController {
    private final TitleService titleService;

    /**
     * For Admin
     */
    @GetMapping("/init")
    public ResponseEntity<Void> init(){
        titleService.initAllTitle();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("clear-init")
    public ResponseEntity<Void> clearInit(){
        titleService.clearAndInitAllTitle();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addTitle(@RequestBody AddTitleRequest addTitleRequest){
        titleService.addTitle(addTitleRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateTitle(@RequestBody UpdateTitleRequest updateTitleRequest){
        titleService.updateTitle(updateTitleRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * For User
     */

    @GetMapping("/check/{memberId}")
    public ResponseEntity<Void> checkGainTitle(@PathVariable Integer memberId){
        titleService.check(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<TitleResponse>> myTitles(@PathVariable Integer memberId){
        List<TitleResponse> results = titleService.findTitlesByMemberId(memberId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<TitleResponse> myRepresentativeTile(@PathVariable Integer memberId){
        TitleResponse result = titleService.findRepresentativeTitleByMemberId(memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TitleResponse>> findAllTitles(){
        List<TitleResponse> results = titleService.findAllTitles();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


}
