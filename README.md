# Enviro365 Investments - Automated Withdrawal Notice System

Full-stack Spring Boot and web interface solution built for the eTalente Junior Developer Technical Evaluation.

## Project Overview
Enviro365 Investments automates client withdrawal notices from investment products (Savings & Retirement), enforces regulatory and business rules, provides real-time audit logs of balances before and after transactions, and supports CSV statement export.

## Business Rules Enforced
1. **Retirement Age Verification**: Withdrawals from `RETIREMENT` products are strictly restricted to investors aged > 65 years.
2. **Total Balance Constraint**: A withdrawal amount cannot exceed the product's current balance.
3. **90% Balance Limit**: A withdrawal amount cannot exceed 90% of the product's current balance.
4. **Transaction Integrity**: The product balance is updated atomically upon withdrawal notice generation.

## Technical Architecture & Rubric Coverage
- **Package**: `com.enviro.assessment.junior.twisisanikhosa`
- **Backend**: Spring Boot 3.2.5, Java 17+, Spring Data JPA, Jakarta Bean Validation.
- **Database**: In-memory H2 database with automatic seed data.
- **DTO Architecture**: Immutable Java records for decoupling entities from the REST API layer.
- **Global Error Handling**: `@RestControllerAdvice` mapping validation constraints and domain exceptions to standardized JSON error payloads.
- **Automated Testing**: JUnit 5 + Mockito unit tests validating business rule enforcement and boundary conditions.
- **Frontend**: Responsive Single-Page Application (HTML5, Tailwind CSS, JavaScript) served directly from `src/main/resources/static`.

---

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.8+ (or use the Maven wrapper)
- A modern web browser

### Running the Application
1. Clone the repository:
   ```bash
   git clone [https://github.com/yourusername/enviro365-withdrawal-system.git](https://github.com/yourusername/enviro365-withdrawal-system.git)
   cd enviro365-withdrawal-system