package com.a708.drwa.rank.controller;

import com.a708.drwa.rank.dto.response.RankResponse;
import com.a708.drwa.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
public class RankController {
    private final RankService rankService;

    @GetMapping("/init")
    public ResponseEntity<Void> init(){
        rankService.initAllRanks();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/clear-init")
    public ResponseEntity<Void> clearAndInit(){
        rankService.clearAndInitAllRanks();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RankResponse>> allRanks(){
        List<RankResponse> results = rankService.findAllRanksWithDto();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
