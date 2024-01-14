

---

# ShortUrl

## Introduction/Description

ShortUrl is a URL shortening service designed using Spring Boot, MongoDB, and Thymeleaf. It provides a platform for users to create shortened versions of long URLs, manage them, and securely authenticate users. The service includes a RESTful API for automated URL operations and integrates with the Google Web Risk API for URL validation.

## Features

- **URL Shortening**: Simplify your web experience with easy-to-manage short URLs. Ideal for social media sharing, marketing campaigns, and data analytics.
- **User Dashboard**: A personalized dashboard for users to manage their shortened URLs. Track, edit, or remove URLs as needed.
- **User Authentication**: Robust authentication system ensuring secure access to user-specific functionalities and data.
- **QR Code Generation**: Enhance accessibility with QR codes for each shortened URL, perfect for print media and quick scanning.
- **Google Web Risk API Integration**: Every URL shortened is checked against Google's constantly updated database of unsafe web resources, ensuring the safety and credibility of your links.

## Technologies Used

- **Spring Boot**: For a powerful, extensible backend framework.
- **MongoDB**: Providing a flexible, scalable NoSQL database system.
- **Thymeleaf**: An efficient server-side Java template engine for dynamic web pages.
- **Java**: The core programming language offering reliability and a vast ecosystem.
- **Maven**: For dependable project management and build automation.

## Getting Started/Installation

### Prerequisites

- Java JDK 8 or newer.
- MongoDB installed and running.
- Maven for dependency management and project build.

### Installation Guide

1. **Clone the Repository**: 
   ```bash
   git clone https://github.com/pkalsi97/ShortUrl_Qr
   ```


2. **Navigate to the Project Directory**:
   ```bash
   cd ShortUrl
   ```

3. **Configure Application**:
   Edit the `application.properties` file to set up your MongoDB connection and other necessary configurations.
     

```properties

# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=myshorturl_db

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.mode=HTML
spring.thymeleaf.servlet.content-type=text/html 

# Application settings
app.baseUrl=http://localhost:8080
app.timeAllotted=48
app.reservedPaths=login,logout,register,...

# JWT Token Key
token.signing.key=[Your JWT Signing Key]

```

4. **Build the Project**:
   Using Maven, build the project to resolve dependencies:
   ```bash
   mvn clean install
   ```

5. **Run the Application**:
   Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   Access the application at `http://localhost:8080`.
---

## API Documentation for ShortUrl

ShortUrl's API provides comprehensive functionality for URL management, including user authentication, URL generation, and safety checks. Below are the detailed API endpoints along with examples.

### Authentication

To interact with most of the API endpoints, users must be authenticated and provided with a JWT token.

#### POST /authenticate
- **Purpose**: Authenticate users and issue a JWT token.
- **Request Body**: Include `username` and `password`.
- **Response**: JWT token for authenticated sessions.

### URL Management

#### GET /api/url/active
- **Purpose**: Retrieve all active URLs for the authenticated user.
- **Headers**: `Authorization: Bearer <JWT_Token>`
- **Response**: A list of active `ShortURLDto` objects.

#### GET /api/url/inactive
- **Purpose**: Fetch all inactive URLs for the authenticated user.
- **Headers**: `Authorization: Bearer <JWT_Token>`
- **Response**: A list of inactive `ShortURLDto` objects.

#### POST /api/url/generateRandom
- **Purpose**: Generate a random short URL.
- **Headers**: `Authorization: Bearer <JWT_Token>`
- **Request Body**: `{"originalUrl": "https://example.com"}`
- **Response**: The created `ShortURLDto` object.

#### POST /api/url/generateCustom
- **Purpose**: Generate a custom short URL.
- **Headers**: `Authorization: Bearer <JWT_Token>`
- **Request Body**: `{"originalUrl": "https://example.com", "backHalf": "custom123"}`
- **Response**: The created `ShortURLDto` object or an error message.

#### GET /api/url/checkSafety
- **Purpose**: Check the safety of a given URL using the Google Web Risk API.
- **Parameters**: `url` (the URL to check)
- **Response**: A message indicating if the URL is safe or not.

#### POST /api/url/disableUrl
- **Purpose**: Disable an active short URL.
- **Headers**: `Authorization: Bearer <JWT_Token>`
- **Request Body**: `{"shortUrl": "http://localhost:8080/custom123"}`
- **Response**: Success or failure message.

### Usage Examples

#### Authenticating a User

```bash
curl -X POST http://localhost:8080/authenticate \
-H "Content-Type: application/json" \
-d '{"username": "user", "password": "password"}'
```

#### Retrieving Active URLs

```bash
curl -X GET http://localhost:8080/api/url/active \
-H "Authorization: Bearer <JWT_Token>"
```

#### Generating a Custom Short URL

```bash
curl -X POST http://localhost:8080/api/url/generateCustom \
-H "Authorization: Bearer <JWT_Token>" \
-H "Content-Type: application/json" \
-d '{"originalUrl": "https://example.com", "backHalf": "custom123"}'
```

#### Checking URL Safety

```bash
curl -X GET http://localhost:8080/api/url/checkSafety?url=https://example.com
```

### URL Validation
- Utilizes Google Web Risk API to check the safety of the original URLs before shortening.
## Contributing

Contributions are welcome. Fork the repository, make changes, and submit a pull request. Please adhere to the existing coding style and standards.
