CREATE TABLE IF NOT EXISTS menu (
  id SERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description TEXT,
  price NUMERIC(10,2) NOT NULL,
  image TEXT
);

CREATE TABLE IF NOT EXISTS reservations (
  id SERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(200) NOT NULL,
  date DATE NOT NULL,
  time TIME NOT NULL,
  guests INT NOT NULL,
  confirmation_code VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  checked_in BOOLEAN NOT NULL DEFAULT FALSE,
  check_in_time TIMESTAMP NULL,
  table_number INT NULL
);

-- Orders placed either during reservation (preorder) or in-restaurant
CREATE TABLE IF NOT EXISTS orders (
  id SERIAL PRIMARY KEY,
  reservation_id INT NULL,
  table_number INT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'NEW',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  paid_at TIMESTAMP NULL,
  order_number INT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  id SERIAL PRIMARY KEY,
  order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  name VARCHAR(200) NOT NULL,
  quantity INT NOT NULL,
  price NUMERIC(10,2) NOT NULL
);

-- Users for authentication/authorization
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  role VARCHAR(30) NOT NULL, -- ADMIN or KITCHEN
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
