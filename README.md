# GitHub Proxy API

A small Spring Boot application that acts as a proxy for GitHub’s public API. It retrieves repositories for a given user, filters out forks, and includes branch details for each repository.

## Features

- Retrieve repositories for a GitHub user
- Exclude forked repositories
- Include branch name and last commit SHA for each repository
- Return a consistent JSON error response for missing users
- Tested with WireMock to avoid real external HTTP calls

## API

### `GET /users/{username}/repos`

Returns repositories for the given GitHub user.

#### Example response: `200 OK`

```json
[
  {
    "name": "git-consortium",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "master",
        "lastCommitSha": "b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e"
      }
    ]
  }
]
```

#### Example response: 404 Not Found

```json
{
  "status": 404,
  "message": "GitHub user nonexistent not found."
}
```

## Architecture

The project follows a simple layered architecture:

- **Controller** — exposes the REST endpoint
- **Service** — contains the business logic
- **Client** — communicates with GitHub through an HTTP interface

---

## External integration

The application communicates with the GitHub REST API using the following endpoints:

- `/users/{username}/repos`
- `/repos/{owner}/{repo}/branches`

The GitHub base URL is configured via application properties:

```properties
github.api.base-url=https://api.github.com
```

## Testing

Integration tests are implemented using:

- `@SpringBootTest`
- `RestTestClient`
- WireMock

WireMock is used to simulate GitHub responses, ensuring that tests do not depend on external services.

---

## Requirements

- Java 25
- Spring Boot 4
- Gradle (Kotlin DSL)

---

## Run the application

```bash
./gradlew bootRun
```

## Run the tests

```bash
./gradlew test
```
