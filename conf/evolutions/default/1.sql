# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table thing (
  id                        varchar(255) not null,
  url                       varchar(255),
  constraint pk_thing primary key (id))
;

create sequence thing_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists thing;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists thing_seq;

