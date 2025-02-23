spring.application.name=puzzle-server
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/puzzle_game?createDatabaseIfNotExist=true&serverTimezone=UTC&sslMode=REQUIRED
//spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=puzzle_user
spring.datasource.password=12345
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
logging.level.root=DEBUG
logging.level.org.springframework=DEBUG
logging.level.com.puzzle=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
logging.level.org.hibernate.type=TRACE
