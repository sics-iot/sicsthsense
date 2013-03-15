# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actuators (
  id                        bigint not null,
  owner_id                  bigint,
  input_parser              varchar(255),
  constraint pk_actuators primary key (id))
;

create table data_point_double (
  id                        bigint not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      double,
  constraint uq_data_point_double_1 unique (stream_id,timestamp),
  constraint pk_data_point_double primary key (id))
;

create table data_point_string (
  id                        bigint not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      varchar(160),
  constraint uq_data_point_string_1 unique (stream_id,timestamp),
  constraint pk_data_point_string primary key (id))
;

create table functions (
  id                        bigint not null,
  owner_id                  bigint,
  constraint pk_functions primary key (id))
;

create table operators (
  id                        bigint not null,
  constraint pk_operators primary key (id))
;

create table sources (
  id                        bigint not null,
  owner_id                  bigint,
  label                     varchar(255),
  polling_period            bigint,
  last_polled               bigint,
  polling_url               varchar(255),
  polling_authentication_key varchar(255),
  key                       varchar(255),
  constraint uq_sources_1 unique (owner_id,label),
  constraint pk_sources primary key (id))
;

create table streams (
  id                        bigint not null,
  type                      varchar(1),
  public_access             boolean,
  history_size              bigint,
  last_updated              bigint,
  key                       varchar(255),
  owner_id                  bigint,
  source_id                 bigint,
  constraint ck_streams_type check (type in ('U','D','S')),
  constraint pk_streams primary key (id))
;

create table parsers (
  id                        bigint not null,
  source_id                 bigint,
  stream_id                 bigint,
  input_parser              varchar(255),
  input_type                varchar(255),
  constraint pk_parsers primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(256) not null,
  user_name                 varchar(256) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  location                  varchar(255),
  creation_date             timestamp not null,
  last_login                timestamp,
  token                     varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_user_name unique (user_name),
  constraint pk_users primary key (id))
;

create table vfiles (
  id                        bigint not null,
  path                      varchar(255) not null,
  owner_id                  bigint,
  type                      varchar(1) not null,
  linked_stream_id          bigint,
  constraint ck_vfiles_type check (type in ('F','D')),
  constraint uq_vfiles_1 unique (owner_id,path),
  constraint pk_vfiles primary key (id))
;


create table functions_streams (
  functions_id                   bigint not null,
  streams_id                     bigint not null,
  constraint pk_functions_streams primary key (functions_id, streams_id))
;
create sequence actuators_seq;

create sequence data_point_double_seq;

create sequence data_point_string_seq;

create sequence functions_seq;

create sequence operators_seq;

create sequence sources_seq;

create sequence streams_seq;

create sequence parsers_seq;

create sequence users_seq;

create sequence vfiles_seq;

alter table actuators add constraint fk_actuators_owner_1 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_actuators_owner_1 on actuators (owner_id);
alter table data_point_double add constraint fk_data_point_double_stream_2 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_double_stream_2 on data_point_double (stream_id);
alter table data_point_string add constraint fk_data_point_string_stream_3 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_string_stream_3 on data_point_string (stream_id);
alter table functions add constraint fk_functions_owner_4 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_functions_owner_4 on functions (owner_id);
alter table sources add constraint fk_sources_owner_5 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_sources_owner_5 on sources (owner_id);
alter table streams add constraint fk_streams_owner_6 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_streams_owner_6 on streams (owner_id);
alter table streams add constraint fk_streams_source_7 foreign key (source_id) references sources (id) on delete restrict on update restrict;
create index ix_streams_source_7 on streams (source_id);
alter table parsers add constraint fk_parsers_source_8 foreign key (source_id) references sources (id) on delete restrict on update restrict;
create index ix_parsers_source_8 on parsers (source_id);
alter table parsers add constraint fk_parsers_stream_9 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_parsers_stream_9 on parsers (stream_id);
alter table vfiles add constraint fk_vfiles_owner_10 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_vfiles_owner_10 on vfiles (owner_id);
alter table vfiles add constraint fk_vfiles_linkedStream_11 foreign key (linked_stream_id) references streams (id) on delete restrict on update restrict;
create index ix_vfiles_linkedStream_11 on vfiles (linked_stream_id);



alter table functions_streams add constraint fk_functions_streams_function_01 foreign key (functions_id) references functions (id) on delete restrict on update restrict;

alter table functions_streams add constraint fk_functions_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists actuators;

drop table if exists data_point_double;

drop table if exists data_point_string;

drop table if exists functions;

drop table if exists functions_streams;

drop table if exists operators;

drop table if exists sources;

drop table if exists streams;

drop table if exists parsers;

drop table if exists users;

drop table if exists vfiles;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists actuators_seq;

drop sequence if exists data_point_double_seq;

drop sequence if exists data_point_string_seq;

drop sequence if exists functions_seq;

drop sequence if exists operators_seq;

drop sequence if exists sources_seq;

drop sequence if exists streams_seq;

drop sequence if exists parsers_seq;

drop sequence if exists users_seq;

drop sequence if exists vfiles_seq;

