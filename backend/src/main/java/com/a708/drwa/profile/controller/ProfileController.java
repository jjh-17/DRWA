package com.a708.drwa.profile.controller;

import com.a708.drwa.profile.dto.request.AddProfileRequest;
import com.a708.drwa.profile.dto.request.UpdateProfileRequest;
import com.a708.drwa.profile.dto.response.ProfileResponse;
import com.a708.drwa.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/new")
    public ResponseEntity<Void> addProfile(@RequestBody AddProfileRequest addProfileRequest){
        profileService.addProfile(addProfileRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest){
        profileService.updateProfile(updateProfileRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ProfileResponse> findProfile(@PathVariable Integer memberId){
        ProfileResponse profile = profileService.findProfileByMemberId(memberId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }


    /**
     * Get All Member's Profiles
     */

    @GetMapping("/all")
    public ResponseEntity<List<ProfileResponse>> findAllProfiles(){
        List<ProfileResponse> results = profileService.findAll();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


}
