# BDD Testing Framework
## Introduction
TBD
## Installation and Setup
### Prerequisites
- Bazel
- Java for step file development (optional)
## Usage Guide
### Write test
Test cases are written in Gherkin syntax. Below is an example:
```
Feature: User login
  Scenario: pre check environmental variables are set or not
    Given URL: https://iot.agora-lab.w3n.io
    Given validate below environments are all set
      | KEYCLOAK_BASE_URL |
      | KEYCLOAK_USERNAME |
      | KEYCLOAK_PASSWORD |
      | KEYCLOAK_REALM    |
      |KEYCLOAK_CLIENT_ID |
    Then login to Keycloak, and save token to "TOKEN_FROM_KEYCLOAK"
```
### Building the test target
Build an executor binary and an executor image.
```
load("//ns/testkube/executor-images/cucumber:defs.bzl", "cucumber_jvm_image")

cucumber_jvm_image(
    name = "user_login_test",
    cucumber_args = [
        "--plugin",
        "pretty",
    ],
    features = [
        "user_login.feature",
    ],
)

```

### Run test
To run tests, use the following Bazel command:
```
bazel run //path/to/build/file:user_login_test_bin
```
To bake the test executor binary in a Docker image and load it to your local repository, use the following Bazel command:
```
bazel run //path/to/build/file:user_login_test.load
```
To run the above Docker image, use the following Docker command:
```
docker run path/to/build/file:user_login_test
```

## Best Practice
### Test Writing
- Write clear and concise steps.
- Reuse step definitions where possible.
- Avoid unnecessary complexity in test scenarios.
### Framework Usage
- Regularly update dependencies.
- Keep custom steps modular and well-documented.
- Use meaningful names for scenarios and steps
