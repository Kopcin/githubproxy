package com.example.githubproxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class AppConfig {

    @Bean
    public GithubClient githubClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

        return factory.createClient(GithubClient.class);
    }
}
