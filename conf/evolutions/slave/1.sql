-- Created by Ebean DDL
-- To stop Ebean DDL generation, remove this comment (both lines) and start using Evolutions

-- !Ups

-- apply changes
create table user (
  id                            bigint generated by default as identity not null,
  name                          varchar,
  email                         varchar,
  password                      varchar,
  constraint pk_user primary key (id)
);


-- !Downs

-- drop all
drop table if exists user cascade;

