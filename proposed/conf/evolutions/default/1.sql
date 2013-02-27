# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actuators (
  id                        bigint not null,
  owner_id                  bigint,
  input_parser              varchar(255),
  constraint pk_actuators primary key (id))
;

create table data_point (
  DTYPE                     varchar(16) not null,
  id                        bigint not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      bigint,
  constraint pk_data_point primary key (id))
;

create table operators (
  id                        bigint not null,
  owner_id                  bigint,
  constraint pk_operators primary key (id))
;

create table sources (
  id                        bigint not null,
  owner_id                  bigint,
  polling_period            bigint,
  last_polled               bigint,
  polling_url               varchar(255),
  polling_authentication_key varchar(255),
  token                     varchar(255),
  constraint pk_sources primary key (id))
;

create table streams (
  id                        bigint not null,
  type                      varchar(1),
  owner_id                  bigint,
  source_id                 bigint,
  public_access             boolean,
  history_size              bigint,
  last_updated              bigint,
  token                     varchar(255),
  constraint ck_streams_type check (type in ('U','D','L','S')),
  constraint pk_streams primary key (id))
;

create table parsers (
  id                        bigint not null,
  source_id                 bigint,
  vfile_uuid                varchar(40),
  input_parser              varchar(255),
  input_type                varchar(255),
  constraint pk_parsers primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(256) not null,
  token                     varchar(255),
  user_name                 varchar(256) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  location                  varchar(255),
  creation_date             timestamp not null,
  last_login                timestamp,
  constraint uq_users_email unique (email),
  constraint uq_users_user_name unique (user_name),
  constraint pk_users primary key (id))
;

create table vfiles (
  uuid                      varchar(40) not null,
  path                      varchar(255) not null,
  owner_id                  bigint,
  type                      varchar(1) not null,
  linked_stream_id          bigint,
  constraint ck_vfiles_type check (type in ('F','D')),
  constraint uq_vfiles_1 unique (owner_id,path),
  constraint pk_vfiles primary key (uuid))
;


create table operators_streams (
  operators_id                   bigint not null,
  streams_id                     bigint not null,
  constraint pk_operators_streams primary key (operators_id, streams_id))
;
create sequence actuators_seq;

create sequence data_point_seq;

create sequence operators_seq;

create sequence sources_seq;

create sequence streams_seq;

create sequence parsers_seq;

create sequence users_seq;

alter table actuators add constraint fk_actuators_owner_1 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_actuators_owner_1 on actuators (owner_id);
alter table data_point add constraint fk_data_point_stream_2 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_stream_2 on data_point (stream_id);
alter table operators add constraint fk_operators_owner_3 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_operators_owner_3 on operators (owner_id);
alter table sources add constraint fk_sources_owner_4 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_sources_owner_4 on sources (owner_id);
alter table streams add constraint fk_streams_owner_5 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_streams_owner_5 on streams (owner_id);
alter table streams add constraint fk_streams_source_6 foreign key (source_id) references sources (id) on delete restrict on update restrict;
create index ix_streams_source_6 on streams (source_id);
alter table parsers add constraint fk_parsers_source_7 foreign key (source_id) references sources (id) on delete restrict on update restrict;
create index ix_parsers_source_7 on parsers (source_id);
alter table parsers add constraint fk_parsers_vfile_8 foreign key (vfile_uuid) references vfiles (uuid) on delete restrict on update restrict;
create index ix_parsers_vfile_8 on parsers (vfile_uuid);
alter table vfiles add constraint fk_vfiles_owner_9 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_vfiles_owner_9 on vfiles (owner_id);
alter table vfiles add constraint fk_vfiles_linkedStream_10 foreign key (linked_stream_id) references streams (id) on delete restrict on update restrict;
create index ix_vfiles_linkedStream_10 on vfiles (linked_stream_id);



alter table operators_streams add constraint fk_operators_streams_operator_01 foreign key (operators_id) references operators (id) on delete restrict on update restrict;

alter table operators_streams add constraint fk_operators_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists actuators;

drop table if exists data_point;

drop table if exists operators;

drop table if exists operators_streams;

drop table if exists sources;

drop table if exists streams;

drop table if exists parsers;

drop table if exists users;

drop table if exists vfiles;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists actuators_seq;

drop sequence if exists data_point_seq;

drop sequence if exists operators_seq;

drop sequence if exists sources_seq;

drop sequence if exists streams_seq;

drop sequence if exists parsers_seq;

drop sequence if exists users_seq;

