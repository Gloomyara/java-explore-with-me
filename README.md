# Explore with me
Планировщик мероприятий, который позволяет пользователям создавать, искать и участвовать в различных событиях.

## Стек Технологий
- Java 11
- Maven 4
- Spring Boot 2
- Spring Data
- PostgreSQl
- MapStruct
- Lombok
- Docker

## API
1) Service URL: http://localhost:8080 

    [Спецификация Swagger](https://app.swaggerhub.com/apis/Gloomyara/explore-with_me_api/1.0)

2) Statistic URL: http://localhost:9090

    [Спецификация Swagger](https://app.swaggerhub.com/apis/Gloomyara/stat-service_api/v1.0)

## Сборка
1. Клонируйте репозиторий:
```Bash
git clone https://github.com/Gloomyara/java-explore-with-me.git
```
2. Перейдите в каталог проекта: 
```Bash
cd java-explore-with-me
```
3. Скомпилируйте исходные файлы:
```Bash
mvn clean package
```
4. Запустите проект:
```Bash
docker-compose up
```
## Статус проекта
Завершен.
