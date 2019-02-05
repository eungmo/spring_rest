package io.simondev.demoinflearnrestapi.accounts;

import io.simondev.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        String password = "seungmo";
        String username = "joesengmo@gmal.com";

        // Given
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        //this.accountRepository.save(account);
        this.accountService.saveAccount(account);

        // When
        UserDetailsService userDetailsService = (UserDetailsService)accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    @TestDescription("username로 불려오려다가 실패한 예외 테스트")
    public void findByUsernameFail() {
        /*
        // 이런 식으로 하면 코드가 장황하긴 하지만 많은 것을 체크 가능
        String username = "random@gmail.com";

        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed"); // 테스트 실패를 명시
        } catch (UsernameNotFoundException e) { // 에러 객체를 갖고 와서, 많은 것을 확인할 수 있다.
            assertThat(e.getMessage()).containsSequence(username); // 에러 메시지가 username을 담고 있는지
        }
        */

        // 코드는 간결하지만 expected를 먼저 적어야 한다는 것을 조심해야 한다.
        String username = "random@gmail.com";

        // Expected: 발생할 예외를 미리 적어준다.
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When
        accountService.loadUserByUsername(username);
    }
}