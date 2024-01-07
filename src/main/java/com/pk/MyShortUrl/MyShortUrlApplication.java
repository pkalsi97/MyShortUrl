package com.pk.MyShortUrl;

import com.pk.MyShortUrl.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(AppConfig.class)
@EnableScheduling
public class MyShortUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyShortUrlApplication.class, args);
	}
}
