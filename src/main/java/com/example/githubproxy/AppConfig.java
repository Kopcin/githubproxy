package com.example.githubproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class AppConfig {

    @Value("${github.api.base-url}")
    private String githubBaseUrl;

    @Bean
    public GithubClient githubClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(githubBaseUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

        return factory.createClient(GithubClient.class);
    }
}
