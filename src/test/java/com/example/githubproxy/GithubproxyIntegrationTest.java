package com.example.githubproxy;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "github.api.base-url=http://localhost:8089"
)
@AutoConfigureRestTestClient
@WireMockTest(httpPort = 8089)
class GithubproxyIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @BeforeEach
    void setup() {
        reset();
    }

    private String readJson(final String filename) throws Exception {
        return Files.readString(Path.of("src/test/resources", filename));
    }

    @Test
    @DisplayName("Should return non-fork repositories with branches and required fields")
    void shouldReturnRepositoriesWithoutForks() throws Exception {

        String reposJson = readJson("github_api_repos.json");
        String branchesJson = readJson("github_api_branches.json");

        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(reposJson)));

        stubFor(get(urlEqualTo("/repos/octocat/git-consortium/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(branchesJson)));

        String response = restTestClient.get()
                .uri("/users/octocat/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String expected = """
                [
                  {
                    "name":"git-consortium",
                    "ownerLogin":"octocat",
                    "branches":[
                      {
                        "name":"master",
                        "lastCommitSha":"b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e"
                      }
                    ]
                  }
                ]
                """;

        assertEquals(expected, response, STRICT);
    }

    @Test
    @DisplayName("Should return 404 response in required format for non-existing GitHub user")
    void shouldReturn404ForNonExistingUser() throws Exception {

        stubFor(get(urlEqualTo("/users/nonexistent/repos"))
                .willReturn(aResponse().withStatus(404)));

        String response = restTestClient.get()
                .uri("/users/nonexistent/repos")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String expected = """
                {
                  "status":404,
                  "message":"GitHub user nonexistent not found."
                }
                """;

        assertEquals(expected, response, STRICT);
    }

    @Test
    @DisplayName("Should return empty list when user has no repositories")
    void shouldReturnEmptyListWhenUserHasNoRepositories() throws Exception {

        stubFor(get(urlEqualTo("/users/empty/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        String response = restTestClient.get()
                .uri("/users/empty/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertEquals("[]", response, STRICT);
    }
}
