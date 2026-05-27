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
Запуск приложения
bash
mvn spring-boot:run
API документация
После запуска: http://localhost:8080/swagger-ui.html

Тестовые пользователи
Роль	Логин	Пароль
ADMIN	admin	admin123
MANAGER	manager	manager123
USER	user	user123
API Endpoints
Аутентификация
Метод	Эндпоинт	Описание
POST	/api/auth/register	Регистрация нового пользователя
POST	/api/auth/login	Авторизация (JWT токен)
POST	/api/auth/logout	Выход из системы
GET	/api/auth/info	Информация о текущем пользователе
PUT	/api/auth/change_password	Смена пароля
Дворники (CRUD)
Метод	Эндпоинт	Описание	Доступ
GET	/api/wipers	Получить всех дворников	✅ ADMIN / MANAGER / USER
POST	/api/wipers	Создать дворника	✅ ADMIN / MANAGER
PUT	/api/wipers/{id}	Обновить дворника	✅ ADMIN / MANAGER
DELETE	/api/wipers/{id}	Удалить дворника	✅ ADMIN
POST	/api/wipers/{id}/repair	Починить дворника	✅ ADMIN / MANAGER
Отчёты и Excel
Метод	Эндпоинт	Описание	Доступ
GET	/api/wipers/export/excel	Экспорт всех дворников в Excel	✅ ADMIN / MANAGER
GET	/api/wipers/report	Сформировать отчёт	✅ ADMIN / MANAGER
GET	/api/wipers/report/pdf	Сформировать PDF отчёт	✅ ADMIN / MANAGER
POST	/api/wipers/import/excel	Импорт из Excel	✅ ADMIN
Особенности
🔐 JWT авторизация через cookies

👥 Ролевая система (ADMIN, MANAGER, USER)
![Uploading image.png…]()

🎲 Аномалии

📧 Telegram уведомления о событиях

📊 Экспорт в Excel и PDF

📝 Swagger документация
