# Calculator DevOps --- End-to-End CI/CD Pipeline

A Spring Boot calculator REST API built with Java 21 and Maven, tested
with JUnit 5 and Spring MockMvc, packaged as a multi-stage Docker image,
and deployed using Docker Compose behind an Nginx reverse proxy. Jenkins
automates the build, test, image publishing, deployment, and
health-verification steps.

> **Status as of 29 October 2026:** Jenkins Build #7 completed
> successfully. The Docker image was pushed to Docker Hub, and the API
> and Nginx Compose services were both observed healthy. Automatic
> GitHub triggering and email notifications are not yet
> configured/verified.

## Contents

-   [Overview and architecture](#overview-and-architecture)
-   [Technology stack](#technology-stack)
-   [Repository structure](#repository-structure)
-   [API endpoints](#api-endpoints)
-   [Run locally](#run-locally)
-   [Docker](#docker)
-   [Docker Compose](#docker-compose)
-   [Tests](#tests)
-   [Jenkins CI/CD pipeline](#jenkins-cicd-pipeline)
-   [Git workflow](#git-workflow)
-   [Troubleshooting and security](#troubleshooting-and-security)
-   [Status and remaining work](#status-and-remaining-work)
-   [Project links](#project-links)

## Overview and architecture

The application supports addition, subtraction, multiplication, and
division through HTTP GET endpoints. Responses are JSON. Invalid
requests and division by zero are handled with HTTP `400 Bad Request`
and a structured JSON error. Spring Boot Actuator provides
`/actuator/health`.

``` text
Developer pushes code
        |
        v
GitHub repository (main)
        |
        | Automatic trigger: pending configuration
        v
Jenkins: calculator-devops-pipeline
        |
        +--> Checkout
        +--> Maven Build
        +--> Test + publish JUnit XML
        +--> Build Docker image
        +--> Push versioned tag + latest to Docker Hub
        +--> Deploy with Docker Compose
        +--> Verify /actuator/health
                          |
                          v
             Docker Compose bridge network
                   /                 \
                  v                   v
          Nginx reverse proxy    Spring Boot API
          host port 8081         container port 8081
```

In the local development environment, Jenkins uses host port `8080`.
Nginx is therefore published on host port `8081`, mapping to Nginx
container port `8080`. Nginx forwards requests to
`http://calculator-api:8081` over the Compose network.

## Technology stack

-   Java 21 / Eclipse Temurin
-   Spring Boot and Spring Boot Actuator
-   Maven and Maven Wrapper
-   JUnit 5 and Spring MockMvc
-   Git and GitHub
-   Jenkins declarative pipeline
-   Docker and Docker Compose
-   Nginx reverse proxy
-   Docker Hub image registry

## Repository structure

The project is in `calculator-devops/` within the repository.

``` text
SPE_MINI_PROJECT/
├── README.md
└── calculator-devops/
    ├── Dockerfile
    ├── .dockerignore
    ├── .gitignore
    ├── Jenkinsfile
    ├── docker-compose.yml
    ├── nginx.conf
    ├── pom.xml
    ├── mvnw
    ├── .mvn/
    └── src/
        ├── main/java/com/calculator/devops/
        └── test/java/com/calculator/devops/
            ├── service/CalculatorServiceTest.java
            ├── controller/CalculatorControllerTest.java
            └── CalculatorDevopsApplicationTests.java
```

This is a summary of the main files, not an exhaustive listing of every
source file.

## API endpoints

The deployed local API is reachable through Nginx at
`http://localhost:8081`.

  Method   Endpoint                      Purpose
  -------- ----------------------------- ---------------------------
  GET      `/api/v1/add?a=10&b=5`        Add two numbers
  GET      `/api/v1/subtract?a=10&b=5`   Subtract `b` from `a`
  GET      `/api/v1/multiply?a=10&b=5`   Multiply two numbers
  GET      `/api/v1/divide?a=10&b=5`     Divide `a` by `b`
  GET      `/actuator/health`            Application health status

### Examples

``` bash
curl "http://localhost:8081/api/v1/add?a=10&b=5"
```

Example response:

``` json
{
  "operation": "add",
  "a": 10.0,
  "b": 5.0,
  "result": 15.0
}
```

``` bash
curl "http://localhost:8081/api/v1/subtract?a=10&b=5"
curl "http://localhost:8081/api/v1/multiply?a=10&b=5"
curl "http://localhost:8081/api/v1/divide?a=10&b=5"
curl -fsS "http://localhost:8081/actuator/health"
```

Division by zero:

``` bash
curl -i "http://localhost:8081/api/v1/divide?a=10&b=0"
```

Observed response: HTTP `400 Bad Request`, with JSON similar to:

``` json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/divide"
}
```

The health endpoint returned a status of `UP` during the verified
Jenkins deployment.

## Run locally

### Prerequisites

-   Git
-   Java 21 JDK
-   Included Maven Wrapper (`./mvnw`)

``` bash
git clone https://github.com/Ayush-Mishra-0018/SPE_MINI_PROJECT.git
cd SPE_MINI_PROJECT/calculator-devops
java -version
javac -version
./mvnw -version
```

Run tests and build:

``` bash
./mvnw -B test
./mvnw -B clean package
```

Run the JAR:

``` bash
java -jar target/calculator-devops-0.0.1-SNAPSHOT.jar
```

Then test from another terminal:

``` bash
curl "http://localhost:8081/api/v1/add?a=10&b=5"
curl -fsS "http://localhost:8081/actuator/health"
```

Stop the foreground application with `Ctrl+C`.

## Docker

### Dockerfile design

The Dockerfile uses a multi-stage build:

1.  **Build stage:** Maven with Java 21. Maven configuration and wrapper
    files are copied before application source to improve
    dependency-layer caching. Dependencies are downloaded and the
    executable JAR is packaged.
2.  **Runtime stage:** A Java 21 JRE image receives the built JAR,
    rather than the full Maven build environment.
3.  **Non-root execution:** The runtime process uses a dedicated
    `spring` user.
4.  **Port declaration:** The image declares `EXPOSE 8081`.
5.  **Build context hygiene:** `.dockerignore` excludes generated
    output, IDE files, Git metadata, logs, and environment files.

Build and run a standalone image from `calculator-devops/`:

``` bash
docker build -t calculator-devops:local .
docker run -d --name calculator-api -p 8081:8081 calculator-devops:local
curl "http://localhost:8081/api/v1/add?a=10&b=5"
curl -fsS "http://localhost:8081/actuator/health"
```

Inspect and stop it:

``` bash
docker ps
docker logs calculator-api
docker stop calculator-api
docker rm calculator-api
```

**Note:** The standalone container and Compose stack both use host port
`8081`. Stop one before starting the other.

## Docker Compose

The Compose file runs two services:

-   **`calculator-api`:** Spring Boot API on container port `8081`,
    accessible to other Compose services.
-   **`nginx`:** Reverse proxy on container port `8080`, mapped to host
    port `8081`.

Both services share a dedicated Docker bridge network. The configuration
includes API and Nginx health checks, `restart: unless-stopped`, and a
dependency condition so Nginx waits for the API to become healthy.

From `calculator-devops/`, run:

``` bash
docker compose up -d --build
docker compose ps
docker compose logs -f
```

Test through Nginx:

``` bash
curl "http://localhost:8081/api/v1/add?a=10&b=5"
curl "http://localhost:8081/api/v1/subtract?a=10&b=5"
curl "http://localhost:8081/api/v1/multiply?a=10&b=5"
curl "http://localhost:8081/api/v1/divide?a=10&b=5"
curl -fsS "http://localhost:8081/actuator/health"
```

Stop the stack:

``` bash
docker compose down
```

### Deployment verification already performed

The following commands were run from the repository root:

``` bash
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
docker compose -f calculator-devops/docker-compose.yml ps
```

Both services were reported `Up` and `healthy`:

-   `calculator-devops-calculator-api-1`, using
    `ayushmishra0018/calculator-devops:7`
-   `calculator-devops-nginx-1`, publishing host port `8081` to
    container port `8080`

## Tests

The test sources include:

-   `CalculatorServiceTest.java`: service-level tests for addition,
    subtraction, multiplication, division, and division by zero.
-   `CalculatorControllerTest.java`: MockMvc controller tests for API
    responses and HTTP error handling.
-   `CalculatorDevopsApplicationTests.java`: application-level MockMvc
    tests.

Run all tests:

``` bash
./mvnw -B test
```

Maven Surefire writes XML reports under `target/surefire-reports/`.
Jenkins publishes `calculator-devops/target/surefire-reports/*.xml`
using the JUnit publisher.

## Jenkins CI/CD pipeline

### Job configuration

-   **Job name:** `calculator-devops-pipeline`
-   **Local Jenkins URL:** `http://localhost:8080`
-   **SCM branch:** `main`
-   **Pipeline script path:** `calculator-devops/Jenkinsfile`
-   **Verified build:** `#7`

### Pipeline stages

1.  **Checkout** --- checks out the configured Git repository.
2.  **Build** --- runs `./mvnw -B -DskipTests clean package`. Tests are
    deliberately run in the next stage.
3.  **Test** --- runs `./mvnw -B test` and publishes Surefire XML
    reports to Jenkins.
4.  **Build Docker Image** --- builds the image and tags it with the
    Jenkins build number and `latest`.
5.  **Push to Docker Hub** --- logs in using a Jenkins-managed
    credential, then pushes the versioned tag and `latest`.
6.  **Deploy with Docker Compose** --- exports the image username and
    build tag, then runs `docker compose up -d --no-build`.
7.  **Verify Deployment** --- retries a request to
    `http://localhost:8081/actuator/health`; on failure, the pipeline
    prints container status and Compose logs.

The pipeline also timestamps log output, disables concurrent builds,
prints a success/failure message, and prunes unused Docker images at the
end.

Key commands:

``` bash
./mvnw -B -DskipTests clean package
./mvnw -B test
docker compose up -d --no-build
curl -fsS --retry 30 --retry-delay 2 --retry-connrefused \
  http://localhost:8081/actuator/health
```

### Credentials and image tags

The Jenkins credential ID used for Docker Hub is `dockerhub-creds`. The
pipeline binds the username and token to environment variables during
the push stage and uses `--password-stdin` for authentication. Do not
commit access tokens or print them in logs.

The image name is `ayushmishra0018/calculator-devops`. The pipeline
publishes a build-specific tag, such as `:7`, and the rolling `:latest`
tag.

### Verified Build #7

Build #7 finished with `SUCCESS`. The observed results included:

-   Build and test stages succeeded.
-   Docker image push succeeded.
-   The pushed image digest was
    `sha256:57f0891d0b0650815caa3611310157b4ee8ffda4f94febda98516ecf71d5fcea`.
-   Docker Compose started/recreated the API and Nginx services.
-   The health request through `http://localhost:8081/actuator/health`
    returned a response with `"status":"UP"`.
-   The pipeline printed `CI/CD pipeline completed successfully!`.

### Automatic trigger --- pending

The Jenkins Build Triggers page was inspected. At that point,
**neither** `GitHub hook trigger for GITScm polling` nor `Poll SCM` was
checked. An automatic trigger still needs to be configured and tested.

A GitHub webhook requires a Jenkins URL reachable from GitHub's servers.
`http://localhost:8080` is only a local address and cannot be used as a
public webhook destination. If a reachable endpoint is not available,
Poll SCM is an alternative accepted by the assignment.

### Email notifications --- pending

SMTP configuration and notification delivery for both successful and
failed builds still need to be configured and tested. This requirement
should only be marked complete after an actual notification has been
received.

## Git workflow

The repository uses feature/fix branches and pull requests merged into
`main`. The recent history checked locally confirmed these merged PRs:

-   PR #3 --- Jenkins pipeline feature
-   PR #4 --- Jenkins shell interpolation fix
-   PR #5 --- Jenkins loop syntax fix
-   PR #6 --- Jenkins pipeline cleanup and health-check fix

The local `main` branch was at merge commit `bd3d285`
(`Merge pull request #6 from Ayush-Mishra-0018/fix/jenkins-pipeline-cleanup`)
when checked.

Useful commands:

``` bash
git status
git fetch origin
git branch -a
git log origin/main --oneline -10
```

The application `.gitignore` is at `calculator-devops/.gitignore`; the
Docker-specific `.dockerignore` controls the Docker build context.

## Troubleshooting and security

### Jenkins Docker permissions

These commands were verified to work:

``` bash
sudo -u jenkins docker ps
sudo -u jenkins docker compose version
```

### Java version mismatch

Check both the shell user and Jenkins service user:

``` bash
java -version
javac -version
sudo -u jenkins java -version
sudo -u jenkins javac -version
```

Java 21 JDK was installed and `javac` was corrected to version 21 in the
current environment.

### Docker Hub push failure

-   Confirm the Jenkins credential ID is `dockerhub-creds`.
-   Confirm the token has the required read/write permissions.
-   Update the token only in Jenkins Credentials.
-   Never put tokens in source control or paste them into logs.

### Deployment health failure

``` bash
docker ps
docker compose -f calculator-devops/docker-compose.yml ps
docker compose -f calculator-devops/docker-compose.yml logs --tail=100
curl -i http://localhost:8081/actuator/health
```

### Security notes

-   Keep credentials and secrets out of Git.
-   Keep Docker Hub credentials in Jenkins' credential store.
-   Do not expose Jenkins publicly without proper authentication and
    network protections.
-   Use version-specific image tags for reproducibility; `latest` is
    convenient but mutable.

## Status and remaining work

### Completed and verified

-   [x] Calculator REST endpoints.
-   [x] Structured division-by-zero error response.
-   [x] Actuator health endpoint.
-   [x] JUnit 5 service tests and MockMvc controller/application tests.
-   [x] Maven build and test workflow.
-   [x] Multi-stage Java 21 Dockerfile.
-   [x] Non-root runtime user and `.dockerignore`.
-   [x] Docker Compose API + Nginx stack.
-   [x] Compose health checks, network, dependency condition, and
    restart policies.
-   [x] Public Docker Hub image and versioned image push.
-   [x] Jenkins build, tests/report publishing, image build/push,
    deployment, and health verification.
-   [x] Jenkins Build #7 succeeded.
-   [x] Both Compose services observed healthy.
-   [x] Feature/fix branches and merged pull requests confirmed.

### Remaining work

-   [ ] Configure and verify an automatic Jenkins trigger (GitHub
    webhook or Poll SCM).
-   [ ] Configure SMTP and test success/failure email notifications.
-   [ ] Push a small change and capture evidence of automatic pipeline
    execution.
-   [ ] Capture screenshots: successful Jenkins run, published test
    results, email notification, `docker ps`, and sample API responses.
-   [ ] Prepare the required 2--3 page PDF report with architecture
    diagram, pipeline stages, challenges/solutions, and screenshots.
-   [ ] Demonstrate a code push, pipeline run, deployment, and healthy
    API.

## Project links

-   **GitHub repository:**
    https://github.com/Ayush-Mishra-0018/SPE_MINI_PROJECT
-   **Docker Hub image:**
    https://hub.docker.com/r/ayushmishra0018/calculator-devops
-   **Jenkins (local):** http://localhost:8080
-   **Deployed API (local):** http://localhost:8081
-   **Health endpoint:** http://localhost:8081/actuator/health

------------------------------------------------------------------------

This README records the work completed and verified so far. Update the
checklist when the remaining trigger, email, report, and demonstration
tasks are complete.
