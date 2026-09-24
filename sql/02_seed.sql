INSERT INTO customers (id, email, full_name, birth_date, loyalty_pts)
VALUES (1, 'alice@example.com', 'Alice Johnson', DATE '1998-04-12', 250),
       (2, 'bob@example.com', 'Bob Smith', DATE '2001-09-03', 0),
       (3, 'carol@example.com', 'Carol Chen', DATE '1996-01-20', NULL),
       (4, 'dan@example.com', 'Dan Brown', NULL, 120),
       (5, 'eve@example.com', 'Eve Wilson', DATE '2000-11-08', NULL),
       (6, 'o''brien@example.com', 'Pat O''Brien', DATE '1999-06-17', 75),
       (7, 'frank@example.com', 'Frank Garcia', DATE '1997-03-28', 500),
       (8, 'delete.me@example.com', 'Delete Me', NULL, 10);

ALTER TABLE customers
    ALTER COLUMN id RESTART WITH 9;

INSERT INTO accounts (id, owner_id, balance)
VALUES (1, 1, 100.00),
       (2, 2, 50.00);

ALTER TABLE accounts
    ALTER COLUMN id RESTART WITH 3;
