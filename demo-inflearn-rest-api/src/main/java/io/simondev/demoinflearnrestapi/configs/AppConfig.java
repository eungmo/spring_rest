package io.simondev.demoinflearnrestapi.configs;

import io.simondev.demoinflearnrestapi.accounts.Account;
import io.simondev.demoinflearnrestapi.accounts.AccountRole;
import io.simondev.demoinflearnrestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {
    // 공용으로 쓸 것이기 때문에 빈으로 등록하자
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 애플리케이션 시작 시에 테스트용 유저를 하나 만들어서 저장해보자
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account seungmo = Account.builder()
                        .email("seungmo@email.com")
                        .password("seungmo")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(seungmo);

            }
        };
    }
}
