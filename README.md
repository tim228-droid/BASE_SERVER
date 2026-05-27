# Система управления дворниками автобусов

## Описание

Система учёта, мониторинга и анализа состояния дворников машин. Позволяет отслеживать аномалии, формировать отчёты и получать уведомления в Telegram.

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

## Настройка конфигурации
```
jwt.secret=66546A555E5A7234753778214123222A472D4B614E645262356B587032733576
telegram.bot.token=есть
telegram.chat.id=8810328516
telegram.enabled=true
upload.path=./uploads/
```

## Запуск приложения
```
mvn spring-boot:run 
```
## API документация
```
После запуска: http://localhost:8080/swagger-ui.html
```
## Тестовые пользователи

| Роль     | Логин     | Пароль     |
|----------|-----------|------------|
| ADMIN    | admin1    | admin123   |
| MANAGER  | manager1  | manager123 |
| USER     | user1     | user123    |

## 📡 API Endpoints

| Метод | Эндпоинт | Описание |
|:-----:|----------|----------|
| POST | `/api/auth/register` | Регистрация нового пользователя |
| POST | `/api/auth/login` | Авторизация (получение JWT токена) |
| POST | `/api/wipers` | Создать нового дворника |
| GET | `/api/wipers` | Получить всех дворников |
| GET | `/api/wipers/{id}` | Получить дворника по ID |
| PUT | `/api/wipers/{id}` | Обновить данные дворника |
| DELETE | `/api/wipers/{id}` | Удалить дворника |
| POST | `/api/wipers/{id}/repair` | Починить дворника (исправить аномалию) |
| GET | `/api/wipers/search` | Поиск дворников по бренду |
| GET | `/api/wipers/export/excel` | Экспорт всех дворников в Excel |
| GET | `/api/wipers/report` | Сформировать отчёт в Excel |
| GET | `/api/wipers/report/pdf` | Сформировать отчёт в PDF |
| POST | `/api/wipers/import/excel` | Импорт дворников из Excel |

## Схема БД
https://github.com/tim228-droid/BASE_SERVER/blob/98fdb5cbb78382ebe867774c5e1285603261e344/BD.png
