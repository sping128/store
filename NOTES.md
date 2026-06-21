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
