package com.example.githubproxy;

import java.util.List;

public record RepositoryResponse(String name, String ownerLogin, List<BranchResponse> branches) {
    public record BranchResponse(String name, String lastCommitSha) {}
}
