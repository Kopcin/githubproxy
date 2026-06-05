package com.example.githubproxy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private final GithubClient githubClient;

    public GithubService(GithubClient githubClient){
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getUserRepositories(String username) {
        try {
            GithubRepositoryDto[] repos = this.githubClient.getUserRepositories(username);

            if (repos == null || repos.length == 0) {
                return List.of();
            }

            return Arrays.stream(repos)
                    .filter(repo -> !repo.fork())
                    .map(repo -> {
                        String ownerLogin = repo.owner() != null ? repo.owner().login() : username;

                        GithubRepositoryDto.BranchDto[] branchesDto;
                        try {
                            branchesDto = githubClient.getBranches(ownerLogin, repo.name());
                        } catch (HttpClientErrorException.NotFound exception) {
                            branchesDto = new GithubRepositoryDto.BranchDto[0];
                        }

                        List<RepositoryResponse.BranchResponse> branches = branchesDto == null
                                ? List.of()
                                : Arrays.stream(branchesDto)
                                .map(branch -> new RepositoryResponse.BranchResponse(
                                        branch.name(),
                                        branch.commit() != null ? branch.commit().sha() : null
                                ))
                                .collect(Collectors.toList());
                        return new RepositoryResponse(repo.name(), ownerLogin, branches);
                    })
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "GitHub user " + username + " not found.");
        }
    }
}
