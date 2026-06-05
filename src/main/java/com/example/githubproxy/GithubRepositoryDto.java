package com.example.githubproxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryDto(String name, Owner owner, boolean fork) {
    public record Owner(String login) {}
    public record BranchDto(String name, Commit commit) {
        public record Commit(String sha) {}
    }
}
