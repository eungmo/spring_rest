package io.simondev.demoinflearnrestapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    // resource id 등을 설정한다.
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
        // 엑세스가 거부되었을 때, 즉, 접근권한이 없는 경우 어떻게 대응을 할지,
        // http 설정, authenticationManager 등등을 설정할 수 있으나
        // 여기서는 기본설정으로 남기겠다.
    }

    // http 설정
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
            .exceptionHandling() // 인증이 잘 못되거나, 권한이 없는 경우,
                .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // 그 중 권한이 없는 경우, 이 핸들러를 사용
    }
}
