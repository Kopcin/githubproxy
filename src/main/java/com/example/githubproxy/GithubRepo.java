package com.example.githubproxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepo {
    private String name;

    public GithubRepo() {}

    public String getName() {
        return name;
    }

}
