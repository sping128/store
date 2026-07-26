# Spring Boot Learning Notes

## Topics Covered

### 1. Project Setup — `lesson-01-project-setup`
- Initializing a Spring Boot project

### 2. Dependency Injection — `lesson-02-dependency-injection`
- How Spring manages beans
- Injecting dependencies via constructor (`@Service`, `@Component`)
- Using interfaces for DI (e.g. `PaymentService` / `PaypalPaymentService`)

### 3. Configuration — `lesson-03-configuration`
- Reading values from `application.properties`

### 4. Thymeleaf — `lesson-04-thymeleaf`
- Server-side HTML templating with `@Controller` + `templates/`

### 5. Database — JPA & H2 — `lesson-05-jpa-h2`
- Connecting to a local H2 file database
- Mapping entities with `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- Auto schema management via `spring.jpa.hibernate.ddl-auto=update`
- Spring Data JPA repositories (`JpaRepository`)

### 6. Entity Relationships — `lesson-06-entity-relationships`
- `@ManyToOne` / `@JoinColumn` (Product → Category)

### 7. REST APIs — `lesson-07-rest-apis`
- `@RestController`, `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Path variables (`@PathVariable`)
- Query parameters (`@RequestParam`)
- Request headers (`@RequestHeader`)
- Returning `ResponseEntity` with proper HTTP status codes
- Building response `Location` header with `UriComponentsBuilder`

### 8. DTOs (Data Transfer Objects) — `lesson-08-dtos`
- Separating API shape from entity shape
- Different DTOs for different operations (register vs update vs response)

### 9. MapStruct — `lesson-09-mapstruct`
- Mapping between entities and DTOs with `@Mapper`
- Ignoring fields (`@Mapping(target = "...", ignore = true)`)
- Updating an existing entity in place (`@MappingTarget`)
- Injecting expressions into mappings (`expression = "java(...)"`)

### 10. JSON Annotations — `lesson-10-json-annotations`
- `@JsonProperty` — rename a field in JSON output
- `@JsonFormat` — format dates in JSON output

### 11. Swagger / OpenAPI — `lesson-11-swagger`
- Auto-generated API docs via `springdoc-openapi`
- Swagger UI at `/swagger-ui.html`

### 12. Full CRUD for a Resource — `lesson-12-full-crud`
- Implemented full GET / POST / PUT / DELETE for both Users and Products
- Change password endpoint (`POST /api/users/{id}/change-password`)

### 13. Bean Validation — `lesson-13-bean-validation`
- Added `spring-boot-starter-validation` dependency
- Field-level constraint annotations: `@NotBlank`, `@Email`, `@Size(min = ?)`
- Annotations live in `jakarta.validation.constraints.*`
- `@Valid` on `@RequestBody` in the controller activates validation
- On failure: Spring throws `MethodArgumentNotValidException` → automatic `400 Bad Request` before controller code runs
- Cross-field validation (e.g. new password ≠ old password) requires a custom `ConstraintValidator` — covered separately

### 14. Exception Handling with @ControllerAdvice — `lesson-14-exception-handling`
- `@ControllerAdvice` + `@RestController` — a global exception handler class that intercepts exceptions from all controllers
- `@ExceptionHandler(SomeException.class)` — routes a specific exception type to a handler method
- `@ResponseStatus(HttpStatus.BAD_REQUEST)` — sets the HTTP status on the response
- `MethodArgumentNotValidException.getBindingResult().getFieldErrors()` — extracts field-level validation errors
- Each `FieldError` has `.getField()` (field name) and `.getDefaultMessage()` (constraint message)
- Custom messages via `message = "..."` on constraint annotations (e.g. `@NotBlank(message = "Name is required")`)

