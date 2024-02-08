package com.a708.drwa.member.interceptor;

import com.a708.drwa.annotation.AuthRequired;
import com.a708.drwa.member.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인증이 필요한 요청에 대해 인증을 확인하는 인터셉터
 */
public class AuthInterceptor implements HandlerInterceptor {
    private final String ACCESSTOKEN_HEADER = "Authorization";

    private final JWTUtil jwtUtil;

    private AuthInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // handler가 메소드에서 호출된 것인지 확인.
        // 그렇지 않다면 모두 패스.
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }


        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 호출된 메소드의 Annotation이 AuthRequired인지 확인.
        if(
                handlerMethod.getMethodAnnotation(AuthRequired.class) != null ||
                        handlerMethod.getBeanType().getAnnotation(AuthRequired.class) != null
        ) {

            // accessToken 확인.
            String accessToken = request.getHeader(ACCESSTOKEN_HEADER);

            if(accessToken != null) {
                if(jwtUtil.validCheck(accessToken)) {
                    return true;
                }
            }

            // 권한이 없음을 클라이언트에 전송해준다.
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // AuthRequired가 붙지 않은 메소드인 경우 모두 통과
        return true;
    }
}
