# User Access Management Backend

This project is a backend service for managing user access, including authentication, authorization, and user management functionalities. It is built using Spring Boot and Kotlin, and it leverages JWT for secure authentication.

## Features

- User registration and email verification
- User login and JWT-based authentication
- Password reset functionality
- Role-based access control
- Secure password storage and validation
- Comprehensive logging for audit purposes

## Prerequisites

- Java 21 or higher
- Kotlin 2.1 or higher
- PostgreSQL database
- Gradle 8.10 or higher

## Getting Started

### Clone the Repository

```bash

git clone https://github.com/nenadjakic/user-access-management-be.git
cd user-access-management-be
```

### Configure the Database
Ensure that your PostgreSQL database is running and update the application.yaml file with your database credentials:

### Build and Run the Application
Use Gradle to build and run the application:

```bash

./gradlew build
./gradlew bootRun
```

The application will start on http://localhost:8080.

## License
This project is licensed under the Apache License.