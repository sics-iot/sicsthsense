# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table data_point (
  DTYPE                     varchar(16) not null,
  id                        bigint not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      bigint,
  constraint pk_data_point primary key (id))
;

create table file (
  uuid                      varchar(40) not null,
  path                      varchar(255) not null,
  owner_id                  bigint,
  type                      varchar(1) not null,
  ref_id                    bigint,
  constraint ck_file_type check (type in ('F','D')),
  constraint uq_file_1 unique (owner_id,path),
  constraint pk_file primary key (uuid))
;

create table user (
  id                        bigint not null,
  email                     varchar(256) not null,
  token                     varchar(255),
  user_name                 varchar(256) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  location                  varchar(255),
  creation_date             timestamp not null,
  last_login                timestamp,
  constraint uq_user_email unique (email),
  constraint uq_user_user_name unique (user_name),
  constraint pk_user primary key (id))
;

create table user_owned_resource (
  DTYPE                     varchar(16) not null,
  id                        bigint not null,
  user_id                   bigint,
  creation_date             timestamp,
  description               varchar(255),
  public_access             boolean,
  input_parser              varchar(255),
  type                      varchar(1),
  source_id                 bigint,
  history_size              bigint,
  last_updated              bigint,
  token                     varchar(255),
  constraint ck_user_owned_resource_type check (type in ('D','L','S')),
  constraint pk_user_owned_resource primary key (id))
;


create table user_owned_resource_user (
  user_owned_resource_id         bigint not null,
  user_id                        bigint not null,
  constraint pk_user_owned_resource_user primary key (user_owned_resource_id, user_id))
;
create sequence data_point_seq;

create sequence user_seq;

create sequence user_owned_resource_seq;

alter table data_point add constraint fk_data_point_stream_1 foreign key (stream_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_data_point_stream_1 on data_point (stream_id);
alter table file add constraint fk_file_owner_2 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_file_owner_2 on file (owner_id);
alter table user_owned_resource add constraint fk_user_owned_resource_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_owned_resource_user_3 on user_owned_resource (user_id);
alter table user_owned_resource add constraint fk_user_owned_resource_source_4 foreign key (source_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_user_owned_resource_source_4 on user_owned_resource (source_id);



alter table user_owned_resource_user add constraint fk_user_owned_resource_user_u_01 foreign key (user_owned_resource_id) references user_owned_resource (id) on delete restrict on update restrict;

alter table user_owned_resource_user add constraint fk_user_owned_resource_user_u_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists data_point;

drop table if exists file;

drop table if exists user;

drop table if exists user_owned_resource_user;

drop table if exists user_owned_resource;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists data_point_seq;

drop sequence if exists user_seq;

drop sequence if exists user_owned_resource_seq;

