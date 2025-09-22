## Treading Platform (Spring Boot)

### Overview
Backend for a crypto trading platform inspired by Binance. Provides JWT/OAuth2 authentication, optional email-based 2FA, wallet and transaction management, order processing, deposits via Stripe/Razorpay, withdrawals, and CoinGecko market data endpoints.

### Key Features
- **Auth**: Signup, signin, JWT issuance, optional Google OAuth2, email OTP 2FA.
- **Coins/Market Data**: List/search coins, coin details, trending, market charts (CoinGecko).
- **Orders**: Create/process spot buy/sell orders against wallet balance; view orders.
- **Wallet**: Get balance, list transactions, deposit (demo and gateway flows), wallet-to-wallet transfer, pay orders from wallet.
- **Payments**: Create payment links via Stripe/Razorpay, finalize deposits, store `PaymentOrder`.
- **Withdrawals**: User requests, admin proceeds/accepts, wallet debit/refund.
- **Watchlist/Assets/Chatbot**: Scaffolding present in codebase for future use.

### Tech Stack
- Java 17, Spring Boot 3.2.x (Web, Security, Data JPA, Validation, Mail, OAuth2 Client)
- MySQL (Hibernate JPA; ddl-auto=update for dev)
- JWT via `io.jsonwebtoken` (jjwt 0.11.x)
- Stripe and Razorpay SDKs
- Jackson for JSON

### Project Structure
```
Crypto/
  pom.xml
  src/main/java/com/Avirat/
    config/            # JWT, constants, security helpers
    controller/        # REST controllers (auth, coins, orders, wallet, payments, withdrawals, ...)
    domain/            # enums (order type/status, wallet tx types, etc.)
    exception/         # error model + global exception handler
    model/             # JPA entities (User, Wallet, Order, PaymentOrder, ...)
    repository/        # Spring Data JPA repositories
    service/           # service interfaces + implementations
    utils/             # helpers (e.g., OTP)
    TreadingPlateformApplication.java
  src/main/resources/
    application.properties
```

### Setup
1) Preconditions
- JDK 17 installed (match runtime). Maven 3.9+. MySQL running.

2) Database
- Create a database, e.g. `trading_db`.

3) Configuration (env or `application.properties`)
Set the following (env vars preferred for secrets):
```
spring.datasource.url=jdbc:mysql://localhost:3306/trading_db
spring.datasource.username=<your_mysql_user>
spring.datasource.password=<your_mysql_password>
spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<gmail_user>
spring.mail.password=<gmail_app_password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT secret (define in JwtConstant.SECRET_KEY or env)
# e.g., export JWT_SECRET=<a-strong-256-bit-secret>

# Payment gateways
stripe.api.key=<stripe_secret_key>
razorpay.api.key=<razorpay_key>
razorpay.api.secret=<razorpay_secret>

# CoinGecko base URL (used by services)
coingecko.api.key=https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd

# Optional Google OAuth2
spring.security.oauth2.client.registration.google.client-id=<google_client_id>
spring.security.oauth2.client.registration.google.client-secret=<google_client_secret>
```

4) Build
```bash
mvn clean package -DskipTests
```

5) Run
```bash
mvn spring-boot:run
# or
java -jar target/treading-plateform-0.0.1-SNAPSHOT.jar
```

### Important Notes
- Align Java version: `pom.xml` sets java.version=17 but compiler source/target to 19; use a single version (prefer 17) or upgrade the runtime accordingly.
- Do not store real secrets in `application.properties` for production; use environment variables or a secrets manager.
- `ddl-auto=update` is convenient for dev; use migrations (Flyway/Liquibase) for prod.

### Core Endpoints (summary)
- Auth
  - POST `/auth/signup`
  - POST `/auth/signin`
  - POST `/auth/two-factor/otp/{otp}?id=<sessionId>`
  - GET `/auth/login/google` → redirects; callback `/auth/login/oauth2/code/google`

- Coins
  - GET `/coins?page=<n>`
  - GET `/coins/{coinId}/chart?days=<n>`
  - GET `/coins/search?q=<keyword>`
  - GET `/coins/top50`
  - GET `/coins/trading`
  - GET `/coins/details/{coinId}`

- Orders
  - POST `/api/orders/pay` (body: coinId, quantity, orderType)
  - GET `/api/orders/{orderId}`
  - GET `/api/orders?order_type=&asset_symbol=`

- Wallet
  - GET `/api/wallet`
  - GET `/api/wallet/transactions`
  - PUT `/api/wallet/deposit/amount/{amount}` (demo/direct)
  - PUT `/api/wallet/deposit?order_id=&payment_id=` (gateway finalize)
  - PUT `/api/wallet/{walletId}/transfer`
  - PUT `/api/wallet/order/{orderId}/pay`

- Payments
  - POST `/api/payment/{paymentMethod}/amount/{amount}` (paymentMethod: STRIPE|RAZORPAY)

- Withdrawals
  - POST `/api/withdrawal/{amount}`
  - PATCH `/api/admin/withdrawal/{id}/proceed/{accept}`
  - GET `/api/withdrawal`
  - GET `/api/admin/withdrawal`

### Enabling 2FA (email OTP)
1) Ensure mail credentials are configured.
2) When a user with 2FA enabled signs in, an OTP is emailed and a temporary session id is returned.
3) Verify OTP using `/auth/two-factor/otp/{otp}?id=<sessionId>` to receive the final JWT.

### Development Tips
- Add caching for CoinGecko endpoints and introduce rate limiting.
- Consider Stripe/Razorpay webhooks for asynchronous, idempotent deposit confirmation.
- Use transactions and optimistic/pessimistic locking around wallet balance updates.
- Represent monetary values with `BigDecimal` end-to-end.
- Gate admin routes with role checks in Spring Security config.

### Running Tests
```bash
mvn test
```

### License
This project is provided as-is for learning and demonstration purposes.


