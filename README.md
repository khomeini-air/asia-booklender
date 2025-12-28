# Book Lender (Loan) API

A secure RESTful API for managing a book lending system with configurable borrowing rules.

## Project Scope

### In Scope

- Authentication & Authorization: JWT token-based authentication and role-based access control (ADMIN, MEMBER).
- APIs for Member, Book, and Loan management/features with consistent response format.
- Member API will only cover the get all (`/members`) for Admin,  and get me (`/members/me`) for all users. The remaining operations will have similar pattern with Book and Loan API.
- Configurable business rule: Maximum active loans (default: 3), overdue loans enforcement (default: true), and due days (default: 14 days).
- Adaptive hybrid locking for concurrent borrow operations to ensure Data Consistency.
- Rational database with PostgreSQL.
- Containerization with Docker and Docker Compose setup.
- Monitoring and Observability with Spring Actuator integrated with Micrometer and Prometheus, and comprehensive logging with tracingId across services.
- API Documentation with OpenAPI 3.0 and Swagger UI.
- Data Initialization with idempotent seed data scripts for Members and Books.
- System is capable of handling medium throughput of high-demand books, 1000 users for one book.
- System latency is medium.


### Out of Scope
- Unit Tests, Integration Tests, Performance Tests, Security Tests. Implementing those tests will require huge amount of dedications that only worth, in my opinion, if the candidate got shortlisted 
- User and Member registration, logout, password reset, and refresh token.
- Support for multiple book loan
- Full-Text Search for members, books and loans history.
- Book wishlist, book tags, and book reservations
- Reviews and Ratings
- CI/CD and Production readiness
- Horizontal Scalability with load balancing, multiple instances and database sharding/read replica.
- Caching for frequently accessed data (popular books)
- Distributed lock for ultra-high throughput and low latency. 

---

## Tech Stack

- Java 17
- Spring Boot 3.5.6
- PostgreSQL
- Gradle
- Docker & Docker Compose
- Swagger/OpenAPI 3.0
- Spring Actuator with Micrometer and Prometheus

## Architecture & Design

### High-level Architecture
```
                        ┌──────────────────────────┐
                        │        API Clients          │
                        │  (Web / REST Consumers)     │
                        └─────────────┬────────────┘
                                      │
                                      ▼
                    ┌──────────────────────────────────┐
                    │     Spring Security Filter Chain    │
                    │                                     │   
                    │  - RequestLoggingFilter             │
                    │    (traceId, request logging)       │
                    │  - JwtAuthFilter                    │
                    │    (JWT validation, auth)           │
                    └─────────────┬────────────────────┘
                                  │
                                  ▼
┌───────────────────────────────────────────────────────────────┐
│                        Package-by-Feature Layer                     │
│                                                                     │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐         │
│  │   auth        │   │   book         │   │   loan        │         │
│  │──────────────│   │──────────────│   │──────────────│         │
│  │ api           │   │ api            │   │ api           │         │
│  │ dto           │   │ dto            │   │ dto           │         │
│  │ service       │   │ service        │   │ service       │         │
│  │ security      │   │ repository     │   │ repository    │         │
│  │ filter        │   │ mapper         │   │ mapper        │         │
│  └──────────────┘   └──────────────┘    └──────────────┘        │
│                                                                     │
│  ┌─────────────┐                                                   │
│  │  member      │                                                   │
│  │─────────────│                                                   │
│  │ api          │                                                   │
│  │ dto          │                                                   │
│  │ service      │                                                   │
│  │ repository   │                                                   │
│  │ mapper       │                                                   │
│  └─────────────┘                                                   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────┐       │
│  │                        shared                            │       │
│  │----------------------------------------------------------│       │
│  │ api (ApiResponse, Pagination)                            │       │
│  │ exception (GlobalExceptionHandler)                       │       │
│  │ observability (logging)                                  │       │
│  │ security (SecurityUtil, CurrentUser)                     │       │    
│  └─────────────────────────────────────────────────────┘       │
└───────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────────┐
                    │        Repository Layer             │
                    │      (Spring Data JPA)              │
                    └────────────┬────────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────────┐
                    │        PostgreSQL Database          │
                    └──────────────────────────────────┘

```


### Key Design

1. **Package-by-Feature**: Enforce feature isolation, allow each to evolve independently and easily convert them into microservices later.
2. **Adaptive Hybrid Locking**: Use optimistic locking for high inventory, pessimistic for low inventory. Result in better throughput for abundant books and Guaranteed consistency for scarce books.
3. **JWT Token Authentication**: Stateless JWT tokens for better scalability, distributed-friendly, and mobile-friendly.
4. **Member email from JWT Token**: More secure as it prevents users from impersonating others, cleaner API, and ensuring resource ownership implicit in the authentication.
5. **Global Exception Handling**: Clean controller code, consistent error response.


