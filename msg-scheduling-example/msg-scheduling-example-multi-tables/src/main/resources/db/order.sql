create database if not exists `msg_schedule`;
use `msg_schedule`;
create table `order`
(
    id   varchar(64) not null
        primary key,
    name varchar(20) not null
);