# End-to-End CI/CD Pipeline for a Calculator REST API

**Student Name:** Ayush Mishra  
**Roll Number:** IMT2023129  
**Course:** Software Production Engineering (SPE)  
**Institute:** International Institute of Information Technology Bangalore (IIIT-B)

**GitHub Repository:** [SPE_MINI_PROJECT](https://github.com/Ayush-Mishra-0018/SPE_MINI_PROJECT)  
**Docker Hub Repository:** [ayushmishra0018/calculator-devops](https://hub.docker.com/r/ayushmishra0018/calculator-devops)

---

## 1. Project Overview

This project implements an end-to-end Continuous Integration and Continuous Deployment (CI/CD) pipeline for a Calculator REST API developed using Spring Boot.

The objective is to automate the complete software delivery workflow, beginning with a code change pushed to GitHub and ending with the updated application deployed in Docker containers.

Jenkins orchestrates the pipeline. It checks out the source code, builds the application, runs automated tests, publishes test reports, builds a Docker image, pushes the image to Docker Hub, deploys the application using Docker Compose, verifies the deployment, and sends email notifications.

The deployment consists of two services:

- **Calculator API:** A Spring Boot application that implements arithmetic operations.
- **Nginx:** A reverse proxy that exposes the application through port `8081` on the host machine.

The project demonstrates source control, automated testing, continuous integration, containerization, image publishing, automated deployment, health checks, and build notifications in one workflow.

## 2. Technology Stack

| Component | Technology |
|---|---|
| Programming language | Java |
| Application framework | Spring Boot |
| Build tool | Maven |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Source control | Git |
| Source code hosting | GitHub |
| CI/CD orchestration | Jenkins |
| Containerization | Docker |
| Multi-container deployment | Docker Compose |
| Reverse proxy | Nginx |
| Container registry | Docker Hub |
| Health monitoring | Spring Boot Actuator |
| Build notifications | Jenkins Email Extension Plugin |

## 3. Repository Structure

The project is organized into application source code, automated tests, container configuration, CI/CD pipeline configuration, and documentation.

```text
SPE_MINI_PROJECT/
└── calculator-devops/
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/calculator/devops/
    │   │   │       ├── CalculatorDevopsApplication.java
    │   │   │       ├── controller/
    │   │   │       │   └── CalculatorController.java
    │   │   │       └── service/
    │   │   │           └── CalculatorService.java
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    │       └── java/com/calculator/devops/
    │           ├── CalculatorDevopsApplicationTests.java
    │           ├── controller/
    │           │   └── CalculatorControllerTest.java
    │           └── service/
    │               └── CalculatorServiceTest.java
    ├── Dockerfile
    ├── .dockerignore
    ├── docker-compose.yml
    ├── Jenkinsfile
    ├── pom.xml
    └── README.md
```

The tree represents the main project components. The exact file structure can be checked against the current GitHub repository.

### 3.1 Main Application Files

#### `CalculatorDevopsApplication.java`

This is the Spring Boot entry point. It starts the Spring application and initializes the application context.

#### `CalculatorController.java`

The controller exposes the calculator's REST endpoints. It accepts HTTP requests, reads the input parameters, invokes the service layer, and returns appropriate HTTP responses.

#### `CalculatorService.java`

The service layer implements the arithmetic operations:

- Addition
- Subtraction
- Multiplication
- Division

It also handles division-by-zero validation so that invalid arithmetic operations produce an appropriate error.

#### `application.properties`

This file contains application configuration, including server and management endpoint settings.

#### `CalculatorServiceTest.java`

This test class verifies the arithmetic logic independently of the HTTP layer.

#### `CalculatorControllerTest.java`

This test class verifies the controller's HTTP behaviour, including successful requests, missing parameters, invalid numeric input, and division-by-zero handling.

#### `CalculatorDevopsApplicationTests.java`

This class includes application-context testing and integration testing to verify that the application starts and that application components work together.

#### `pom.xml`

The Maven project file defines dependencies, build configuration, Java version settings, and test execution configuration.

#### `Dockerfile`

This file defines how the application is packaged into a Docker image using a multi-stage build.

#### `.dockerignore`

This file excludes unnecessary files from the Docker build context, reducing the amount of data sent to the Docker daemon.

#### `docker-compose.yml`

This file defines the Calculator API and Nginx services, their networking, port mappings, health checks, and restart policies.

#### `Jenkinsfile`

This file contains the Jenkins Declarative Pipeline. It defines the stages that automate testing, image creation, publishing, deployment, verification, and notifications.

#### `README.md`

This file documents the application, setup instructions, API endpoints, testing, and deployment.

## 4. Calculator REST API

The application provides four arithmetic endpoints and one health endpoint.

| HTTP Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/v1/add?a=10&b=5` | Addition |
| GET | `/api/v1/subtract?a=10&b=5` | Subtraction |
| GET | `/api/v1/multiply?a=10&b=5` | Multiplication |
| GET | `/api/v1/divide?a=10&b=5` | Division |
| GET | `/actuator/health` | Application health |

### 4.1 Example: Addition

Request:

```http
GET /api/v1/add?a=10&b=5
```

Successful response:

```json
{
  "operation": "add",
  "a": 10.0,
  "b": 5.0,
  "result": 15.0
}
```

The endpoint returns HTTP `200 OK` when the operation succeeds.

### 4.2 Division by Zero

Division by zero is an invalid operation. The API returns HTTP `400 Bad Request` with a structured JSON response.

Example response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/divide"
}
```

This makes the API's error behaviour predictable and allows clients and automated tests to distinguish invalid requests from successful operations.

### 4.3 Health Endpoint

Spring Boot Actuator provides the health endpoint:

```http
GET /actuator/health
```

Example response:

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

The pipeline uses this endpoint to verify that the deployed application is responding after deployment.

## 5. Automated Testing

The project uses Maven and JUnit 5 to automate testing. Spring Boot testing support and MockMvc are used to test application and HTTP behaviour.

The verified Jenkins test report contains 11 passing tests.

| Test Class | Tests | Purpose |
|---|---:|---|
| `CalculatorDevopsApplicationTests` | 2 | Application context and integration behaviour |
| `CalculatorControllerTest` | 4 | HTTP endpoint behaviour and error handling |
| `CalculatorServiceTest` | 5 | Arithmetic operations and division by zero |
| **Total** | **11** | **All tests passed** |

The test suite covers:

- Addition
- Subtraction
- Multiplication
- Division
- Division by zero
- Successful controller requests
- Missing request parameters
- Invalid numeric input
- Application startup and integration behaviour

Run the tests locally using:

```bash
mvn clean test
```

Maven executes the tests and generates JUnit XML reports. Jenkins collects these reports and displays the test results through its build interface.

The verified pipeline run reported:

- Total tests: 11
- Passed: 11
- Failed: 0
- Skipped: 0

Publishing test reports makes the results visible in Jenkins rather than requiring someone to inspect only the console logs.

## 6. Docker Image Creation

Docker packages the application and its runtime environment into an image. This allows the application to run consistently across different environments.

### 6.1 Multi-Stage Docker Build

The Dockerfile uses two stages.

**Stage 1: Build stage**

- Uses a Maven image with a JDK.
- Copies Maven dependency metadata before copying application source code.
- Builds the executable Spring Boot JAR.

Copying dependency metadata separately can improve build caching because dependencies do not need to be downloaded again whenever only application source files change.

**Stage 2: Runtime stage**

- Uses a slim Java runtime image.
- Copies the packaged JAR from the build stage.
- Runs the application using a non-root user.
- Exposes the application's internal port.

Using a separate runtime image avoids carrying the full Maven build environment into the deployed container. Running as a non-root user also reduces unnecessary privileges.

### 6.2 Docker Build Context

The `.dockerignore` file excludes unnecessary files such as build output, Git metadata, IDE configuration, and local environment files.

This keeps the Docker build context smaller and avoids copying irrelevant local files into the image build process.

### 6.3 Docker Hub

The image is published to:

[Docker Hub: ayushmishra0018/calculator-devops](https://hub.docker.com/r/ayushmishra0018/calculator-devops)

The pipeline publishes two tags:

- A build-specific tag, such as `13`
- The `latest` tag

Example commands:

```bash
docker pull ayushmishra0018/calculator-devops:latest
docker pull ayushmishra0018/calculator-devops:13
```

A build-specific tag identifies the image associated with a particular Jenkins build. The `latest` tag provides a convenient reference to the most recently published image.

## 7. Docker Compose Deployment

Docker Compose coordinates the multiple containers required by the deployment.

The project uses two services: the Calculator API and Nginx.

### 7.1 Calculator API Service

The API service:

- Runs the Spring Boot application.
- Uses the image selected by the deployment pipeline.
- Includes a health check using `/actuator/health`.
- Uses a restart policy to improve recovery after unexpected stops.

The API listens on its configured internal application port.

### 7.2 Nginx Service

The Nginx service:

- Uses the `nginx:alpine` image.
- Publishes host port `8081` and maps it to container port `8080`.
- Forwards incoming requests to the Calculator API through the Docker network.
- Has its own health check and restart policy.

The user accesses the application through Nginx at:

```text
http://localhost:8081
```

For example:

```text
http://localhost:8081/api/v1/add?a=10&b=5
```

### 7.3 Networking and Health Checks

The services communicate over a Docker Compose network. Health checks allow Docker to report whether a service is healthy.

The Compose configuration also coordinates service startup so that Nginx waits for the API to become healthy before it is considered ready to start, as configured.

Start the deployment with:

```bash
docker compose up -d
```

Inspect the running services with:

```bash
docker compose ps
```

The verified deployment showed both the Calculator API and Nginx containers as healthy.

## 8. Jenkins CI/CD Pipeline

Jenkins automates the software delivery workflow. The configured job is named:

```text
calculator-devops-pipeline
```

### 8.1 Automatic Triggering

The Jenkins job uses SCM polling to detect changes in the GitHub repository.

When Jenkins detects a source-control change, it automatically triggers a pipeline build. This avoids requiring a developer to manually start Jenkins after every push.

SCM polling is the configured trigger mechanism for this project.

### 8.2 Pipeline Stages

The Jenkins Declarative Pipeline contains the following stages.

#### Stage 1: Checkout

Jenkins retrieves the latest source code from the configured GitHub repository.

This ensures that the build operates on the committed version of the project.

#### Stage 2: Build

Jenkins executes the Maven package build.

```bash
mvn clean package -DskipTests
```

The build stage checks whether the application can be compiled and packaged. Tests are executed separately in the next stage.

#### Stage 3: Test

Jenkins runs:

```bash
mvn test
```

This executes the automated test suite. If a test fails, Maven returns a failure status and the pipeline is marked as failed.

#### Stage 4: Publish Test Results

Jenkins collects the JUnit XML reports generated by Maven.

The reports provide a structured view of passed, failed, and skipped tests in the Jenkins interface.

This also makes it easier to investigate failures without relying only on console output.

#### Stage 5: Build Docker Image

Jenkins builds the Docker image and assigns both a build-specific tag and the `latest` tag.

For example:

```bash
docker build \
  -t ayushmishra0018/calculator-devops:13 \
  -t ayushmishra0018/calculator-devops:latest .
```

The build-specific tag links the image to the Jenkins build that produced it.

#### Stage 6: Push to Docker Hub

Jenkins authenticates to Docker Hub using credentials stored in Jenkins Credentials.

The pipeline pushes the versioned image and the `latest` image to the registry.

Credentials are not hard-coded into the Git repository. Jenkins manages them separately and makes them available to the pipeline when needed.

#### Stage 7: Deploy with Docker Compose

The pipeline supplies the image tag for the build and deploys the services using Docker Compose.

The deployment uses:

```bash
docker compose up -d --no-build
```

This allows Compose to use the selected image rather than rebuilding the application image during deployment.

#### Stage 8: Verify Deployment

After deployment, Jenkins calls the application's Actuator health endpoint.

A successful health response indicates that the deployed application is responding as expected.

This stage checks the running deployment, not merely whether the Docker image was built successfully.

#### Stage 9: Post-Build Actions

The pipeline defines post-build actions for success and failure.

These actions provide build status information and send email notifications using the Jenkins Email Extension Plugin. The pipeline also performs Docker image cleanup to remove unused images where possible.

### 8.3 Pipeline Summary

```text
Developer pushes code to GitHub
              |
              v
      Jenkins SCM Polling
              |
              v
           Checkout
              |
              v
             Build
              |
              v
             Test
              |
              v
      Publish JUnit Reports
              |
              v
       Build Docker Image
              |
              v
       Push to Docker Hub
              |
              v
      Deploy using Compose
              |
              v
      Verify Health Endpoint
              |
              v
      Post-Build Notification
```

This workflow connects source-code changes to automated testing and deployment.

## 9. Email Notifications

The project uses the Jenkins Email Extension Plugin to send build notifications.

The SMTP settings are configured in Jenkins under **Extended E-mail Notification**.

The configuration uses Gmail SMTP:

| Setting | Value |
|---|---|
| SMTP server | `smtp.gmail.com` |
| SMTP port | `587` |
| TLS | Enabled |
| SSL | Disabled |
| Notification recipient | Configured recipient email address |

The pipeline's post-build actions use the build result to send a formatted notification.

The success notification includes information such as:

- Pipeline job name
- Build number
- Build status
- Docker image tag
- Link to the Jenkins console output

The successful build notification was received in Gmail.

Email notifications are useful because a developer can learn whether a pipeline succeeded or failed without continuously monitoring the Jenkins interface.

## 10. Verification and Results

The project has been verified through Jenkins test reports, Docker Hub image publication, Docker Compose status, API requests, and the Actuator health endpoint.

### 10.1 Jenkins Build

The Jenkins pipeline completed successfully.

The verified build showed:

- Successful source checkout
- Successful Maven build
- All 11 tests passing
- JUnit reports published
- Docker image built successfully
- Image pushed to Docker Hub
- Docker Compose deployment completed
- Application health check successful
- Success notification received

### 10.2 Docker Compose Status

The command:

```bash
docker compose ps
```

showed the Calculator API and Nginx services running with healthy status.

This confirms that both containers were running and passing their configured health checks at the time of verification.

### 10.3 Addition Endpoint

Request:

```bash
curl -sS -i "http://localhost:8081/api/v1/add?a=10&b=5"
```

Observed result:

```http
HTTP/1.1 200
Content-Type: application/json
```

Response body:

```json
{
  "operation": "add",
  "a": 10.0,
  "b": 5.0,
  "result": 15.0
}
```

This verifies that the request reaches the application through Nginx and returns the expected arithmetic result.

### 10.4 Division-by-Zero Endpoint

Request:

```bash
curl -sS -i "http://localhost:8081/api/v1/divide?a=10&b=0"
```

Observed result:

```http
HTTP/1.1 400
Content-Type: application/json
```

Response body:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/divide"
}
```

This verifies that the API returns the expected HTTP error response for division by zero.

### 10.5 Health Endpoint

Request:

```bash
curl -sS -i "http://localhost:8081/actuator/health"
```

Observed response body:

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

This verifies that the deployed application's health endpoint responds successfully through Nginx.

## 11. Git and Version Control

Git and GitHub are used to maintain the source code and track changes.

The repository includes meaningful commits documenting project development. Feature branches and merges were used during development, providing a history of how the implementation evolved.

Version control provides several benefits:

- Tracks changes to source code and configuration.
- Makes it possible to inspect earlier versions.
- Provides a record of project development.
- Connects source-code changes to automated Jenkins builds.
- Helps prevent accidental inclusion of generated files and local configuration.

The `.gitignore` file excludes files that should not be committed, such as generated build output and local IDE files.

The `.dockerignore` file serves a different purpose: it excludes unnecessary files from the Docker build context.

Sensitive credentials are managed through Jenkins Credentials rather than hard-coded into the pipeline.

## 12. Challenges and Design Decisions

### 12.1 Separating Build and Test Stages

The pipeline separates compilation and packaging from automated testing.

This makes the workflow easier to understand and allows the test stage to provide a clear result before the image is published and deployed.

### 12.2 Publishing JUnit Reports

Executing tests in Jenkins is not enough by itself. Publishing JUnit XML reports makes test outcomes visible in the Jenkins interface and provides a convenient way to inspect failures.

### 12.3 Using Versioned Docker Images

A build-specific Docker tag identifies the image created by a particular pipeline run.

This is more traceable than relying exclusively on the `latest` tag because the build-specific tag records which image corresponds to a particular build.

### 12.4 Deploying Multiple Services

Docker Compose manages both the Calculator API and Nginx. It defines their networking, health checks, restart behaviour, and startup dependency.

This allows the deployment to be started and inspected through a small set of Compose commands rather than managing each container independently.

### 12.5 Verifying the Running Application

A successful image build does not guarantee that the application is available after deployment.

The pipeline therefore checks the Actuator health endpoint. The API was also tested manually through Nginx to verify successful arithmetic requests and error handling.

### 12.6 Configuring Email Notifications

Jenkins requires correct SMTP configuration to send notifications.

The Extended E-mail Notification configuration was set to use Gmail SMTP on port `587`, with TLS enabled and SSL disabled. The successful pipeline notification was received in Gmail.

## 13. Conclusion

This project demonstrates an end-to-end CI/CD workflow for a Spring Boot Calculator REST API.

It integrates GitHub version control, Jenkins automation, Maven builds, JUnit testing, test-report publication, Docker image creation, Docker Hub publishing, Docker Compose deployment, Actuator health checks, and email notifications.

The verified pipeline passed all 11 automated tests, published the Docker image, deployed the Calculator API and Nginx containers, and returned successful API and health-check responses.

The project illustrates how a code change can move through an automated delivery process with repeatable builds, automated tests, containerized deployment, & visible pipeline results.

---

**Student:** Ayush Mishra  
**Roll Number:** IMT2023129  
**Course:** Software Production Engineering  
**Institute:** IIIT Bangalore