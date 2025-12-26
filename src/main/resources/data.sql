-- Insert sample books only if they don't exist
INSERT INTO books (title, author, isbn, total_copies, available_copies, created_by, version)
SELECT * FROM (VALUES ('Clean Code', 'Robert C. Martin', '978-0132350884', 5, 5, 'admin@booklender.com', 0)) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '978-0132350884');

INSERT INTO books (title, author, isbn, total_copies, available_copies, created_by, version)
SELECT * FROM (VALUES ('Design Patterns', 'Gang of Four', '978-0201633610', 3, 3, 'admin@booklender.com', 0)) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '978-0201633610');

INSERT INTO books (title, author, isbn, total_copies, available_copies, created_by, version)
SELECT * FROM (VALUES ('The Pragmatic Programmer', 'Andrew Hunt', '978-0201616224', 4, 4, 'admin@booklender.com', 0)) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '978-0201616224');

INSERT INTO books (title, author, isbn, total_copies, available_copies, created_by, version)
SELECT * FROM (VALUES ('Refactoring', 'Martin Fowler', '978-0201485677', 2, 2, 'admin@booklender.com', 0)) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '978-0201485677');

INSERT INTO books (title, author, isbn, total_copies, available_copies, created_by, version)
SELECT * FROM (VALUES ('Domain-Driven Design', 'Eric Evans', '978-0321125217', 3, 3, 'admin@booklender.com', 0)) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '978-0321125217');

-- Insert sample members only if they don't exist
INSERT INTO members (name, email, created_by)
SELECT * FROM (VALUES ('Admin', 'admin@booklender.com', 'admin@booklender.com')) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'admin@booklender.com');

INSERT INTO members (name, email, created_by)
SELECT * FROM (VALUES ('Donald Trump', 'donald.trump@usa.com', 'donald.trump@usa.com')) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'donald.trump@usa.com');

INSERT INTO members (name, email, created_by)
SELECT * FROM (VALUES ('Barack Obama', 'barack.obama@usa.com', 'barack.obama@usa.com')) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'barack.obama@usa.com');

INSERT INTO members (name, email, created_by)
SELECT * FROM (VALUES ('John Rambo', 'john.rambo@usa.com', 'john.rambo@usa.com')) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'john.rambo@usa.com');
