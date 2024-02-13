package com.a708.drwa.auth.interceptor;

import com.a708.drwa.auth.annotation.AuthRequired;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인증이 필요한 요청에 대해 인증을 확인하는 인터셉터
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // handler가 메소드에서 호출된 것인지 확인.
        // 그렇지 않다면 모두 패스.
        if(!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 호출된 메소드의 Annotation이 AuthRequired인지 확인.
        AuthRequired auth = handlerMethod.getMethodAnnotation(AuthRequired.class);
        if(auth == null)
            return true;

        // accessToken 확인.
        String accessToken = request.getHeader("Authorization");
        if(accessToken != null) {
            // 토큰 유효성 검사
            jwtUtil.validCheck(accessToken);

            // 토큰 내 정보로 유효한 멤버인지 확인
            // 추후 관리자 기능 추가하면 권한 확인으로 변경 가능
            if(memberRepository.existsById(jwtUtil.getMember(accessToken).getMemberId()))
                return true;
        }

        // 권한이 없음을 클라이언트에 전송해준다.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
