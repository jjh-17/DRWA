package com.a708.drwa.config;

import com.a708.drwa.member.interceptor.AuthInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;dlsxpf

public class InterceptorConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public InterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 인터셉터를 등록한다.
     * @param registry 인터셉터 레지스트리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }

}
