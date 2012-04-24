# Tasks schema
 
# --- !Ups

create sequence thing_id_seq;
create table thing (
  id integer                not null default nextval('thing_id_seq'),
  url                       varchar(255),
  description               varchar(255),
  label                     varchar(255),
  resources                 varchar(255),
);
 
# --- !Downs
 
drop table if exists thing;
drop sequence thing_id_seq;
