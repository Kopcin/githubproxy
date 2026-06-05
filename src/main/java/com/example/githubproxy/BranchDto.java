package com.example.githubproxy;

public record BranchDto(String name, Commit commit) {
    public record Commit(String sha) {}
}
