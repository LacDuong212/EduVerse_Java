package com.eduverse.eduversebe.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    private String clientUrl;
    private Momo momo;
    private Vnpay vnpay;

    @Data
    public static class Momo {
        private String partnerCode;
        private String accessKey;
        private String secretKey;
        private String apiEndpoint;
        private String redirectUrl;
        private String ipnUrl;
    }

    @Data
    public static class Vnpay {
        private String tmnCode;
        private String hashSecret;
        private String url;
        private String returnUrl;
    }
}
