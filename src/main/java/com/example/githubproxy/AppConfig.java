package com.example.githubproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(GithubClient.class)
class AppConfig {

    @Bean
    RestClient.Builder restClientBuilder(@Value("${github.api.base-url}") String githubBaseUrl) {
        return RestClient.builder()
                .baseUrl(githubBaseUrl);
    }
}