### Data Model

```
┌────────────┐         ┌─────────────┐         ┌─────────────┐
│    Book     │         │    Loan      │          │   Member     │
├────────────┤         ├─────────────┤         ├─────────────┤
│ id (PK)     │───┐     │ id (PK)      │    ┌────│ id (PK)      │
│ title       │   │     │ bookId (FK)  │────┘    │ name         │
│ author      │   └────│ memberId(FK)  │         │ email (UK)   │
│ isbn (UK)   │         │ borarowedAt  │         └─────────────┘
│ totalCopies │         │ dueAt        │   
│ availableC. │         │ returnedAt   │ 
│ version     │         └─────────────┘
└────────────┘
```
Apart from the core attributes above, each entity by default also have the following:
1. created_by
2. updated_by
3. created_at
4. updated_at

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Running with Docker

1. Clone the repository and navigate to project directory

2. Build and start the containers:
```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`

### Access Points

After starting the application, you can access:

| Endpoint                                    | Purpose | Authentication |
|---------------------------------------------|---------|----------------|
| http://localhost:8080/swagger-ui/index.html | Interactive API Documentation | None (public)  |
| http://localhost:8080/api-docs              | OpenAPI Specification (JSON) | None (public)  |
| http://localhost:8080/actuator/health       | Health Check | Admin only     |
| http://localhost:8080/actuator/info         | Application Info | Admin only     |
| http://localhost:8080/actuator/metrics      | Metrics | Admin only     |
| http://localhost:8080/actuator/prometheus   | Prometheus Metrics | Admin only     |


## Authentication & Authorization

The API uses JWT (JSON Web Token) based authentication. Users must login first to receive a token, then use that token for subsequent API calls.

### Available Users:

| Email                | Password  | Role | Linked Member |
|----------------------|-----------|------|---------------|
| admin@booklender.com | admin123  | ADMIN | Admin         |
| donald.trump@usa.com | donald123 | MEMBER | Donald Trump  |
| barack.obama@usa.com | barack123 | MEMBER | Barack Obama  |
| john.rambo@usa.com   | john123   | MEMBER | John Rambo    |

### Authentication Flow:

1. **Login to get JWT token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "donald.trump@usa.com",
    "password": "donald123"
  }'
```

Response:
```json
{
  "result": {
    "result": "S",
    "code": "SUCCESS",
    "description": "Success"
  },
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLC..",
    "email": "donald.trump@usa.com",
    "authority": "ROLE_MEMBER",
    "memberId": 6
  }
}
```

2. **Use the token in subsequent requests:**
```bash
curl http://localhost:8080/api/members/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLC.."
```

### Token Details:
- Expiration: configurable, default 10 minutes
- Type: Bearer token
- Algorithm: HMAC SHA-256

## API Endpoints

**For detailed API documentation with request/response examples, schemas, and interactive testing, please visit the Swagger UI at http://localhost:8080/swagger-ui/index.html**

### Quick Reference

Below is a summary of available endpoints. For complete details, refer to Swagger documentation.

### Authentication (Public)

- `POST /api/auth/login` - Login and receive JWT token

### Books 

- `GET /api/books` - Get all books.
- `GET /api/books/{id}` - Get book by ID.
- `POST /api/admin/books` - Create a new book, Admin only.
- `PUT /api/admin/books/{id}` - Update a book, Admin only.
- `DELETE /api/admin/books/{id}` - Delete a book, Admin only.

### Members

- `GET /api/admin/members` - Get all members - Admin only.
- `GET /api/members/me` - Get member details for authenticated user.
- Other APIs are not implemented due to time constraint.

### Loans (Member operations use authenticated user from JWT)

- `POST /api/loans/{bookId}` - Borrow a book (uses authenticated member from JWT)
- `POST /api/loans/return/{loanId}` - Return a book (can only return own loans)
- `GET /api/loans/my?page&size` - Get my loans
- `GET /api/loans/my?activeOnly=true&page&size` - Get my active loans
- `GET /api/loans/{loanId}` - Get specific loan details. Member can only view own loans. Admin can view all loans.
- `GET /api/admin/loans?page&size` - Get all loans from all users, Admin only.

## API Response Structure
API response will contain the following field and its data type:
1. `result` (mandatory) : `ResultEnum` - Representing the result code.
2. `pagination` (mandatory if the data is an array): `Pagination`.
3. `data` (mandatory) : `Object` or `Array` representing the server resource returned to the client to consume e.g. Book, Loan.
4. `message` (optional) : `Text` - more detail message if available.

### ResultEnum
It is an Enum that provides the result code of the API calls whether it is successful or failed.
It has the following attribute:
1. `result`: `String` : `S` | `F` - indicates the result, `S`=Success, `F`=Failed.
2. `code`: `String` - constants for the results code, example `SUCCESS`, `BAD_CREDENTIALS`. 
3. `description`: `Text` - short description of the result

The following are the list of value of `ResultEnum`:

| result                     | code | description                   | 
|---------------------------|----------|-------------------------------|
| `S`     | `"SUCCESS"` | Success                       | 
| `F`     | `"BAD_CREDENTIALS"` | User is not aunthenticated/authorized | 
| `F`    | `"PARAM_ILLEGAL"` |Bad Parameter                  | 
| `F` | `"RESOURCE_NOT_FOUND"` | Resource Not Found            |
| `F` | `"BOOK_NOT_AVAILABLE"` | Book not available for loan   |
| `F` | `"MAX_LOAN_EXCEEDED"` | Maximum loans exceeded        |
| `F` | `"LOAN_OVERDUE"` | User has loan overdue         |
| `F` | `"ACCESS_DENIED"` | User is not authorized to access the service |
| `F` | `"LOAN_ALREADY_RETURNED"` | The loan has been returned previously      |
| `F` | `"INTERNAL_ERROR"` | MEMBER                        |


### Pagination
Indicates the page of the array of resources returned by the Server:
1. currentPage: int - the current page of the result.
2. pageSize: int - the number of resources returned in the page.
3. totalElements: int - the total number of resources.
4. totalPages: int - total number of pages.


## API Examples

### Login
Donald log in to the system.

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "donald.trump@usa.com",
    "password": "donald123"
  }'
```

