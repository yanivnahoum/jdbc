# JDBC Workshop — Syllabus

3 hours · Java 25 · Maven · PostgreSQL 17 (Docker)

## Before the workshop

Participants complete a short setup check before the three-hour session:

* `java -version` and `./mvnw -version` both report JDK 25
* `docker compose up -d` starts PostgreSQL
* `./mvnw -q compile` succeeds

## Block A — Reading data (50 min)

* Connect — JDBC URLs, DriverManager, try-with-resources
* Query & ResultSet — PreparedStatement, cursors, typed getters, NULL handling
* SQL injection — why concatenation breaks, how bind parameters fix it

Break — 10 min

## Block B — Writing data (50 min)

* Insert / update / delete — executeUpdate, generated keys
* Transactions — autocommit, commit, rollback, isolation levels
* Savepoints — optional stretch topic

Break — 10 min

## Block C — Doing it properly (45 min)

* Batching — addBatch / executeBatch, measured speedup
* Connection pooling — HikariCP as a DataSource
* DAO design & SQLException — resource leaks, error codes
* What sits on top — JPA/Hibernate, jOOQ, Spring JdbcClient

Closing questions — 5 min

The scheduled material uses 170 minutes, leaving 10 minutes of flex for questions and exercise overruns. Protect the
transaction exercise; trim savepoints and the final ecosystem overview first.
