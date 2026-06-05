package com.example.githubproxy;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

@Service
public class GithubService {
    private final GithubClient githubClient;

    public GithubService(GithubClient githubClient){
        this.githubClient = githubClient;
    }

    public List<GithubRepo> getUserRepositories(String username) {
        try {
            GithubRepo[] repos = this.githubClient.getUserRepositories(username);

            if (repos == null || repos.length == 0) {
                return List.of();
            }

            return Arrays.asList(repos);
        } catch (HttpClientErrorException.NotFound ex) {
            System.out.println(ex.getMessage());
        } catch (HttpClientErrorException ex) {
            System.out.println(ex.getMessage());
        }
        return List.of();
    }


}
