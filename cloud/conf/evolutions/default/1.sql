# Tasks schema
 
# --- !Ups

create table user (
  email                     varchar(255) not null primary key,
  firstName                 varchar(255) not null,
  lastName                  varchar(255) not null,
);

create sequence thing_seq start with 1024;
create table thing (
  id                        integer not null default nextval('thing_seq'),
  url                       varchar(255) not null,
  uid                       varchar(255) not null,
  name                      varchar(255) not null,
  constraint uc_thing unique (url),
);

create sequence resource_seq start with 1024;
create table resource (
  id                        integer not null default nextval('resource_seq'),
  thingId                   integer not null,
  path                      varchar(255) not null,
  foreign key(thingId)      references thing(id) on delete cascade,
  constraint uc_resource unique (thingId, path)
);

create sequence monitor_seq start with 1024;
create table monitor (
  id                        integer not null default nextval('monitor_seq'),
  resourceId                integer not null,
  period                    integer,
  lastUpdate                integer,
  foreign key(resourceId)   references resource(id) on delete cascade,
);

create table sample (
  resourceId                integer not null,
  timestamp                 integer,
  value                     double,
  foreign key(resourceId)   references resource(id) on delete cascade,
);

# --- !Downs
 
drop table if exists user;
drop table if exists thing;
drop table if exists resource;
drop table if exists monitor;
drop table if exists sample;

drop sequence if exists thing_seq;
drop sequence if exists resource_seq;
drop sequence if exists monitor_seq;