# Система управления дворниками автобусов

## Описание

Система учёта, мониторинга и анализа состояния дворников автобусного парка. Позволяет отслеживать аномалии, формировать отчёты и получать уведомления в Telegram.

## Технологии

- Java 17
- Spring Boot 3.4.4
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI
- Apache POI (Excel)
- Telegram Bot API
- Maven

## Запуск проекта

### Требования

- Java 17
- PostgreSQL 17
- Maven

### Настройка базы данных

```sql
CREATE DATABASE sandbox;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE sandbox TO postgres;
