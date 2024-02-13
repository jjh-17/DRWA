package com.a708.drwa.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증이 필요한 API에 사용하는 어노테이션
 */
// ElementType.METHOD: Method에 붙여줄 수 있다.
@Target({ElementType.METHOD, ElementType.TYPE})
// RetentionPolicy.RUNTIME: 런타임동안 어노테이션 정보를 유지한다.
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {

}
