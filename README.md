# Liftrr Backend

REST API for the Liftrr fitness app — auth, user profiles, workout sessions with video upload, and body-weight tracking.

Built with **Spring Boot 3.5**, **Kotlin 1.9**, **Java 21**, JPA/Hibernate, JWT auth (with refresh-token rotation), Google ID token sign-in, and Cloudflare R2 storage via presigned URLs.

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Authentication](#authentication)
- [File uploads](#file-uploads)
- [API reference](#api-reference)
- [Exception handling](#exception-handling)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Common commands](#common-commands)
- [Project structure](#project-structure)
- [Development guardrails](#development-guardrails)
- [License](#license)

---

## Features

- **Email/password and Google OAuth sign-in** with a unified token-issuance flow.
- **JWT access tokens** (15 min) backed by **refresh-token rotation** (7 days) with revoke-on-use and replay detection.
- **User profiles** — create, fetch, partial update, soft delete.
- **Profile photo upload** via 15-minute presigned PUT URLs to Cloudflare R2 (backend never streams binaries).
- **Workout sessions** with soft-delete, video upload via presigned URLs to a separate R2 bucket.
- **Body-weight tracking** — list and bulk upsert for offline-friendly sync.
- **Typed domain exceptions** mapped centrally to HTTP responses by a single `@RestControllerAdvice`.
- **OpenAPI / Swagger UI** at `/swagger-ui/index.html`.
- **H2 in-memory DB** by default — zero external dependencies to boot locally.

---

## Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin 1.9 (JVM target 21) |
| Framework | Spring Boot 3.5 (Web, Security, Data JPA) |
| DB (dev) | H2 in-memory |
| ORM | Hibernate / Spring Data JPA |
| Auth | JWT (`jjwt 0.12.6`) + Google API Client for ID token verification |
| Object storage | Cloudflare R2 via AWS SDK v2 (`software.amazon.awssdk:s3`) |
| Docs | Springdoc OpenAPI 2.8 |
| Build | Gradle (Kotlin DSL) |

---

## Architecture

The codebase is organized by **feature package**, not by layer. Each feature owns its entity, repository, service, controller, and DTOs.

| Package | Responsibility |
|---|---|
| `auth/` | Registration, email/password login, Google OAuth, token issuance |
| `auth/jwt/` | `JwtService` (sign/parse) and `JwtAuthFilter` (request authentication) |
| `auth/refresh/` | `RefreshToken` entity, rotation, revoke-on-use, replay detection |
| `auth/google/` | `OAuthTokenVerifier` interface + `GoogleTokenService` implementation |
| `user/` | `User` entity, `UserService`, `UserDetailsServiceImpl` |
| `userprofile/` | `UserProfile` entity (one-to-one with `User`), CRUD endpoints |
| `userprofile/photo/` | Profile photo presigned upload + confirm |
| `workout/` | `WorkoutSession` entity + metadata CRUD (soft delete) |
| `workout/video/` | Workout video presigned upload + confirm |
| `weight/` | Body-weight entries + bulk upsert |
| `storage/` | `StorageService` interface + R2 implementation, `MediaUploadService` helper |
| `common/` | Sealed `LiftrrException` hierarchy + `GlobalExceptionHandler` |
| `config/` | `SecurityConfig`, `R2Config`, `OpenApiConfig`, `GoogleAuthVerificationConfig` |

### Layering rules

- **Controllers** are thin — they map HTTP to service calls and back to DTOs.
- **Services** hold business logic and throw typed `LiftrrException` subclasses.
- **Repositories** handle persistence only.
- **Storage** access goes through `StorageService` — controllers and services never touch the AWS S3 SDK directly.
- **Errors** are returned by `GlobalExceptionHandler`, not by services.

---

## Authentication

### Token issuance

1. Client calls `POST /api/v1/auth/register`, `/login`, or `/google` with credentials or a Google ID token.
2. `AuthService` verifies, looks up or creates the `User`, then calls `issueTokenPair()`.
3. Response contains:
   - **Access token** — short-lived JWT (default 15 min, `jwt.expiration-ms`).
   - **Refresh token** — long-lived opaque token persisted in DB (default 7 days, `jwt.refresh-expiration-ms`).

### Refresh-token rotation

- Each call to `POST /api/v1/auth/refresh` **revokes** the supplied refresh token and **issues a new one**.
- If a **revoked** refresh token is ever presented again, `RefreshTokenService` interprets it as a replay attack and **revokes every refresh token for that user** (`revokeAllForUser`).

### Request authentication

- `JwtAuthFilter` runs ahead of the Spring Security filter chain.
- It extracts the bearer token, validates the signature and expiry, loads `UserDetails` via `UserDetailsServiceImpl`, and populates `SecurityContext`.

### Public routes

- `/api/v1/auth/**`
- `/swagger-ui/**`, `/v3/api-docs/**`

Everything else requires a valid bearer JWT.

---

## File uploads

Large uploads (profile photos, workout videos) **never pass through the backend**. Flow:

1. Client calls `POST /api/v1/profile/photo` or `POST /api/v1/workout/video` to request an upload target.
2. Backend returns an `UploadTarget`:
   ```json
   {
     "uploadUrl": "https://<r2-endpoint>/...",
     "objectKey": "profiles/<userId>/<uuid>",
     "expiresInSeconds": 900
   }
   ```
3. Client `PUT`s the binary directly to the presigned URL on Cloudflare R2.
4. Client calls the matching `PATCH` endpoint to **confirm** the upload — backend then persists the object key.

Object key conventions:

- Profile photos: `profiles/{userId}/{uuid}`
- Workout videos: stored in a separate bucket (`r2.video.bucket`)

Object keys are validated strictly server-side to prevent users from claiming objects they didn't upload.

---

## API reference

All paths are prefixed with `/api/v1`. Unless noted, requests require `Authorization: Bearer <jwt>`.

### Auth — `/auth`

| Method | Path | Auth | Purpose |
|---|---|---|---|
| `POST` | `/register` | public | Email/password sign-up |
| `POST` | `/login` | public | Email/password sign-in |
| `POST` | `/google` | public | Exchange a Google ID token for a Liftrr token pair |
| `POST` | `/refresh` | public | Rotate refresh token, issue new access token |
| `POST` | `/logout` | public | Revoke the supplied refresh token |
| `GET` | `/me` | required | Current authenticated user summary |

### User profile — `/profile`

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/profile` | Create profile (one per user) |
| `GET` | `/profile` | Fetch current user's profile |
| `PATCH` | `/profile` | Partial update |
| `DELETE` | `/profile` | Soft-delete |

### Profile photo — `/profile/photo`

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/profile/photo` | Request presigned upload URL |
| `PATCH` | `/profile/photo` | Confirm upload, persist object key |

### Workout session — `/workout/session`

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/workout/session` | Create workout session |
| `GET` | `/workout/session` | List user's (non-deleted) sessions |
| `DELETE` | `/workout/session/{sessionId}` | Soft-delete |

### Workout video — `/workout/video`

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/workout/video` | Request presigned upload URL |
| `PATCH` | `/workout/video` | Confirm upload, persist object key |

### Weight tracking — `/weights`

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/weights` | List entries for current user |
| `POST` | `/weights/bulk` | Bulk upsert (offline-sync friendly) |

Full request/response shapes are documented at **`/swagger-ui/index.html`** once the app is running.

---

## Exception handling

Every domain error extends the sealed class `LiftrrException` in `common/exceptions.kt`. `GlobalExceptionHandler` maps subclasses to HTTP status codes:

| Status | Exception |
|---|---|
| **401** | `LiftrrUnauthorizedException` and subclasses (`InvalidRefreshTokenException`, `ExpiredRefreshTokenException`, `ReplayedRefreshTokenException`, `InvalidGoogleTokenException`) |
| **403** | `InvalidObjectKeyException` |
| **404** | `UserNotFoundException`, `ProfileNotFoundException`, `WorkoutSessionNotFoundException` |
| **409** | `ProfileAlreadyExistsException`, `EmailAlreadyInUseException` |
| **422** | `PhotoNotFoundException`, `MediaNotFoundException` |
| **500** | `UserNotPersistedException` |

**Rules:**

- Services throw typed exceptions; they never return error responses.
- Controllers do not catch domain exceptions — `GlobalExceptionHandler` handles them centrally.
- Add a new exception by extending `LiftrrException` and adding a handler method.

---

## Getting started

### Prerequisites

- **JDK 21** (the build uses a toolchain spec, so Gradle can fetch one if needed)
- Cloudflare R2 credentials (or any S3-compatible store)
- A Google OAuth 2.0 client ID (only required for the `/auth/google` route)

### Setup

```bash
# 1. Clone
git clone https://github.com/faizanmir/liftrr-backend.git
cd liftrr-backend

# 2. Copy the config template and fill in secrets
cp src/main/resources/application.properties.template \
   src/main/resources/application.properties

# 3. Generate a JWT secret (anything ≥ 64 random bytes, base64-encoded)
openssl rand -base64 64

# 4. Run
./gradlew bootRun
```

The app starts on **`http://localhost:9090`**. Open `http://localhost:9090/swagger-ui/index.html` to explore the API.

`application.properties` is gitignored — never commit real secrets.

---

## Configuration

Fill these in `src/main/resources/application.properties`:

| Key | Required | Purpose |
|---|---|---|
| `jwt.secret` | yes | HMAC secret for signing JWTs (64+ random bytes, base64) |
| `jwt.expiration-ms` | yes | Access-token lifetime (default 900000 = 15 min) |
| `jwt.refresh-expiration-ms` | yes | Refresh-token lifetime (default 604800000 = 7 days) |
| `google.client-id` | for Google sign-in | Web-client OAuth 2.0 client ID |
| `r2.account-id` | yes | Cloudflare account ID |
| `r2.access-key` | yes | R2 access key ID |
| `r2.secret-key` | yes | R2 secret access key |
| `r2.bucket` | yes | Bucket for profile photos |
| `r2.public-url` | yes | Public/CDN base URL for the photo bucket |
| `r2.video.bucket` | yes | Bucket for workout videos |
| `r2.video.public-url` | yes | Public/CDN base URL for the video bucket |

The dev profile uses H2 in-memory storage with `spring.jpa.hibernate.ddl-auto=update`, so no schema migrations are required to boot locally. Swap in PostgreSQL/MySQL by replacing the `spring.datasource.*` and `spring.jpa.database-platform` properties.

---

## Common commands

```bash
# Build (compile + tests + jar)
./gradlew build

# Run locally on port 9090
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.liftrr.LiftrrBackendApplicationTests"
```

Swagger UI: <http://localhost:9090/swagger-ui/index.html>

---

## Project structure

```text
src/main/kotlin/org/liftrr/
├── LiftrrBackendApplication.kt
├── auth/
│   ├── AuthController.kt
│   ├── AuthDto.kt
│   ├── AuthService.kt
│   ├── google/           # Google ID token verification
│   ├── jwt/              # JwtService, JwtAuthFilter
│   └── refresh/          # RefreshToken entity + rotation logic
├── user/                 # User entity, UserDetailsServiceImpl
├── userprofile/
│   ├── UserProfile.kt
│   ├── UserProfileController.kt
│   ├── UserProfileService.kt
│   ├── UserProfileRepository.kt
│   ├── dto/
│   ├── photo/            # Profile photo upload
│   └── storage/
├── workout/
│   ├── WorkoutSession.kt
│   ├── WorkoutMetaDataController.kt
│   ├── WorkoutSessionService.kt
│   ├── WorkoutSessionRepository.kt
│   ├── dto/
│   └── video/            # Workout video upload
├── weight/               # Body-weight entries
│   ├── WeightEntry.kt
│   ├── WeightEntryController.kt
│   ├── WeightEntryService.kt
│   ├── WeightEntryRepository.kt
│   └── dto/
├── storage/              # StorageService + R2 implementation
├── common/               # LiftrrException hierarchy + GlobalExceptionHandler
└── config/               # SecurityConfig, R2Config, OpenApiConfig, ...
```

---

## Development guardrails

Treat these as hard rules when contributing:

### Auth

- Do not weaken JWT validation.
- Do not bypass `JwtAuthFilter` for protected APIs.
- Do not add public routes unless explicitly required.
- Preserve refresh-token rotation and replay detection.

### Persistence

- Be careful with JPA entity relationships and ownership.
- Do not blindly add `CascadeType.ALL`.
- Prefer explicit saves and clear aggregate boundaries.
- Watch out for detached entities and lazy-loading at serialization time.

### Storage

- Use the `StorageService` abstraction instead of calling R2 directly.
- Keep object-key validation strict — never trust client-supplied keys without server-side checks.
- Prefer presigned URLs for any upload over ~1 MB.
- Never expose R2 credentials in responses or logs.

### Errors

- Throw typed `LiftrrException` subclasses from services.
- Do not build error responses inside services or controllers.
- Route everything through `GlobalExceptionHandler`.

### Secrets

- Never commit `application.properties`, JWT secrets, R2 credentials, Google client secrets, or local machine paths.
- `application.properties` is gitignored — keep it that way.

---

## License

Not yet licensed. All rights reserved by the project owner until a license file is added.
