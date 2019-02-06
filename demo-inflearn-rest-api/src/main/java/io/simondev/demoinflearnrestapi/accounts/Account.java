package io.simondev.demoinflearnrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String email;
    private String password;

    // 여러 개의 Enum을 갖을 수 있도록 설정
    // 기본적으로 모든 컬렉션은 LAZY Fetch인데,
    // 이 경우에는 가져올 롤도 별로 없고, 거의 매번 필요한 정보라 EAGER 모드로 설정
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
