package io.simondev.demoinflearnrestapi.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 파라미터에 붙일 수 있고
@Target(ElementType.PARAMETER)
// 런타임까지 애노테이션 정보를 유지
@Retention(RetentionPolicy.RUNTIME)
//@AuthenticationPrincipal(expression = "account")
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}
