package com.a708.drwa.member.dto;

import com.a708.drwa.member.type.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;


@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoResponse extends SocialUserInfoResponse{

    /**
     * JSON 응답을 Map으로 변환하여 중첩된 response 필드를 언패킹한다.
     * @param response JSON 응답
     */
    @JsonProperty("response")
    private void unpackNestedResponse(Map<String, Object> response) {
        this.email = (String) response.get("email");
        this.name = (String) response.get("name");
        this.picture = (String) response.get("profile_image");
        this.id = (String) response.get("id");
    }
}
