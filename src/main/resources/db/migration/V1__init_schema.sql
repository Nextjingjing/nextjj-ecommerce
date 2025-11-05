-- V1__init_schema.sql (for PostgreSQL)
-- Flyway Migration: Initial Schema for NextJingJing API

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    fname VARCHAR(255),
    lname VARCHAR(255),
    address VARCHAR(255),
    tel VARCHAR(20),
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    price DOUBLE PRECISION NOT NULL,
    stock INT NOT NULL,
    category_id BIGINT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_date TIMESTAMP NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    method VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    client_secret VARCHAR(255),
    stripe_payment_intent_id VARCHAR(100),
    order_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_products (
    id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    price_per_unit DOUBLE PRECISION NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_orderproduct_order FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_orderproduct_product FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
