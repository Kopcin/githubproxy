package com.example.githubproxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryDto(String name, Owner owner, boolean fork) {
    public record Owner(String login) {}
}
