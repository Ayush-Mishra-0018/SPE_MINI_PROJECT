# Calculator DevOps Project

A Spring Boot REST API for basic arithmetic operations, containerized using Docker and deployed using Docker Compose with an Nginx reverse proxy. This project demonstrates application development, automated testing, version control, containerization, and deployment practices.

## Features

- REST API for addition, subtraction, multiplication, and division.
- Structured JSON responses for successful operations.
- HTTP 400 error responses for invalid requests and division by zero.
- Unit tests and integration tests using JUnit 5 and Spring testing tools.
- Spring Boot Actuator health endpoint.
- Multi-stage Docker build using Maven and Java 21.
- Non-root user for running the application container.
- Docker Compose deployment with Nginx as a reverse proxy.
- Health checks and restart policies for containers.
- GitHub feature branches and pull requests for changes.

## Technology Stack

- **Java 21**
- **Spring Boot**
- **Maven**
- **JUnit 5**
- **Spring MockMvc**
- **Git and GitHub**
- **Docker**
- **Docker Compose**
- **Nginx**

## API Endpoints

The application is exposed through Nginx on host port `8081` in the current development environment.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/add?a=10&b=20` | Adds two numbers |
| GET | `/api/v1/subtract?a=20&b=8` | Subtracts the second number from the first |
| GET | `/api/v1/multiply?a=6&b=7` | Multiplies two numbers |
| GET | `/api/v1/divide?a=10&b=2` | Divides the first number by the second |
| GET | `/actuator/health` | Checks application health |

### Example Requests

**Addition**

```bash
curl "http://localhost:8081/api/v1/add?a=10&b=20"
```

Example response:

```json
{
  "operation": "add",
  "a": 10.0,
  "b": 20.0,
  "result": 30.0
}
```

**Subtraction**

```bash
curl "http://localhost:8081/api/v1/subtract?a=20&b=8"
```

**Multiplication**

```bash
curl "http://localhost:8081/api/v1/multiply?a=6&b=7"
```

**Division by zero**

```bash
curl "http://localhost:8081/api/v1/divide?a=10&b=0"
```

Division by zero returns HTTP `400 Bad Request` with a structured JSON error response.

## Run Locally

### Prerequisites

- Java 21
- Git
- Maven, or use the included Maven Wrapper

Clone the repository:

```bash
git clone https://github.com/Ayush-Mishra-0018/SPE_MINI_PROJECT.git
cd SPE_MINI_PROJECT/calculator-devops
```

Run the tests:

```bash
./mvnw test
```

Build the application:

```bash
./mvnw clean package
```

Run the generated JAR:

```bash
java -jar target/calculator-devops-0.0.1-SNAPSHOT.jar
```

The application runs on port `8081` by default.

## Docker Image

The application image is published on Docker Hub.

**Docker Hub:** https://hub.docker.com/r/ayushmishra0018/calculator-devops

Pull the image:

```bash
docker pull ayushmishra0018/calculator-devops:latest
```

Run the API container directly:

```bash
docker run -d \
  --name calculator-api \
  -p 8081:8081 \
  ayushmishra0018/calculator-devops:latest
```

Check the health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

To stop and remove the standalone container:

```bash
docker stop calculator-api
docker rm calculator-api
```

## Docker Architecture

The Dockerfile uses a **multi-stage build**:

1. **Build stage:** Uses Maven with Java 21 to download dependencies, compile the application, run tests, and package the executable JAR.
2. **Runtime stage:** Uses a Java 21 JRE image and copies only the generated JAR into the runtime image.
3. **Security:** Runs the application as a dedicated non-root user.
4. **Image optimization:** Uses `.dockerignore` to exclude unnecessary files from the build context.

## Docker Compose Deployment

The project uses Docker Compose to run two services:

- **calculator-api:** The Spring Boot application, accessible to other containers on port `8081`.
- **nginx:** A reverse proxy that accepts requests on container port `8080` and forwards them to the calculator API.

The host exposes Nginx on port `8081`. This avoids a conflict with Jenkins, which uses host port `8080` in the current development environment.

### Start the application

From the `calculator-devops` directory:

```bash
docker compose up -d --build
```

Check service status:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

Test the deployed application:

```bash
curl "http://localhost:8081/api/v1/add?a=10&b=20"
curl http://localhost:8081/actuator/health
```

Stop the services:

```bash
docker compose down
```

### Deployment Features

- Dedicated Docker bridge network for communication between services.
- API health check using Spring Boot Actuator.
- Nginx health check.
- Nginx starts after the API reports healthy.
- Automatic container restart policies.
- Environment-based configuration for the API port and Docker image tag.

## Testing

The project includes:

- **Service unit tests:** Verify arithmetic operations and division-by-zero handling.
- **Controller tests:** Verify API responses and HTTP error handling using MockMvc.
- **Application integration tests:** Verify application-level behavior.

Run all tests:

```bash
./mvnw test
```

Maven generates test reports under:

```text
target/surefire-reports/
```

## Git and GitHub Workflow

The project uses Git for version control and GitHub for remote source management.

Development follows a feature-branch workflow:

1. Create a branch for a feature or improvement.
2. Implement and test the changes.
3. Commit changes with descriptive messages.
4. Push the branch to GitHub.
5. Open a pull request and merge it into `main`.

Feature branches and pull requests have been used for Docker containerization and Docker Compose/Nginx deployment.

## Project Status

### Completed so far

- Spring Boot calculator REST API.
- Unit and integration tests.
- Structured error handling and Actuator health endpoint.
- Multi-stage Dockerfile and `.dockerignore`.
- Docker Compose configuration for the API and Nginx.
- Successful local container deployment with both services healthy.
- Successful API and health-check requests through Nginx.
- Docker image pushed to Docker Hub.

### Planned / remaining work

- Configure the Jenkins declarative pipeline.
- Automate testing, image building, Docker Hub publishing, and deployment through Jenkins.
- Configure Jenkins credentials and build notifications.
- Capture final demo screenshots and prepare the project report.

## Repository

GitHub: https://github.com/Ayush-Mishra-0018/SPE_MINI_PROJECT

Docker Hub: https://hub.docker.com/r/ayushmishra0018/calculator-devops
