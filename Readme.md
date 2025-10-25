create database users

CREATE USER 'sharehubuser'@'localhost' IDENTIFIED BY 'sharehubuser';
GRANT ALL PRIVILEGES ON sharehub.* TO 'sharehubuser'@'localhost';

