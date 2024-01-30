package com.a708.drwa.member.dto;

import com.a708.drwa.member.type.SocialType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
// 알 수 없는 JSON 필드 무시
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class SocialUserInfoResponse {
    protected String id;
    protected String email;
    protected String name;
    protected String picture;
    @Setter
    protected SocialType socialType;

//    public void setSocialType(SocialType socialType) {
//        log.info("socialType: {}", socialType);
//        this.socialType = socialType;
//        log.info("this{}.socialType: {}",this.hashCode(),  this.socialType);
//    }


}
