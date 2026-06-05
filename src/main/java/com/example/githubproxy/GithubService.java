package com.example.githubproxy;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GithubService {
    private final GithubClient githubClient;

    public GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getUserRepositories(String username) {

        GithubRepositoryDto[] repos = this.githubClient.getUserRepositories(username);

        if (repos == null || repos.length == 0) {
            return List.of();
        }

        return Arrays.stream(repos)
                .filter(repo -> !repo.fork())
                .map(repo -> {

                    BranchDto[] branches = githubClient.getBranches(repo.owner().login(), repo.name());

                    List<RepositoryResponse.BranchResponse> branchResponses =
                            (branches == null ? List.<BranchDto>of() : Arrays.asList(branches))
                                    .stream()
                                    .map(branch -> new RepositoryResponse.BranchResponse(
                                            branch.name(),
                                            branch.commit().sha()
                                    ))
                                    .toList();

                    return new RepositoryResponse(repo.name(), repo.owner().login(), branchResponses);
                })
                .toList();
    }
}
