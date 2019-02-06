package io.simondev.demoinflearnrestapi.configs;

import io.simondev.demoinflearnrestapi.accounts.AccountService;
import io.simondev.demoinflearnrestapi.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

// 이 서버 설정을 하면, 인증 토큰이 발급받을 수 있어야 한다.
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    AppProperties appProperties;

    // 여기서는 PasswordEncoder를 설정
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 이 PasswordEncoder를 사용해서 client_secret을 확인한다.
        security.passwordEncoder(passwordEncoder);
    }

    // Client를 설정
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // jdbc를 사용해 실제 DB에서 관리하는게 이상적이다.
                .withClient(appProperties.getClientId()) // 인메모리로 myApp이란 클라이언트 생성
                // OAuth Token을 발급할 때, refresh_token도 같이 발급해주는데,
                // 이 refresh_token을 갖고 새로운 access token을 발급받을 수 있게 해준다.
                .authorizedGrantTypes("password", "refresh_token") // 지원할 grant type
                .scopes("read", "write") // 앱에서 정의하기 나름이니, 임의의 값을 정의
                .secret(this.passwordEncoder.encode(appProperties.getClientSecret())) // 이 앱의 secret
                .accessTokenValiditySeconds(10 * 60) // Token이 유효한 시간 - 10분
                .refreshTokenValiditySeconds(60 * 60); // Refresh Token이 유효한 시간 - 1시간
    }

    // Endpoints는 AthenticationManager(유저 인증정보를 갖고 있는 애), Token Store와 UserDetailsService 설정
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}
