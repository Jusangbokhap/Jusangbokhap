package jsbh.Jusangbokhap.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao.api")
public class KakaoPayProperties {
    private String adminKey;
    private String cid;
    private String approvalUrl;
    private String cancelUrl;
    private String failUrl;
    private String orderUrl;
}
