package com.example.githubproxy;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private final GithubClient githubClient;

    public GithubService(GithubClient githubClient){
        this.githubClient = githubClient;
    }

    public List<Repository> getUserRepositories(String username) {
        try {
            GithubRepo[] repos = this.githubClient.getUserRepositories(username);

            if (repos == null || repos.length == 0) {
                return List.of();
            }

            return Arrays.stream(repos)
                    .filter(repo -> !repo.fork())
                    .map(repo -> {
                        String ownerLogin = repo.owner() != null ? repo.owner().login() : username;

                        GithubRepo.BranchDto[] branchesDto;
                        try {
                            branchesDto = githubClient.getBranches(ownerLogin, repo.name());
                        } catch (HttpClientErrorException.NotFound exception) {
                            branchesDto = new GithubRepo.BranchDto[0];
                        }

                        List<Repository.Branch> branches = branchesDto == null
                                ? List.of()
                                : Arrays.stream(branchesDto)
                                .map(branch -> new Repository.Branch(
                                        branch.name(),
                                        branch.commit() != null ? branch.commit().sha() : null
                                ))
                                .collect(Collectors.toList());
                        return new Repository(repo.name(), ownerLogin, branches);
                    })
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound ex) {
            System.out.println(ex.getMessage());
        } catch (HttpClientErrorException ex) {
            System.out.println(ex.getMessage());
        }
        return List.of();
    }


}