Response:
```json
{
  "result": {
    "result": "S",
    "code": "SUCCESS",
    "description": "Success"
  },
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLC..",
    "email": "donald.trump@usa.com",
    "authority": "ROLE_MEMBER",
    "memberId": 6
  }
}
```

Save the token for subsequent requests.

### Borrow a Book
Donald borrow the book  'Clean Code' by Robert Martin

```bash
curl -X POST 'localhost:8080/api/loans/5' -H 'Authorization: Bearer eyJhbGciOiJIUzI1Ni..' 
```

Response:
```json
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "data": {
        "id": 6,
        "borrowedAt": 1766762806867,
        "dueAt": 1767972406858,
        "book": {
            "id": 5,
            "title": "Clean Code",
            "author": "Robert C. Martin",
            "isbn": "978-0132350884",
            "totalCopies": 5,
            "availableCopies": 3
        },
        "member": {
            "id": 6,
            "name": "Donald Trump",
            "email": "donald.trump@usa.com"
        }
    }
}
```

### Return a Book
Donald returns the book

```bash
curl -X POST 'localhost:8080/api/loans/return/6' -H 'Authorization: Bearer eyJhbGciOiJIUzI1Ni..' 

# Donald cannot return Obama's loan - authorization check prevents this
# Returns: 403 Forbidden - "You can only return your own loans"
```

Response:
```json
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "data": {
        "id": 6,
        "borrowedAt": 1766762806867,
        "returnedAt": 1766763336442,
        "dueAt": 1767972406858,
        "book": {
            "id": 5,
            "title": "Clean Code",
            "author": "Robert C. Martin",
            "isbn": "978-0132350884",
            "totalCopies": 5,
            "availableCopies": 4
        },
        "member": {
            "id": 6,
            "name": "Donald Trump",
            "email": "donald.trump@usa.com"
        }
    }
}
```

Note that Donald can not return Obama's loan, getting the following failed response:
`403 Forbidden`
```json 
{
    "result": {
        "result": "F",
        "code": "ACCESS_DENIED",
        "description": "User is not authorized to access the service"
    },
    "message": "You can only return your own loans"
}
```

## Monitoring & Observability

### Spring Actuator

The application includes Spring Boot Actuator for operational monitoring and management. All of them require admin role.

**Available Endpoints:**

| Endpoint | Description | Access     |
|----------|-------------|------------|
| `/actuator/health` | Application health status | Admin only |
| `/actuator/info` | Application information | Admin only |
| `/actuator/metrics` | Application metrics | Admin only |
| `/actuator/prometheus` | Prometheus-formatted metrics | Admin only |


## Stopping the Application

With Docker:
```bash
docker-compose down
```

To remove volumes:
```bash
docker-compose down -v
```