### 15. Pagination & Sorting — `lesson-15-pagination-sorting`
- `Pageable` parameter in controller — Spring auto-binds `?page=0&size=10&sort=name,asc` from query string
- `Page<T>` return type wraps results with metadata: `content`, `totalPages`, `totalElements`, `size`, `number`
- `JpaRepository` inherits `findAll(Pageable)` from `PagingAndSortingRepository` — no extra code needed
- Custom repository methods support pagination: `Page<Product> findByCategoryId(Long id, Pageable pageable)`
- `page.map(mapper::toDto)` transforms content while preserving pagination metadata (don't use `.stream().map().toList()`)
- `@PageableDefault(size = 5)` sets the default page size when caller omits `?size=`
- Swagger: `@PageableAsQueryParam` on the method + `@Parameter(hidden = true)` on the `Pageable` param renders individual `page`/`size`/`sort` fields
- Sort format: `?sort=price,desc` — field name must match the entity field, not the DTO

### 16. JWT Authentication — `lesson-16-jwt-auth`
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

### 17. Role-Based Access Control (RBAC) — `lesson-17-rbac`
- `Role` enum in entity package with `@Enumerated(EnumType.STRING)` on the `User.role` field — stores `"ADMIN"`/`"USER"` as strings, not integers
- `ddl-auto=update` doesn't alter existing column types — must drop/recreate DB or run `ALTER TABLE` manually when changing enum storage type
- `CustomUserDetailsService implements UserDetailsService` — bridges your `User` entity to Spring Security; `loadUserByUsername` returns a `UserDetails` built with `.username()`, `.password()`, `.roles()`
- `.roles("ADMIN")` auto-prefixes to `ROLE_ADMIN` internally — matches `hasRole('ADMIN')` in `@PreAuthorize`
- `JwtAuthFilter` must load `UserDetails` and pass `userDetails.getAuthorities()` to `UsernamePasswordAuthenticationToken` — otherwise roles are never set in the security context
- `@EnableMethodSecurity` on `SecurityConfig` — enables `@PreAuthorize` on controller methods
- `@PreAuthorize("hasRole('ADMIN')")` on a method — Spring wraps the bean in a proxy that checks the expression before calling the method
- `AccessDeniedHandler` in `.exceptionHandling()` — hook for logging or customising 403 responses; use `@Slf4j` for structured logging

### 18. Refresh Tokens — `lesson-18-refresh-tokens`
- Login returns two JWTs: a short-lived **access token** (15 min) and a long-lived **refresh token** (1 day)
- `JwtService.generateToken` is `private`; callers use `generateAccessToken` / `generateRefreshToken` wrappers with durations as `private static final` constants
- A `type` claim (`"access"` / `"refresh"`) is embedded in every token to prevent misuse — constants live in `JwtService` as `TOKEN_TYPE_ACCESS` / `TOKEN_TYPE_REFRESH`
- `POST /api/auth/refresh` accepts `{"refreshToken":"..."}`, validates the token via `extractUsername` + `extractType`, and returns fresh tokens; catches `JwtException` for expired/tampered tokens
- `POST /api/auth/refresh` is added to `permitAll()` in `SecurityConfig`
- `JwtAuthFilter` wraps token parsing in a try/catch so an expired access token in the `Authorization` header returns 401 instead of propagating as 500/403
- Stateless refresh tokens cannot be revoked before expiry — DB-backed tokens (stored and deleted on logout) are required for revocation

### 19. @Transactional — `lesson-19-transactional`
- `@Transactional` wraps a method in a DB transaction — Spring opens it before the method runs, commits on success, rolls back on `RuntimeException`
- Spring uses a **proxy** to intercept calls: the annotation only takes effect when called through the proxy (i.e., from outside the bean)
- **Self-invocation gotcha**: calling `this.method()` bypasses the proxy — `@Transactional` on the inner method is silently ignored
- Default rollback: unchecked exceptions (`RuntimeException`) only — checked exceptions require `@Transactional(rollbackFor = ...)`
- `@Transactional(readOnly = true)` on query methods signals the DB to skip write locks — a small but free performance hint
- Business logic (including exception throwing) belongs in the service layer, not the controller — controllers should only translate service results to HTTP responses
- Custom exceptions (`UserNotFoundException`, `InvalidPasswordException`) extend `RuntimeException` and are mapped to HTTP status codes in `GlobalExceptionHandler` via `@ExceptionHandler`

### 20. Spring Events & Async Processing — `lesson-20-events-async`
- `ApplicationEventPublisher` — injected into a service to publish domain events; decouples side effects from the main flow
- Event class is a plain POJO (no need to extend `ApplicationEvent` since Spring 4.2)
- `@EventListener` on a method in a `@Component` — Spring automatically calls it when the matching event type is published
- `@EnableAsync` on a `@Configuration` class — enables async method execution globally
- `@Async` on a listener method — runs the handler on a separate thread pool so the HTTP request returns immediately
- `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` — fires the event only after the transaction commits; prevents the listener from reading stale data if it queries the DB
- `fallbackExecution = true` — fires even when no active transaction exists (useful for non-transactional publishers)
- Pattern: publish the event inside the `@Transactional` service method; the listener handles side effects (email, audit log, etc.) independently

### 21. Caching with @Cacheable — `lesson-21-caching`
- `spring-boot-starter-cache` dependency — brings in Spring's cache abstraction; uses a simple in-memory `ConcurrentHashMap` by default (no extra config needed for dev/learning)
- `@EnableCaching` on a `@Configuration` or `@SpringBootApplication` class — activates Spring's cache proxy infrastructure, same proxy pattern as `@Transactional` and `@Async`
- `@Cacheable(value="products", key="#id")` on a service method — on first call, executes the method and stores the result; on subsequent calls with the same key, skips the method and returns the cached value
- `@CachePut(value="products", key="#id")` on update — always executes the method and updates the cache entry; prevents stale data after a write
- `@CacheEvict(value="products", key="#id")` on delete — removes the entry from the cache when the underlying data is gone
- Cache annotations only work when called through the Spring proxy (same self-invocation gotcha as `@Transactional`)
- For production, swap the in-memory cache for Redis by adding `spring-boot-starter-data-redis` and setting `spring.cache.type=redis`

### 22. Redis Cache — `lesson-22-redis-cache`
- `spring-boot-starter-data-redis` — adds Redis client (Lettuce by default) and `RedisCacheManager`
- `spring.cache.type=redis`, `spring.data.redis.host`, `spring.data.redis.port` — switches cache backend to Redis
- `CacheConfig` — `@Bean RedisCacheConfiguration` configures serialization and TTL for all caches
- Keys: `StringRedisSerializer` — human-readable keys like `products::3` visible in `redis-cli`
- Values: custom `RedisSerializer<Object>` using Jackson 3 (`tools.jackson`) `JsonMapper` with `activateDefaultTyping` — embeds the class name in JSON so Spring can deserialize back to the correct type
- `BasicPolymorphicTypeValidator.builder().allowIfSubType("com.codewithmosh.store.")` — restricts polymorphic deserialization to classes in our own package; `allowIfBaseType(Object.class)` would allow *any* class name embedded in the JSON to be instantiated, which is a deserialization gadget-chain risk if that JSON is ever untrusted
- `DefaultTyping.NON_FINAL` tags every value whose declared type is not final, so `BigDecimal` (not a final class) gets a type id while `String` (final) does not - the allow-list therefore needs `allowIfSubType("java.math.")` too, or `price` fails to deserialize with `InvalidTypeIdException`
- Deserialization is not passive parsing: the embedded class name is loaded and instantiated, so whoever can write to Redis chooses which class your JVM constructs - hence an allow-list rather than a deny-list
- `DefaultTyping` is a top-level class in Jackson 3, not nested inside `ObjectMapper` (breaking change from Jackson 2)
- `entryTtl(Duration.ofMinutes(10))` — entries auto-expire; the default in-memory cache never expires
- `@Cacheable` / `@CachePut` / `@CacheEvict` on `ProductService` need no changes — the abstraction is transparent
- `@Cacheable(..., unless = "#result == null")` on `getProduct` — without it, Spring caches a miss (`null`) too, so a product created right after a failed lookup for the same id would keep reading as "not found" until the entry expires
- JSON format stored in Redis: `["com.example.MyDto", {...fields...}]` — first element is the type, second is the data
- For production: use `RedisCacheManagerBuilderCustomizer` to set per-cache TTLs instead of one global default
