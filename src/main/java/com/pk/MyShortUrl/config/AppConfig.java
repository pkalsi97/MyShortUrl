package com.pk.MyShortUrl.config;

import ch.qos.logback.core.util.COWArrayList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {

    private String baseUrl;
    private int timeAllotted;
    private Set<String> reservedPaths;
}