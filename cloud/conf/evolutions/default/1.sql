# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table data_point (
  id                        bigint not null,
  resource_id               bigint,
  data                      float,
  timestamp                 bigint,
  constraint pk_data_point primary key (id))
;

create table end_point (
  id                        bigint not null,
  label                     varchar(255),
  url                       varchar(255),
  uid                       varchar(255),
  user_id                   bigint,
  constraint uq_end_point_1 unique (user_id,label),
  constraint pk_end_point primary key (id))
;

create table resource (
  id                        bigint not null,
  path                      varchar(255),
  end_point_id              bigint,
  user_id                   bigint,
  polling_period            bigint,
  last_polled               bigint,
  last_updated              bigint,
  constraint uq_resource_1 unique (end_point_id,path),
  constraint pk_resource primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  user_name                 varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  location                  varchar(255),
  constraint uq_user_1 unique (user_name),
  constraint uq_user_2 unique (email),
  constraint pk_user primary key (id))
;

create sequence data_point_seq;

create sequence end_point_seq;

create sequence resource_seq;

create sequence user_seq;

alter table data_point add constraint fk_data_point_resource_1 foreign key (resource_id) references resource (id) on delete restrict on update restrict;
create index ix_data_point_resource_1 on data_point (resource_id);
alter table end_point add constraint fk_end_point_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_end_point_user_2 on end_point (user_id);
alter table resource add constraint fk_resource_endPoint_3 foreign key (end_point_id) references end_point (id) on delete restrict on update restrict;
create index ix_resource_endPoint_3 on resource (end_point_id);
alter table resource add constraint fk_resource_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_resource_user_4 on resource (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists data_point;

drop table if exists end_point;

drop table if exists resource;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists data_point_seq;

drop sequence if exists end_point_seq;

drop sequence if exists resource_seq;

drop sequence if exists user_seq;

