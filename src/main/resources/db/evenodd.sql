--
-- Database: EvenOdd
--

--
-- Dumping data for table admin
--

INSERT INTO admin(adminID, username, password) VALUES
  (1, 'user1', '$2a$11$kJ1bNyO1BfKjxR79tGkVWOTt8lbakwDGjA6LWLvHldrbOHX91MqkC'),
  (2, 'user2', '$2a$11$kJ1bNyO1BfKjxR79tGkVWOTt8lbakwDGjA6LWLvHldrbOHX91MqkC'),
  (3, 'user3', '$2a$11$kJ1bNyO1BfKjxR79tGkVWOTt8lbakwDGjA6LWLvHldrbOHX91MqkC'),
  (4, 'user4', '$2a$11$kJ1bNyO1BfKjxR79tGkVWOTt8lbakwDGjA6LWLvHldrbOHX91MqkC');

INSERT INTO EvenOdd.userRoles (userId, role) VALUES (3, 'ROLE_USER');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (3, 'ROLE_ADMIN');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_USER');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_MASTERADMIN');

