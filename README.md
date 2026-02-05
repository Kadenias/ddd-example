# BookLoad Testapplication

This application serves as a playground for Gatling performance tests. It uses SpringBoot and SpringSecurity using JWT for user authentication.

## Prerequisites
- Java 20
- Docker

## Build

```bash
docker compose up -d
```

## Debug

### OpenApi Documentation
http://localhost:8080/swagger-ui/index.html

### DB

Connect to DB
```shell
docker exec -it book_loan_postgres psql -U user -d db
```
Show all dbs
```shell
\dt 
```
Check books should contain 13 books
```shell
SELECT * FROM book;
```

## Troubleshooting

### Getting ./gradlew: Permission denied?

Set execution flag to file
```
chmod +x gradlew
```

