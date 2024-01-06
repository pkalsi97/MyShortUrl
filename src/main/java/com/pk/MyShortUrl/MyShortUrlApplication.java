package com.pk.MyShortUrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MyShortUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyShortUrlApplication.class, args);
	}
}
