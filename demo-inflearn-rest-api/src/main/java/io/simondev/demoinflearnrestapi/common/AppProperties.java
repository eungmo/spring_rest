package io.simondev.demoinflearnrestapi.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

// 빈으로 등록
@Component
// myApp은 prefix
// spring-boot-configuration-processor 의존성을 추가해야 빌드할 때 자동완성할 수 있게 된다.
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {
    @NotEmpty
    private String adminUsername;
    @NotEmpty
    private String adminPassword;
    @NotEmpty
    private String userUsername;
    @NotEmpty
    private String userPassword;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;
}
