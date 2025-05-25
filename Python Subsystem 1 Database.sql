CREATE SCHEMA document_db;

CREATE TABLE documents(
doc_id INT NOT NULL primary key auto_increment,
author VARCHAR(255),
title VARCHAR(255),
upload_date datetime,
file_name VARCHAR(255)
);

CREATE TABLE contents(
id INT primary KEY auto_increment,
content VARCHAR(255),
doc_id INT,
FOREIGN KEY(doc_id) REFERENCES documents(doc_id)
);