package com.pk.MyShortUrl.config;
// used lombok to reduce boilerplate code
import lombok.Getter;
import lombok.Setter;
//This is used to bind external configuration properties to java code
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**Marks this class as a Configuration property holder for a spring application
 * these values can be externalised for a more flexible application configuration
 */
@Configuration
// Binds the properties in Application properties with prefix "app" to filed of this class
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    public String baseUrl;
    private int timeAllotted;
    private Set<String> reservedPaths;
}
