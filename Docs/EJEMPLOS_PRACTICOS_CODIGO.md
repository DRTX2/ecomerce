# EJEMPLOS PRÁCTICOS DE CÓDIGO - PATRONES HEXAGONALES

## 1. PATRÓN DE CART → ORDER (Ejemplo Completo)

### 1.1 Modelos de Dominio

```java
// ✅ CORRECT: core/model/order/Cart.java
public class Cart {
    private Long id;
    private User user;
    private List<CartItem> items;
    
    public void addItem(CartItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }
    
    public BigDecimal getTotal() {
        return items.stream()
            .map(item -> item.getProduct().getPrice()
                .multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// ✅ CORRECT: core/model/order/CartItem.java
public class CartItem {
    private Long id;
    private Cart cart;
    private Product product;
    private Integer quantity;
    
    // Precio NO se almacena - usa el actual del producto
}

// ✅ CORRECT: core/model/order/Order.java
public class Order {
    private Long id;
    private User user;
    private List<OrderItem> items;
    private BigDecimal total;  // FIJADO al crear
    private OrderState orderState;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<Discount> appliedDiscounts;
    
    // NO se puede modificar total ni items
    // Solo cambiar estado
    public void updateState(OrderState newState) {
        this.orderState = newState;
    }
}

// ✅ CORRECT: core/model/order/OrderItem.java
public class OrderItem {
    private Long id;
    private Order order;
    private Product product;
    private Integer quantity;
    private BigDecimal priceAtPurchase;  // ⭐ INMUTABLE
    
    // El precio se fija al crear la orden
}
```

### 1.2 Puertos (Contratos)

```java
// ✅ CORRECT: core/ports/in/rest/CartUseCasePort.java
public interface CartUseCasePort {
    Cart createCart(Cart cart);
    Optional<Cart> getCartById(Long id);
    List<Cart> getAllCarts(Long userId);
    Cart updateCart(Long id, Cart cart);
    void deleteCart(Long id);
}

// ✅ CORRECT: core/ports/in/rest/OrderUseCasePort.java
public interface OrderUseCasePort {
    Order createOrderFromCart(Long cartId, Long userId);  // ⭐ CONVERSIÓN
    Optional<Order> getOrderById(Long id);
    List<Order> getAllOrders();
    Order updateOrderState(Long id, OrderState newState);
    void cancelOrder(Long id);
}

// ✅ CORRECT: core/ports/out/persistence/CartRepositoryPort.java
public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(Long id);
    List<Cart> findAll(Long userId);
    Cart update(Cart cart);
    void delete(Long id);
}

// ✅ CORRECT: core/ports/out/persistence/OrderRepositoryPort.java
public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Order update(Order order);
    void delete(Long id);
}
```

### 1.3 Casos de Uso (Orquestación)

```java
// ✅ CORRECT: application/usecases/CartUseCaseImpl.java
@Service
@RequiredArgsConstructor
public class CartUseCaseImpl implements CartUseCasePort {
    private final CartRepositoryPort cartRepository;
    
    @Override
    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }
    
    @Override
    public List<Cart> getAllCarts(Long userId) {
        return cartRepository.findAll(userId);
    }
    
    @Override
    public Cart updateCart(Long id, Cart cart) {
        return cartRepository.update(cart);
    }
    
    @Override
    public void deleteCart(Long id) {
        cartRepository.delete(id);
    }
}

// ✅ CORRECT: application/usecases/OrderUseCaseImpl.java
@Service
@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCasePort {
    private final OrderRepositoryPort orderRepository;
    private final CartRepositoryPort cartRepository;
    
    @Override
    public Order createOrderFromCart(Long cartId, Long userId) {
        // 1. Obtener carrito
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        
        // 2. Crear orden con items del carrito
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderState(OrderState.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        
        // 3. Convertir CartItems a OrderItems
        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                
                // ⭐ FIJO EL PRECIO EN ESTE MOMENTO
                orderItem.setPriceAtPurchase(
                    cartItem.getProduct().getPrice()
                );
                orderItem.setOrder(order);
                return orderItem;
            })
            .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // 4. Calcular total FIJADO
        BigDecimal total = orderItems.stream()
            .map(item -> item.getPriceAtPurchase()
                .multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotal(total);
        
        // 5. Guardar orden
        Order savedOrder = orderRepository.save(order);
        
        // 6. VACIAR CARRITO
        cartRepository.delete(cartId);
        
        return savedOrder;
    }
}
```

### 1.4 Adaptadores IN (REST)

