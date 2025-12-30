# 🔒 Security Implementation

Security is a critical component of the architecture, implemented using **Spring Security 6** and **JWT (JSON Web Tokens)** for stateless authentication.

## 🛡️ Core Concepts

### 1. Stateless Authentication
Instead of server-side sessions, we use signed tokens.
- **Login**: User sends credentials → Server validates → Returns JWT.
- **Subsequent Requests**: Client sends `Authorization: Bearer <token>`.
- **Benefit**: Horizontal scalability. Any instance of the backend can validate the token without checking a central session database.

### 2. Request Flow
Standard Spring Security Filter Chain:

1. **JwtAuthFilter**:
   - Intercepts every request.
   - Extracts the "Bearer" token.
   - Parses claims (User ID, Roles).
   - Sets the `Authentication` context in Spring Security.

2. **Authorization**:
   - `SecurityConfig` defines rules:
     - `/auth/**` → Permitted to all (Public).
     - `/admin/**` → Requires `ROLE_ADMIN`.
     - All others → Require `isAuthenticated()`.

### 3. Password Storage
- Passwords are **never** stored in plain text.
- We use `BCryptPasswordEncoder` with a strength of 10.

## ⛔ CORS & CSRF

- **CSRF (Cross-Site Request Forgery)**: Disabled.
  - *Reason*: Since we use JWT (headers) and not Cookies for auth, CSRF attacks are not applicable.
  - *Benefit*: Simplifies frontend integration.

- **CORS (Cross-Origin Resource Sharing)**: Configured to allow strictly trusted origins.
  - Allowed: `http://localhost:3000` (Frontend dev).
  - Methods: `GET`, `POST`, `PUT`, `DELETE`.

## 🧪 Security Testing
Integration tests ensure that the security firewall holds up.
- We verify that accessing `/orders` without a token yields a `403 Forbidden` or `401 Unauthorized`.
