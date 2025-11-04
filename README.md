# nextjj-ecommerce

🛍️ Full-stack ready **E-Commerce Backend** built with **Java Spring Boot**, featuring JWT authentication via cookies, PostgreSQL database, Cloudinary for image uploads, and Stripe for payment integration.

---

## Table of Contents

- [Description](#description)  
- [Tech Stack](#tech-stack)  
- [Features](#features)  
- [Getting Started](#getting-started)  
  - [Prerequisites](#prerequisites)  
  - [Setup](#setup)  

---

## Description  
This project is a RESTful E-Commerce backend built with Spring Boot.  
It provides a full API for managing users, products, orders, authentication, and payment checkout.  
The application uses JWT stored in HTTP-Only cookies for secure user sessions, PostgreSQL as the relational database, Cloudinary for image storage & delivery, and Stripe for payment processing.

Transactions are handled carefully with proper use of `@Transactional` and Pessimistic Locking (`FOR UPDATE`) to prevent concurrent stock issues.

---

## Tech Stack

| Component             | Technology                                 |
|------------------------|--------------------------------------------|
| Backend Framework      | Spring Boot (Java)                          |
| Database               | PostgreSQL                                  |
| ORM                    | Spring Data JPA / Hibernate                 |
| Authentication         | JWT (stored in HTTP-Only Cookies)           |
| File Storage           | Cloudinary API                              |
| Payment Gateway        | Stripe API                                  |
| Validation             | Jakarta Bean Validation (`@Valid`, `@NotNull`, etc.) |
| Transaction Control    | `@Transactional`, Pessimistic Locking       |
| Dev Setup              | Docker, Docker Compose                       |

---

## Features

- ✅ User Registration & Login (JWT + Cookies)  
- ✅ User Profile Management  
- ✅ Product Management (CRUD) + Image Upload to Cloudinary  
- ✅ Order Management: Create / Update / Delete Orders  
- ✅ Stock Checking with Pessimistic Lock to avoid oversell  
- ✅ Payment Integration (Stripe Checkout / Webhook)  
- ✅ Pagination & Filtering for Products and Orders  
- ✅ Global Exception Handling with Structured JSON Responses  
- ✅ CORS Setup for Frontend Integration (e.g., React/Vue)

---

## Getting Started

### Prerequisites

- Java 17+  
- Maven or Gradle  
- PostgreSQL server  
- Docker & Docker Compose (optional but recommended)  
- Cloudinary account  
- Stripe account  

### Setup
## ⚙️ 1. Clone the Repository
```bash
git clone https://github.com/Nextjingjing/nextjj-ecommerce.git
cd nextjj-ecommerce

## ⚙️ 2. Create Environment File
```bash
cp .env.dev.example .env

## ⚙️ 3. Develop With Docker
```bash
docker compose \
  -f docker-compose.dev.yml \
  --env-file .env.dev \
  up -d