```java
// ✅ CORRECT: adapters/in/rest/cart/CartController.java
@RestController
@RequestMapping("/api/carts")
@AllArgsConstructor
public class CartController {
    private final CartUseCasePort cartService;
    private final CartRestMapper mapper;
    
    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts(
            @RequestParam Long userId) {
        List<Cart> carts = cartService.getAllCarts(userId);
        return ResponseEntity.ok(
            carts.stream()
                .map(mapper::toResponse)
                .toList());
    }
    
    @PostMapping
    public ResponseEntity<CartResponse> createCart(
            @RequestBody @jakarta.validation.Valid CartRequest cart) {
        Cart newCart = mapper.toDomain(cart);
        return ResponseEntity.ok(mapper.toResponse(
            cartService.createCart(newCart)));
    }
}

// ✅ CORRECT: adapters/in/rest/order/OrderController.java
@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderUseCasePort orderService;
    private final OrderRestMapper mapper;
    
    // Convertir carrito en orden
    @PostMapping("/from-cart/{cartId}")
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @PathVariable Long cartId,
            @RequestParam Long userId) {
        Order order = orderService.createOrderFromCart(cartId, userId);
        return ResponseEntity.ok(mapper.toResponse(order));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### 1.5 Mappers

```java
// ✅ CORRECT: adapters/in/rest/cart/mappers/CartRestMapper.java
@Mapper(componentModel = "spring")
public interface CartRestMapper {
    CartResponse toResponse(Cart cart);
    Cart toDomain(CartRequest request);
}

// ✅ CORRECT: adapters/out/persistence/cart/CartPersistenceMapper.java
@Component
public class CartPersistenceMapper {
    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;
        
        Cart cart = new Cart();
        cart.setId(entity.getId());
        cart.setUser(new User(entity.getUser().getId()));
        cart.setItems(entity.getItems().stream()
            .map(this::itemToDomain)
            .collect(Collectors.toList()));
        
        return cart;
    }
    
    public CartEntity toEntity(Cart domain) {
        if (domain == null) return null;
        
        CartEntity entity = new CartEntity();
        entity.setId(domain.getId());
        entity.setUser(new UserEntity(domain.getUser().getId()));
        entity.setItems(domain.getItems().stream()
            .map(this::itemToEntity)
            .collect(Collectors.toList()));
        
        return entity;
    }
    
    private CartItem itemToDomain(CartItemEntity entity) {
        CartItem item = new CartItem();
        item.setId(entity.getId());
        item.setProduct(new Product(entity.getProduct().getId()));
        item.setQuantity(entity.getQuantity());
        return item;
    }
    
    private CartItemEntity itemToEntity(CartItem domain) {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(domain.getId());
        entity.setProduct(new ProductEntity(domain.getProduct().getId()));
        entity.setQuantity(domain.getQuantity());
        return entity;
    }
}
```

### 1.6 DTOs

```java
// ✅ CORRECT: adapters/in/rest/cart/dtos/CartRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    @NotEmpty
    private List<CartItemRequest> items;
}

// ✅ CORRECT: adapters/in/rest/cart/dtos/CartResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal total;
}

// ✅ CORRECT: adapters/in/rest/order/dtos/OrderRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull
    private Long cartId;
    
    @NotNull
    private Long userId;
}

// ✅ CORRECT: adapters/in/rest/order/dtos/OrderResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<OrderItemDto> items;
    private BigDecimal total;
    private OrderState state;
    private LocalDateTime createdAt;
}

// ✅ CORRECT: adapters/in/rest/order/dtos/OrderItemDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;  // ⭐ El precio fijado
}
```

---

## 2. PATRÓN DE SHIPPING

### 2.1 Modelo de Dominio

```java
// ✅ CORRECT: core/model/shipping/Shipping.java
public class Shipping {
    private Long id;
    private Order order;
    private String deliveryAddress;
    private ShippingStatus status;
    private String carrier;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    
    // Métodos de dominio
    public void markAsShipped(String trackingNumber) {
        this.status = ShippingStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
    }
    
    public void markAsDelivered() {
        this.status = ShippingStatus.DELIVERED;
    }
    
    public boolean isDelivered() {
        return status == ShippingStatus.DELIVERED;
    }
}

// ✅ CORRECT: core/model/shipping/ShippingStatus.java
public enum ShippingStatus {
    PENDING,      // Esperando ser procesado
    PROCESSING,   // En preparación
    SHIPPED,      // En tránsito
    DELIVERED,    // Entregado
    RETURNED      // Devuelto
}
```

### 2.2 Caso de Uso

```java
// ✅ CORRECT: application/usecases/ShippingUseCaseImpl.java
@Service
@RequiredArgsConstructor
public class ShippingUseCaseImpl implements ShippingUseCasePort {
    private final ShippingRepositoryPort shippingRepository;
    private final OrderRepositoryPort orderRepository;
    
