--
-- Database: EvenOdd
--

--
-- Dumping data for table admin
--

INSERT INTO admin(adminID, username, password) VALUES
  (1, 'user1', 'password'),
  (2, 'user2', 'password'),
  (3, 'user3', 'password'),
  (4, 'user4', 'password');

INSERT INTO EvenOdd.userRoles (userId, role) VALUES (3, 'ROLE_USER');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (3, 'ROLE_ADMIN');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_USER');
INSERT INTO EvenOdd.userRoles (userId, role) VALUES (1, 'ROLE_MASTERADMIN');

