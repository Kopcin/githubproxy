package com.example.githubproxy;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "github.api.base-url=http://localhost:8089"
)
@WireMockTest(httpPort = 8089)
class GithubproxyIntegrationTest {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();

	@BeforeEach
	void resetWiremock() {
		reset();
	}

	private String readJson(String filename) throws Exception {
		Path path = Path.of("src/test/resources", filename);
		return Files.readString(path);
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

		ResponseEntity<String> response = restTemplate.getForEntity(
				"http://localhost:" + port + "/users/octocat/repos",
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		String body = response.getBody();
		assertThat(body).isNotNull();

		// 1 repo (fork odfiltrowany)
		assertThat(body).contains("\"name\":\"git-consortium\"");
		assertThat(body).doesNotContain("\"fork\":true");

		// owner
		assertThat(body).contains("\"ownerLogin\":\"octocat\"");

		// branches
		assertThat(body).contains("\"branches\"");
		assertThat(body).contains("\"name\":\"master\"");
		assertThat(body).contains("\"lastCommitSha\":\"b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e\"");
	}

	@Test
	@DisplayName("Should return 404 response in required format for non-existing GitHub user")
	void shouldReturn404ForNonExistingUser() {
		String url = "http://localhost:" + port +
				"/users/3pignhe0ng3g0n3/repos";

		HttpClientErrorException exception =
				assertThrows(HttpClientErrorException.NotFound.class, () ->
						restTemplate.getForEntity(url, String.class)
				);

		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		String body = exception.getResponseBodyAsString();
		assertThat(body).isNotNull();

		assertThat(body).contains("\"status\":404");
		assertThat(body).contains("\"message\"");
		assertThat(body).contains("GitHub user 3pignhe0ng3g0n3 not found");
	}

}
