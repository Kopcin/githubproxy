package com.example.githubproxy;

import java.util.List;

public record Repository(String name, String ownerLogin, List<Branch> branches) {
    public record Branch(String name, String lastCommitSha) {}
}
