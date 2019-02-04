package io.simondev.demoinflearnrestapi.configs;

import io.simondev.demoinflearnrestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
// @EnableWebSecurity 애노테이션을 설정하고, WebSecurityConfigurerAdapter를 상속받는 순간
// Spring Boot가 제공해주는 default Spring Security 설정은 더 이상 적용이 되지 않는다.
// 즉, 여기에 적용하는 설정만 적용된다.
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService; // 스프링 시큐리티의 UserDetailsService

    @Autowired
    PasswordEncoder passwordEncoder;

    // OAuth 토큰을 저장하는 곳인데, 빈으로 등록한다.
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    // AuthenticationManager를 빈으로 노출시켜준다.
    // 그러면 다른 AuthorizationServer나 ResourceServer가 참조할 수 있게 된다.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // AuthenticationManager를 어떻게 만들지 재정의
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService) // UserDetailsService 설정
                .passwordEncoder(passwordEncoder); // PasswordEncoder 설정
    }

    // 필터를 적용할지 말지 걸러낸다. (스프링 시큐리티에 들어오지도 않는다)
    // static이나 문서 같은 경우에는 서버에서 조금이라도 덜 일하게 하려면,
    // WebSecurity를 적용하여 배재시키는게 더 좋은 방법이다.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 리소스
    }

//    // 스프링 시큐리티로 들어온 다음, 특정 파일들을 anonymous로 허용한다.
//    // 죽, 아무나 접근할 수 있는 요청을 만드는 것이다.
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .mvcMatchers("/docs/index.html").anonymous()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).anonymous();
//    }
}
