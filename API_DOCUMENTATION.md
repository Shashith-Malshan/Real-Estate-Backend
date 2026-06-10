# Real Estate Platform - API Documentation

## Base URL
`http://localhost:8080/api`

---

## 1. Authentication APIs (`/auth`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| POST | `/auth/register` | Register a new user | `UserRegistrationDTO` | `UserResponseDTO` |
| POST | `/auth/login` | Login and get token | `UserLoginDTO` | `UserResponseDTO` (with JWT) |
| GET | `/auth/users/{userId}/roles` | Get roles for a user | None | `List<Long>` (Role IDs) |
| POST | `/auth/switch-role` | Switch active role | `RoleSwitchRequestDTO` | `UserResponseDTO` |

---

## 2. Property APIs (`/properties`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| GET | `/properties` | Get all properties | None | `List<PropertyDTO>` |
| GET | `/properties/{id}` | Get property by ID | None | `PropertyDTO` |
| GET | `/properties/search/district?district={val}`| Search by district | None | `List<PropertyDTO>` |
| GET | `/properties/search/location?location={val}`| Search by location | None | `List<PropertyDTO>` |
| GET | `/properties/search/category?categoryId={val}`| Search by category | None | `List<PropertyDTO>` |
| GET | `/properties/search?district={d}&categoryId={c}`| Advanced search | None | `List<PropertyDTO>` |
| POST | `/properties` | Create new property (Seller) | `PropertyCreateDTO` | `PropertyDTO` |
| PUT | `/properties/{id}` | Update property (Seller) | `PropertyDTO` | `PropertyDTO` |
| DELETE| `/properties/{id}` | Delete property (Seller/Admin)| None | `204 No Content` |

---

## 3. Inquiry APIs (`/inquiries`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| POST | `/inquiries` | Submit new inquiry | `InquiryCreateDTO` | `InquiryDTO` |
| GET | `/inquiries/{id}` | Get inquiry by ID | None | `InquiryDTO` |
| GET | `/inquiries/customer/{id}` | Get customer inquiries | None | `List<InquiryDTO>` |
| GET | `/inquiries/property/{id}` | Get property inquiries | None | `List<InquiryDTO>` |
| GET | `/inquiries/property/{id}/unanswered` | Get unanswered inquiries | None | `List<InquiryDTO>` |
| PUT | `/inquiries/{id}/reply` | Mark as replied (Seller) | None | `InquiryDTO` |
| DELETE| `/inquiries/{id}` | Delete inquiry | None | `204 No Content` |

---

## 4. Visit APIs (`/visits`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| POST | `/visits` | Schedule a visit | `VisitCreateDTO` | `VisitDTO` |
| GET | `/visits/{id}` | Get visit by ID | None | `VisitDTO` |
| GET | `/visits/customer/{id}` | Get customer visits | None | `List<VisitDTO>` |
| GET | `/visits/property/{id}` | Get property visits | None | `List<VisitDTO>` |
| GET | `/visits/property/{id}/pending`| Get pending visits | None | `List<VisitDTO>` |
| PUT | `/visits/{id}/complete` | Mark visit completed | None | `VisitDTO` |
| DELETE| `/visits/{id}` | Cancel/delete visit | None | `204 No Content` |

---

## 5. Deal APIs (`/deals`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| POST | `/deals` | Create a property deal | `PropertyDealCreateDTO` | `PropertyDealDTO` |
| GET | `/deals/{id}` | Get deal by ID | None | `PropertyDealDTO` |
| GET | `/deals/customer/{id}` | Get deals by customer | None | `List<PropertyDealDTO>` |
| GET | `/deals/property/{id}` | Get deals by property | None | `List<PropertyDealDTO>` |
| DELETE| `/deals/{id}` | Delete deal | None | `204 No Content` |

---

## 6. Admin APIs (`/admin`) - System Monitoring

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| GET | `/admin/users` | Get all users | None | `List<UserResponseDTO>` |
| GET | `/admin/users/{id}` | Get user by ID | None | `UserResponseDTO` |
| DELETE| `/admin/users/{id}` | Delete user (Admin only) | None | `204 No Content` |
| GET | `/admin/metrics` | Get system statistics | None | `SystemMetricsDTO` |

---

## Security Instructions

### JWT Authentication
All endpoints except `/auth/register` and `/auth/login` and public `GET /properties` should require a valid JWT token. 

**Header Format:**
```
Authorization: Bearer <your_jwt_token_here>
```

**Role Based Access Control (RBAC):**
- **Seller Endpoints**: Only accessible to users with the `SELLER` role.
- **Buyer Endpoints**: Only accessible to users with the `BUYER` role.
- **Admin Endpoints**: Only accessible to users with the `ADMIN` role.

*Note: Role verification is done on the backend via Spring Security and JWT Claims.*
