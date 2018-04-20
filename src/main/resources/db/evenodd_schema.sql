/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `EvenOdd`
--
drop database if exists EvenOdd;
create database EvenOdd;
use EvenOdd;
GRANT ALL ON * TO 'root'@'localhost';

CREATE TABLE `admin` (
  `adminID` int UNSIGNED NOT NULL auto_increment primary key,
  `username` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `admin`
  ADD UNIQUE KEY `user` (`username`);

CREATE TABLE `userRoles` (
  `id` INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `userId` int unsigned NOT NULL,
  `role` VARCHAR(25) NOT NULL DEFAULT 'ROLES_USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `userroles`
  ADD CONSTRAINT `FK_adminIDa` FOREIGN KEY (`userId`) REFERENCES admin(`adminID`);

CREATE TABLE `game` (
  `gameID` int UNSIGNED NOT NULL auto_increment primary key,
  `IDadmin1` int UNSIGNED,
  `IDadmin2` int UNSIGNED,
  `player1Choice` int,
  `player2Choice` int,
  `score` CHAR(3) DEFAULT '0:0',
  FOREIGN KEY (`IDadmin1`) REFERENCES admin(`adminID`),
  FOREIGN KEY (`IDadmin2`) REFERENCES admin(`adminID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;