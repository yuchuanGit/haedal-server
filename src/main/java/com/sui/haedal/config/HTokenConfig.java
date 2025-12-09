package com.sui.haedal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sui-config.htoken")
public class HTokenConfig {

    private String htokenTemplate;

    private String htokenReplace;


}
