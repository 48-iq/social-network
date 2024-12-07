create database auth_service;
\c auth_service

create table roles (
    name text not null primary key
);

create table accounts (
    id text not null primary key,
    username text not null,
    password text not null,
    created_at timestamp not null,
    deleted boolean not null default false
);

create table accounts_roles (
    role_name text not null,
    account_id text not null,
    primary key (role_name, account_id)
);

insert into roles(name)
    values ('ROLE_ADMIN'), ('ROLE_USER')