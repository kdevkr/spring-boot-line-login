package com.example.demo.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "line.oauth")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class LineOAuthProperties {
    private String callbackUrl;
    private String channelId;
    private String channelSecret;
}
