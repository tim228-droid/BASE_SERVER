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
```

Настройка конфигурации
```
jwt.secret=66546A555E5A7234753778214123222A472D4B614E645262356B587032733576
telegram.bot.token=есть
telegram.chat.id=8810328516
telegram.enabled=true
upload.path=./uploads/
```

Запуск приложения
```
mvn spring-boot:run 
```
API документация
```
После запуска: http://localhost:8080/swagger-ui.html
```
Тестовые пользователи

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| POST | /api/auth/login | Авторизация (получение JWT токена) |
| POST | /api/sensors | Отправить данные датчика |
| GET | /api/sensors/latest | Получить последние показания |
| GET | /api/sensors/history | Получить историю показаний |
| GET | /api/sensors/alerts | Получить список аномалий |

