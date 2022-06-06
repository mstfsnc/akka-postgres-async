CREATE TABLE IF NOT EXISTS users (
    id serial constraint users_pk primary key,
    email varchar,
    age int default 0
);