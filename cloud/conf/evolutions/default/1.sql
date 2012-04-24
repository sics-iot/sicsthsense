# Tasks schema
 
# --- !Ups

create sequence thing_id_seq;
create table thing (
  id                        integer not null default nextval('thing_id_seq'),
  url                       varchar(255),
  description               varchar(255),
  label                     varchar(255),
  constraint uc_thing unique (url),
);

create sequence resource_id_seq;
create table resource (
  id                        integer not null default nextval('resource_id_seq'),
  thingId                   integer,
  path                      varchar(255),
);

create sequence monitor_id_seq;
create table monitor (
  id                        integer not null default nextval('monitor_id_seq'),
  resourceId                integer,
  period                    integer,
  lastUpdate                integer,
  constraint uc_monitor unique (resourceId),
);

create table sample (
  resourceId                integer,
  timestamp                 integer,
  value                     integer,
);

# --- !Downs
 
drop table if exists thing;
drop sequence thing_id_seq;

drop table if exists resource;
drop sequence resource_id_seq;

drop table if exists monitor;
drop sequence monitor_id_seq;
