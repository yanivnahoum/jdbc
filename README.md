# JDBC Workshop

A hands-on introduction to JDBC using Java 25, Maven, PostgreSQL 17, and
HikariCP. Each chapter has a small reference example followed by a focused
exercise.

## Prerequisites

- JDK 25
- Maven
- Docker with Docker Compose
- An IDE or editor with Java support

Verify the tools before the workshop:

```shell
java -version
./mvnw -version
docker version
```

Both Java commands must report JDK 25. Maven can use a different JDK from the
one selected in your shell, so check both outputs.

## Setup

Start PostgreSQL and compile the project:

```shell
docker compose up -d
./mvnw -q compile
```

Run the first connection check:

```shell
./mvnw -q exec:java \
  -Dexec.mainClass=workshop.examples.ex01.ConnectExample
```

You are ready when it prints `Connected to PostgreSQL 17...`.

The examples use these defaults:

| Setting  | Default                                     | Environment variable |
|----------|---------------------------------------------|----------------------|
| JDBC URL | `jdbc:postgresql://localhost:5432/workshop` | `JDBC_URL`           |
| Username | `workshop`                                  | `JDBC_USER`          |
| Password | `workshop`                                  | `JDBC_PASSWORD`      |

### Resetting the workshop database

The SQL files run only when PostgreSQL creates a fresh data directory. To
restore the original data:

```shell
docker compose down --volumes
docker compose up -d
```

This command deletes the workshop database volume. Do not use it for a
database containing data you need to keep.

## Project map

- [`compose.yml`](compose.yml) starts PostgreSQL.
- [`sql/01_schema.sql`](sql/01_schema.sql) defines the tables.
- [`sql/02_seed.sql`](sql/02_seed.sql) creates the starting data.
- [`Db.java`](src/main/java/workshop/Db.java) holds shared connection settings.
- [`examples/`](src/main/java/workshop/examples) contains completed reference
  examples.
- [`exercises/`](src/main/java/workshop/exercises) contains the files you edit.

Run any class with:

```shell
./mvnw -q compile exec:java -Dexec.mainClass=fully.qualified.ClassName
```

## Block A: Reading data

### 1. Connect

**Learn:** JDBC URLs, `DriverManager`, `Connection`, database metadata, and
try-with-resources.

- [Example](src/main/java/workshop/examples/ex01/ConnectExample.java)
- [Exercise](src/main/java/workshop/exercises/ex01/ConnectExercise.java)

Run the exercise:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex01.ConnectExercise
```

Implement `openConnection()` using `DriverManager` and the values in
`workshop.Db`. Keep the returned connection inside the existing
try-with-resources block.

**Done when:** the program prints the PostgreSQL product name and version.

### 2. Query customers

**Learn:** `PreparedStatement`, bind parameters, `ResultSet` cursors, typed
getters, Java records, SQL `NULL`, and `LocalDate`.

- [Example](src/main/java/workshop/examples/ex02/QueryCustomersExample.java)
- [Exercise](src/main/java/workshop/exercises/ex02/QueryCustomersExercise.java)

Use this query as a Java text block:

```sql
SELECT id, email, full_name, birth_date, loyalty_pts
FROM customers
WHERE loyalty_pts IS NULL
   OR loyalty_pts > ?
ORDER BY id
```

Implement `findCustomers()`:

1. Create a `PreparedStatement`.
2. Bind `minimumPoints`. JDBC parameter indexes start at `1`.
3. Advance the cursor with `while (resultSet.next())`.
4. Map columns by name to `Customer`.
5. Read `birth_date` as `LocalDate`.
6. Read `loyalty_pts` as `Integer`, preserving the difference between `NULL`
   and `0`.

Run it:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex02.QueryCustomersExercise
```

**Done when:** the output contains both a customer with `loyaltyPts=null` and
one with a numeric value.

### 3. Prevent SQL injection

**Learn:** why string concatenation turns data into SQL syntax and why bind
parameters are the default for values.

- [Example](src/main/java/workshop/examples/ex03/SqlInjectionExample.java)
- [Exercise](src/main/java/workshop/exercises/ex03/SafeCustomerSearchExercise.java)

Run the reference example with an apostrophe in the email:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.examples.ex03.SqlInjectionExample \
  -Dexec.args=apostrophe
```

Then run its local demonstration payload:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.examples.ex03.SqlInjectionExample \
  -Dexec.args=attack
```

The vulnerable method is intentionally unsafe and exists only to demonstrate
the problem against the local workshop database. Do not copy it into
application code.

Implement `findCustomerNames()` with this shape:

```sql
SELECT full_name
FROM customers
WHERE email = ?
  AND loyalty_pts > ?
```

Bind both values. Do not escape, strip, or concatenate input.

**Done when:** `o'brien@example.com` works normally and the attack value
returns no customers from the exercise.

## Block B: Writing data

### 4. Insert, update, and delete

**Learn:** `executeUpdate()`, affected-row counts, generated keys, and binding
SQL `NULL`.

- [Example](src/main/java/workshop/examples/ex04/WriteCustomersExample.java)
- [Exercise](src/main/java/workshop/exercises/ex04/WriteCustomersExercise.java)

Implement the three methods in the exercise:

1. Insert Ada and request `Statement.RETURN_GENERATED_KEYS`.
2. Bind the nullable points value with
   `setObject(index, null, Types.INTEGER)`.
3. Verify exactly one row was inserted and read the generated key.
4. Update points by email and return the affected-row count.
5. Delete a customer by email and return the affected-row count.

