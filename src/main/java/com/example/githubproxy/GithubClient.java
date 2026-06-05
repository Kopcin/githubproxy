package com.example.githubproxy;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GithubClient {

    @GetExchange("/users/{username}/repos")
    GithubRepo[] getUserRepositories(@PathVariable String username);

    @GetExchange("/repos/{owner}/{repo}/branches")
    GithubRepo.BranchDto[] getBranches(@PathVariable String owner, @PathVariable String repo);
}
