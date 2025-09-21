# DineDigital

A modern web-based restaurant management system built with Spring Boot, enabling seamless food ordering, reservation management, kitchen operations, and billing.

## Features

- **Customer Ordering**: Place orders at tables or pre-order during reservations
- **Reservation System**: Book tables with confirmation codes and optional pre-orders
- **Kitchen Dashboard**: Real-time order tracking and status updates
- **Billing Management**: Mark orders as paid and generate receipts
- **Admin Panel**: Manage menu, users, reservations, and QR codes
- **Daily Order Numbering**: Automatic reset of order IDs each day
- **PDF Receipts**: Downloadable order confirmations
- **Authentication**: JWT-based login for admin and kitchen roles

## Tech Stack

- **Backend**: Spring Boot 3.3.3, Spring MVC, Spring Security
- **Database**: H2 (development), PostgreSQL (production)
- **Frontend**: JSP, Bootstrap 5, JavaScript
- **Build Tool**: Maven
- **Other**: JDBC, PDFBox for receipts

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Git

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Scemworks/dinedigital.git
   cd dinedigital
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

## Running the Application

### Development Mode (H2 Database)
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Production Mode (PostgreSQL)
1. Update `application-prod.properties` with your PostgreSQL connection details
2. Run with production profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

## Usage

### Customer Flow
1. **Home Page**: View menu and make reservations
2. **Order Food**: Select items and place orders at tables
3. **Reservations**: Book tables with optional pre-orders
4. **Confirmation**: Receive order number and download PDF receipt

### Staff Flow
1. **Login**: Access admin or kitchen dashboards
2. **Kitchen**: View pending orders and mark as served
3. **Billing**: List orders and mark as paid
4. **Admin**: Manage menu, users, and reservations

### Default Credentials
- **Admin**: username: `admin`, password: `admin123`
- **Kitchen**: username: `kitchen`, password: `kitchen123`

## Project Structure

```
src/
├── main/
│   ├── java/com/dinedigital/
│   │   ├── controller/     # Web controllers
│   │   ├── dao/           # Data access objects
│   │   ├── model/         # Entity models
│   │   ├── security/      # Authentication & authorization
│   │   ├── service/       # Business logic
│   │   └── util/          # Utilities
│   ├── resources/         # Application properties & SQL scripts
│   └── webapp/            # JSP views & static assets
└── test/                  # Unit tests
```

## Database Schema

The application uses the following main tables:
- `menu`: Food items with prices
- `reservations`: Table bookings
- `orders`: Customer orders (table or pre-orders)
- `order_items`: Individual order items
- `users`: Staff accounts

Schema is defined in `src/main/resources/schema.sql`

## API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /menu` - View menu
- `GET /reservation` - Make reservation
- `POST /reservation/confirm` - Confirm reservation
- `GET /order` - Place table order
- `POST /orders/place` - Submit order

### Staff Endpoints (Authenticated)
- `GET /admin` - Admin dashboard
- `GET /kitchen` - Kitchen dashboard
- `GET /billing` - Billing page
- `POST /kitchen/complete` - Mark order served
- `POST /admin/billing/paid` - Mark order paid

## Development

### Running Tests
```bash
mvn test
```

### Building WAR
```bash
mvn clean package
```

### H2 Console
Access the H2 database console at `http://localhost:8080/h2-console` in dev mode.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built as a college mini-project
- Uses Bootstrap for responsive design
- PDF generation with Apache PDFBox