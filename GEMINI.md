# GEMINI.md

This file provides a high-level overview of the Liftrr backend project, its structure, and how to build, run, and interact with it.

## Project Overview

This is a Kotlin-based Spring Boot application that serves as the backend for the Liftrr app. It provides RESTful APIs for user authentication (including email/password and Google Sign-In) and other core functionalities.

### Key Technologies

*   **Framework:** Spring Boot 3
*   **Language:** Kotlin
*   **Database:** H2 (in-memory, for development)
*   **Authentication:** JWT (JSON Web Tokens)
*   **API Documentation:** Swagger/OpenAPI 3
*   **Build Tool:** Gradle

### Architecture

The application follows a standard Spring Boot structure:

*   `src/main/kotlin/org/liftrr`: Main source code.
    *   `LiftrrBackendApplication.kt`: The main entry point for the Spring Boot application.
    *   `WebSecurityConfig.kt`: Configures security, including JWT filter and public/private routes.
    *   `OpenApiConfig.kt`: Configures Swagger for API documentation.
    *   `auth/`: Contains all logic related to authentication, such as the `AuthController`, `JwtService`, and `RefreshTokenService`.
    *   `user/`: Contains the `User` entity, `UserRepository`, and `UserDetailsServiceImpl`.
*   `src/main/resources`: Application configuration and static assets.
    *   `application.properties`: Main Spring Boot configuration file.
*   `build.gradle.kts`: The Gradle build file, defining dependencies and build settings.

## Building and Running

### Prerequisites

*   Java 21 or higher
*   Gradle (the provided `gradlew` wrapper is recommended)

### Running the Application

1.  **Clone the repository.**
2.  **Run the application using the Gradle wrapper:**

    ```bash
    ./gradlew bootRun
    ```

The application will start on the port configured in `application.properties` (default is 8080).

### Testing the Application

Run the test suite using the following command:

```bash
./gradlew test
```

## API Documentation

Once the application is running, you can access the Swagger UI for interactive API documentation at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

This interface allows you to view all available endpoints, see their request/response formats, and execute API calls directly from your browser.

## Development Conventions

*   **Authentication:** The API uses Bearer tokens for authenticated endpoints. When a user logs in or registers, they receive a short-lived `accessToken` and a long-lived `refreshToken`. The `accessToken` should be sent in the `Authorization` header for all protected API calls. The `refreshToken` can be used to obtain a new `accessToken` when the old one expires.
*   **Error Handling:** The API uses standard HTTP status codes to indicate the outcome of a request.
*   **Code Style:** Follows standard Kotlin coding conventions.
