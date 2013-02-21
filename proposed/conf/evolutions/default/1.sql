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
  linked_resource_id        bigint,
  constraint ck_file_type check (type in ('F','D')),
  constraint uq_file_1 unique (owner_id,path),
  constraint pk_file primary key (uuid))
;

create table polling_properties (
  id                        bigint not null,
  resource_id               bigint,
  polling_period            bigint,
  last_polled               bigint,
  polling_url               varchar(255),
  polling_authentication_key varchar(255),
  constraint pk_polling_properties primary key (id))
;

create table user_account (
  id                        bigint not null,
  email                     varchar(256) not null,
  token                     varchar(255),
  user_name                 varchar(256) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  location                  varchar(255),
  creation_date             timestamp not null,
  last_login                timestamp,
  constraint uq_user_account_email unique (email),
  constraint uq_user_account_user_name unique (user_name),
  constraint pk_user_account primary key (id))
;

create table user_owned_resource (
  DTYPE                     varchar(16) not null,
  id                        bigint not null,
  user_id                   bigint,
  creation_date             timestamp,
  description               varchar(255),
  public_access             boolean,
  polling_properties_id     bigint,
  input_parser              varchar(255),
  type                      varchar(1),
  source_id                 bigint,
  history_size              bigint,
  last_updated              bigint,
  token                     varchar(255),
  constraint ck_user_owned_resource_type check (type in ('U','D','L','S')),
  constraint pk_user_owned_resource primary key (id))
;


create table user_owned_resource_user_account (
  user_owned_resource_id         bigint not null,
  user_account_id                bigint not null,
  constraint pk_user_owned_resource_user_account primary key (user_owned_resource_id, user_account_id))
;
create sequence data_point_seq;

create sequence polling_properties_seq;

create sequence user_account_seq;

create sequence user_owned_resource_seq;

alter table data_point add constraint fk_data_point_stream_1 foreign key (stream_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_data_point_stream_1 on data_point (stream_id);
alter table file add constraint fk_file_owner_2 foreign key (owner_id) references user_account (id) on delete restrict on update restrict;
create index ix_file_owner_2 on file (owner_id);
alter table file add constraint fk_file_linkedResource_3 foreign key (linked_resource_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_file_linkedResource_3 on file (linked_resource_id);
alter table polling_properties add constraint fk_polling_properties_resource_4 foreign key (resource_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_polling_properties_resource_4 on polling_properties (resource_id);
alter table user_owned_resource add constraint fk_user_owned_resource_user_5 foreign key (user_id) references user_account (id) on delete restrict on update restrict;
create index ix_user_owned_resource_user_5 on user_owned_resource (user_id);
alter table user_owned_resource add constraint fk_user_owned_resource_polling_6 foreign key (polling_properties_id) references polling_properties (id) on delete restrict on update restrict;
create index ix_user_owned_resource_polling_6 on user_owned_resource (polling_properties_id);
alter table user_owned_resource add constraint fk_user_owned_resource_source_7 foreign key (source_id) references user_owned_resource (id) on delete restrict on update restrict;
create index ix_user_owned_resource_source_7 on user_owned_resource (source_id);



alter table user_owned_resource_user_account add constraint fk_user_owned_resource_user_a_01 foreign key (user_owned_resource_id) references user_owned_resource (id) on delete restrict on update restrict;

alter table user_owned_resource_user_account add constraint fk_user_owned_resource_user_a_02 foreign key (user_account_id) references user_account (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists data_point;

drop table if exists file;

drop table if exists polling_properties;

drop table if exists user_account;

drop table if exists user_owned_resource_user_account;

drop table if exists user_owned_resource;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists data_point_seq;

drop sequence if exists polling_properties_seq;

drop sequence if exists user_account_seq;

drop sequence if exists user_owned_resource_seq;

