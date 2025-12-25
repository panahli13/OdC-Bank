# OdC-Bank System

A comprehensive banking application featuring a robust Spring Boot backend and a responsive HTML/CSS/JS frontend.

## 🚀 Features

- **User Authentication**: Secure login and registration with email verification system.
- **Dashboard**: Real-time overview of account balance, active cards, and recent transactions.
- **Banking Services**:
  - **Cards**: Manage debit and credit cards securely.
  - **Loans**: Apply for and track loan status.
  - **Bill Payments**: Pay utility bills and other services directly.
  - **Money Transfers**: Seamless fund transfers between accounts.
  - **Currency Exchange**: View exchange rates and convert currencies.
  - **Savings Goals**: Set and track financial goals.

## 🛠 Technology Stack

- **Backend**: Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA.
- **Database**: PostgreSQL.
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla).
- **Build Tool**: Maven.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [PostgreSQL](https://www.postgresql.org/download/)

## ⚙️ Installation & Setup

### 1. Database Setup
1. Install and start PostgreSQL.
2. Create a new database named `odc_bank`.
3. (Optional) The default database credentials are configured as:
   - **Username**: `postgres`
   - **Password**: `postgres`
   If your local PostgreSQL setup uses different credentials, update them in:
   `src/main/resources/application.properties`

### 2. Backend Setup
1. Open the project folder in your terminal.
2. Run the application using the provided wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Alternatively, on Windows, you can simply run the `START-BACKEND.bat` script.*

The backend server will start on `http://localhost:8080`.

### 3. Frontend Setup
1. Navigate to the `frontend` directory in the project root.
2. Open `index.html` in any modern web browser to launch the application.
3. No build step is required for the frontend.

## 📧 Configuration

The application is configured to use Gmail for sending verification emails. Default settings are in `application.properties`. If you encounter email issues, check the SMTP configuration.

## 🧪 Testing

To run the backend tests:
```bash
./mvnw test
```