Run it once after resetting the database:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex04.WriteCustomersExercise
```

Run it a second time to observe the unique-email constraint failure.

**Done when:** the first run prints a generated ID, the missing-customer update
prints `0`, and the second insert reports a duplicate key.

### 5. Transfer money in a transaction

**Learn:** auto-commit, commit, rollback, connection-scoped transactions,
affected-row validation, and `BigDecimal`.

- [Example](src/main/java/workshop/examples/ex05/TransferExample.java)
- [Exercise](src/main/java/workshop/exercises/ex05/TransferExercise.java)

First, temporarily implement the transfer as `credit()` followed by `debit()`
with auto-commit left on. Reset the database, then run:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex05.TransferExercise \
  -Dexec.args=500.00
```

The credit succeeds and commits before the debit violates the non-negative
balance constraint. Inspect the account balances and observe the partial
update.

Reset the database, then replace the naive implementation:

1. Reject a null or non-positive amount.
2. Reject transfers to the same account.
3. Save the original auto-commit state.
4. Disable auto-commit.
5. Credit and debit using the same `Connection`.
6. Commit on success.
7. Roll back on `SQLException` and preserve a rollback failure as a suppressed
   exception.
8. Restore connection state only after the transaction ended successfully.

Run the failing transfer again, then run a valid one:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex05.TransferExercise \
  -Dexec.args=500.00

./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex05.TransferExercise \
  -Dexec.args=25.00
```

**Done when:** the failed transfer changes neither account and the valid
transfer changes both.

## Block C: Production habits

### 6. Batch inserts

**Learn:** `addBatch()`, `executeBatch()`, periodic flushing, explicit
transactions, and pgJDBC's optional `reWriteBatchedInserts` optimization.

- [Example](src/main/java/workshop/examples/ex06/BatchInsertExample.java)
- [Exercise](src/main/java/workshop/exercises/ex06/BatchInsertExercise.java)

Implement `insertBatch()`:

1. Prepare one parameterized `INSERT`.
2. Bind a unique email and name for each row.
3. Call `addBatch()` for every parameter set.
4. Call `executeBatch()` every 1,000 rows.
5. Flush any remainder after the loop.

Run the reference comparison and then your exercise:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.examples.ex06.BatchInsertExample

./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex06.BatchInsertExercise
```

The printed times are local observations, not rigorous benchmarks. JVM
warm-up, Docker, indexes, logging, and machine load affect the result.

**Done when:** 10,000 rows are inserted in bounded batches and you can explain
why batching and a transaction are normally used together.

### 7. Use a connection pool

**Learn:** `DataSource`, HikariCP, borrowing and returning connections, and why
pool size is bounded.

- [Example](src/main/java/workshop/examples/ex07/ConnectionPoolExample.java)
- [Exercise](src/main/java/workshop/exercises/ex07/ConnectionPoolExercise.java)

Implement `createDataSource()`:

1. Create a `HikariConfig`.
2. Set the URL, username, and password from `workshop.Db`.
3. Set the maximum pool size to `10`.
4. Return a `HikariDataSource`.

Run it:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex07.ConnectionPoolExercise
```

Then run the reference timing comparison:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.examples.ex07.ConnectionPoolExample
```

Pool construction is outside the timed section. Treat the numbers as an
illustration rather than a guaranteed ratio.

**Done when:** the exercise prints `Database returned 1`, and you can explain
why `connection.close()` returns a pooled connection instead of closing the
physical database connection.

### 8. Repair a DAO and handle SQL errors

**Learn:** resource ownership, `DataSource`, stable column mappings, nullable
values, SQL state codes, and avoiding message parsing.

- [Reference DAO](src/main/java/workshop/examples/ex08/CustomerDao.java)
- [Reference runner](src/main/java/workshop/examples/ex08/DaoExample.java)
- [Exercise](src/main/java/workshop/exercises/ex08/CustomerDaoExercise.java)
- [Exercise runner](src/main/java/workshop/exercises/ex08/DaoExerciseRunner.java)

Find and repair these defects in `CustomerDaoExercise`:

1. Resources leak when an exception occurs.
2. `Statement` and `ResultSet` are not explicitly closed.
3. `SELECT *` is coupled to positional getters.
4. A nullable integer is read with `getInt()`, turning `NULL` into `0`.
5. The DAO opens a new physical connection through `DriverManager` instead of
   depending on a `DataSource`.

Refactor the DAO to accept a `DataSource`, use nested try-with-resources, select
named columns, and map columns by name. Compare your result with the reference
DAO only after attempting the exercise.

Run your DAO with:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.exercises.ex08.DaoExerciseRunner
```

Run the reference error-handling demonstration:

```shell
./mvnw -q compile exec:java \
  -Dexec.mainClass=workshop.examples.ex08.DaoExample
```

It recognizes PostgreSQL SQL state `23505` as a unique-constraint violation.
Branch on SQL state or a reliably supported JDBC exception subtype; never
parse `SQLException.getMessage()`.

**Done when:** the rewritten DAO returns all customers through a `DataSource`
without leaking JDBC resources or losing `NULL` values.

## What comes after JDBC?

Most imperative Java database libraries build on the concepts used here:

| Layer                                | What it provides                                       |
|--------------------------------------|--------------------------------------------------------|
| JDBC driver                          | PostgreSQL wire-protocol implementation                |
| JDBC API                             | Connections, statements, result sets, and transactions |
| Spring `JdbcClient` / `JdbcTemplate` | Less boilerplate while retaining SQL                   |
| jOOQ                                 | Generated, type-checked SQL DSL                        |
| JPA / Hibernate                      | Object-relational mapping and generated SQL            |

Reactive database stacks such as R2DBC use a different programming model.
Regardless of the abstraction, SQL behavior, transactions, constraints,
resource limits, and query performance still matter.
