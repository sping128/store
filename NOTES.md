# Spring Boot Learning Notes

## Topics Covered

### 1. Project Setup
- Initializing a Spring Boot project

### 2. Dependency Injection
- How Spring manages beans
- Injecting dependencies via constructor (`@Service`, `@Component`)
- Using interfaces for DI (e.g. `PaymentService` / `PaypalPaymentService`)

### 3. Configuration
- Reading values from `application.properties`

### 4. Thymeleaf
- Server-side HTML templating with `@Controller` + `templates/`

### 5. Database — JPA & H2
- Connecting to a local H2 file database
- Mapping entities with `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- Auto schema management via `spring.jpa.hibernate.ddl-auto=update`
- Spring Data JPA repositories (`JpaRepository`)

### 6. Entity Relationships
- `@ManyToOne` / `@JoinColumn` (Product → Category)

### 7. REST APIs
- `@RestController`, `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Path variables (`@PathVariable`)
- Query parameters (`@RequestParam`)
- Request headers (`@RequestHeader`)
- Returning `ResponseEntity` with proper HTTP status codes
- Building response `Location` header with `UriComponentsBuilder`

### 8. DTOs (Data Transfer Objects)
- Separating API shape from entity shape
- Different DTOs for different operations (register vs update vs response)

### 9. MapStruct
- Mapping between entities and DTOs with `@Mapper`
- Ignoring fields (`@Mapping(target = "...", ignore = true)`)
- Updating an existing entity in place (`@MappingTarget`)
- Injecting expressions into mappings (`expression = "java(...)"`)

### 10. JSON Annotations
- `@JsonProperty` — rename a field in JSON output
- `@JsonFormat` — format dates in JSON output

### 11. Swagger / OpenAPI
- Auto-generated API docs via `springdoc-openapi`
- Swagger UI at `/swagger-ui.html`

### 12. Full CRUD for a Resource
- Implemented full GET / POST / PUT / DELETE for both Users and Products
- Change password endpoint (`POST /api/users/{id}/change-password`)

### 13. Bean Validation
- Added `spring-boot-starter-validation` dependency
- Field-level constraint annotations: `@NotBlank`, `@Email`, `@Size(min = ?)`
- Annotations live in `jakarta.validation.constraints.*`
- `@Valid` on `@RequestBody` in the controller activates validation
- On failure: Spring throws `MethodArgumentNotValidException` → automatic `400 Bad Request` before controller code runs
- Cross-field validation (e.g. new password ≠ old password) requires a custom `ConstraintValidator` — covered separately

### 14. Exception Handling with @ControllerAdvice
- `@ControllerAdvice` + `@RestController` — a global exception handler class that intercepts exceptions from all controllers
- `@ExceptionHandler(SomeException.class)` — routes a specific exception type to a handler method
- `@ResponseStatus(HttpStatus.BAD_REQUEST)` — sets the HTTP status on the response
- `MethodArgumentNotValidException.getBindingResult().getFieldErrors()` — extracts field-level validation errors
- Each `FieldError` has `.getField()` (field name) and `.getDefaultMessage()` (constraint message)
- Custom messages via `message = "..."` on constraint annotations (e.g. `@NotBlank(message = "Name is required")`)

### 15. Pagination & Sorting

- `Pageable` parameter in controller — Spring auto-binds `?page=0&size=10&sort=name,asc` from query string
- `Page<T>` return type wraps results with metadata: `content`, `totalPages`, `totalElements`, `size`, `number`
- `JpaRepository` inherits `findAll(Pageable)` from `PagingAndSortingRepository` — no extra code needed
- Custom repository methods support pagination: `Page<Product> findByCategoryId(Long id, Pageable pageable)`
- `page.map(mapper::toDto)` transforms content while preserving pagination metadata (don't use `.stream().map().toList()`)
- `@PageableDefault(size = 5)` sets the default page size when caller omits `?size=`
- Swagger: `@PageableAsQueryParam` on the method + `@Parameter(hidden = true)` on the `Pageable` param renders individual `page`/`size`/`sort` fields
- Sort format: `?sort=price,desc` — field name must match the entity field, not the DTO

### 16. JWT Authentication

- Added `spring-boot-starter-security`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson` dependencies
- `JwtService` — generates and validates signed JWT tokens using a `SecretKey` built from a Base64 secret in `application.properties`
  - `generateToken(username)` — builds a JWT with subject, issuedAt, expiration, signed with HMAC-SHA key
  - `extractUsername(token)` — parses and verifies the token, returns the subject claim
- `JwtAuthFilter extends OncePerRequestFilter` — runs once per request, reads `Authorization: Bearer <token>`, extracts username, sets `SecurityContextHolder` authentication
- `SecurityConfig` — `@EnableWebSecurity` bean that configures stateless sessions, disables CSRF and form login, permits `/api/auth/login` and Swagger paths, locks everything else
- `AuthController` — `POST /api/auth/login` verifies username + password, returns a JWT string
- `UsernamePasswordAuthenticationToken(username, null, List.of())` — Spring Security's way of representing an authenticated user in the security context
- `@Value("${jwt.secret}")` on a constructor parameter injects the secret from `application.properties`
- `Optional` from `findBy*` is never `null` — use `.isEmpty()` / `.isPresent()`, or return plain object type and null-check

### 17. Role-Based Access Control (RBAC)

- `Role` enum in entity package with `@Enumerated(EnumType.STRING)` on the `User.role` field — stores `"ADMIN"`/`"USER"` as strings, not integers
- `ddl-auto=update` doesn't alter existing column types — must drop/recreate DB or run `ALTER TABLE` manually when changing enum storage type
- `CustomUserDetailsService implements UserDetailsService` — bridges your `User` entity to Spring Security; `loadUserByUsername` returns a `UserDetails` built with `.username()`, `.password()`, `.roles()`
- `.roles("ADMIN")` auto-prefixes to `ROLE_ADMIN` internally — matches `hasRole('ADMIN')` in `@PreAuthorize`
- `JwtAuthFilter` must load `UserDetails` and pass `userDetails.getAuthorities()` to `UsernamePasswordAuthenticationToken` — otherwise roles are never set in the security context
- `@EnableMethodSecurity` on `SecurityConfig` — enables `@PreAuthorize` on controller methods
- `@PreAuthorize("hasRole('ADMIN')")` on a method — Spring wraps the bean in a proxy that checks the expression before calling the method
- `AccessDeniedHandler` in `.exceptionHandling()` — hook for logging or customising 403 responses; use `@Slf4j` for structured logging
