package com.a708.drwa.debate.controller;

import com.a708.drwa.global.dto.ResponseDto;
import com.a708.drwa.debate.service.OpenViduService;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/openvidu/api/sessions")
public class OpenViduController {
    private final OpenViduService openViduService;

    @GetMapping("create/session")
    public void createSession(@RequestParam Long debateId) throws OpenViduJavaClientException, OpenViduHttpException {
        openViduService.createSession(debateId);
    }

    @GetMapping("enter/session")
    public ResponseEntity<ResponseDto> enterSession(@RequestParam Long debateId) throws OpenViduJavaClientException, OpenViduHttpException {
        ResponseDto responseDto = new ResponseDto();
        String token = openViduService.enterSession(debateId);
        responseDto.setBody(token);

        return ResponseEntity.ok(responseDto);
    }

}