    @Override
    public Shipping createShippingForOrder(Long orderId, 
            String deliveryAddress, String carrier) {
        
        // 1. Obtener orden
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        // 2. Crear envío
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setDeliveryAddress(deliveryAddress);
        shipping.setCarrier(carrier);
        shipping.setStatus(ShippingStatus.PENDING);
        shipping.setEstimatedDelivery(
            LocalDateTime.now().plusDays(5)
        );
        
        // 3. Guardar
        return shippingRepository.save(shipping);
    }
    
    @Override
    public Shipping updateShippingStatus(Long shippingId, 
            ShippingStatus newStatus, String trackingNumber) {
        
        Shipping shipping = shippingRepository.findById(shippingId)
            .orElseThrow(() -> new EntityNotFoundException("Shipping not found"));
        
        // Usar método de dominio
        if (newStatus == ShippingStatus.SHIPPED) {
            shipping.markAsShipped(trackingNumber);
        } else if (newStatus == ShippingStatus.DELIVERED) {
            shipping.markAsDelivered();
        } else {
            shipping.setStatus(newStatus);
        }
        
        return shippingRepository.update(shipping);
    }
}
```

---

## 3. PATRÓN DE SEGURIDAD (JWT)

### 3.1 Servicio JWT

```java
// ✅ CORRECT: infrastructure/security/JwtService.java
@Component
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    // Generar token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    // Token con claims adicionales
    public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(
                System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Extraer email
    public String extractEmail(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}

// ✅ CORRECT: infrastructure/security/BcryptPasswordService.java
@Component
public class BcryptPasswordService {
    
    private final PasswordEncoder passwordEncoder;
    
    public BcryptPasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### 3.2 Filtro JWT

```java
// ✅ CORRECT: adapters/in/security/JwtAuthFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Validar token
                if (jwtService.validateToken(token)) {
                    String email = jwtService.extractEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    // Crear autenticación
                    UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    // Establecer en contexto
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            // Log error pero continuar
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 3.3 Controller de Auth

```java
// ✅ CORRECT: adapters/in/security/AuthController.java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthUseCasePort authUseCasePort;
    private final AuthResponseMapper authResponseMapper;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        var authResponse = authUseCasePort.register(
            request.getEmail(),
            request.getPassword(),
            request.getFullName()
        );
        
        return ResponseEntity.ok(authResponseMapper.toResponse(authResponse));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request) {
        
        var authResponse = authUseCasePort.authenticate(
            request.getEmail(),
            request.getPassword()
        );
        
        return ResponseEntity.ok(authResponseMapper.toResponse(authResponse));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        authUseCasePort.logout(email);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 4. MANEJO DE EXCEPCIONES

### 4.1 Excepciones de Dominio

```java
// ✅ CORRECT: core/model/exceptions/EntityNotFoundException.java
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

// ✅ CORRECT: infrastructure/exceptions/user/UserNotFoundException.java
public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
    
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
```

### 4.2 Manejador Global

```java
// ✅ CORRECT: infrastructure/exceptions/GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "NOT_FOUND",
            ex.getMessage(),
            System.currentTimeMillis()
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        
        String message = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            message,
            System.currentTimeMillis()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }
    
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJakartaNotFound(
            jakarta.persistence.EntityNotFoundException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "NOT_FOUND",
            ex.getMessage(),
            System.currentTimeMillis()
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }
}

// ✅ Clase ErrorResponse para respuestas consistentes
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private long timestamp;
}
```

---

## 5. CONFIGURACIÓN DE SEGURIDAD

### 5.1 SecurityConfig

```java
// ✅ CORRECT: infrastructure/security/SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthFilter jwtAuthFilter;
    private final JpaUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) 
            throws Exception {
        
        http
            .csrf().disable()
            .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(
                    HttpStatus.UNAUTHORIZED))
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/products/**").permitAll()
                .antMatchers("/categories/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## CONCLUSIÓN

Estos patrones demuestran:
✅ Separación clara de responsabilidades
✅ Inyección de dependencias
✅ Mapeo entre capas (Domain ↔ DTO ↔ Entity)
✅ Manejo de excepciones consistente
✅ Seguridad implementada correctamente
✅ Validaciones en entrada
✅ Puertos bien definidos

