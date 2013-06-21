# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actuators (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  input_parser              varchar(255),
  constraint pk_actuators primary key (id))
;

create table data_point_double (
  id                        bigint auto_increment not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      double,
  constraint uq_data_point_double_1 unique (stream_id,timestamp),
  constraint pk_data_point_double primary key (id))
;

create table data_point_string (
  id                        bigint auto_increment not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      varchar(160),
  constraint uq_data_point_string_1 unique (stream_id,timestamp),
  constraint pk_data_point_string primary key (id))
;

create table functions (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  constraint pk_functions primary key (id))
;

create table operators (
  id                        bigint auto_increment not null,
  constraint pk_operators primary key (id))
;

create table representations (
  id                        bigint auto_increment not null,
  timestamp                 bigint,
  expires                   bigint,
  content_type              varchar(255),
  content                   varchar(8192),
  parent_id                 bigint,
  constraint pk_representations primary key (id))
;

create table resources (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  label                     varchar(255),
  update_mode               varchar(1),
  polling_period            bigint,
  last_polled               bigint,
  last_posted               bigint,
  parent_id                 bigint,
  polling_url               varchar(255),
  polling_authentication_key varchar(255),
  description               varchar(255),
  secret_key                varchar(255),
  version                   integer not null,
  constraint ck_resources_update_mode check (update_mode in ('D','O','U')),
  constraint uq_resources_1 unique (owner_id,parent_id,label),
  constraint pk_resources primary key (id))
;

create table resource_log (
  id                        bigint auto_increment not null,
  resource_id               bigint,
  creation_timestamp        bigint,
  response_timestamp        bigint,
  interaction_type          varchar(1),
  method                    varchar(255) not null,
  uri                       varchar(255),
  headers                   varchar(4096),
  constraint ck_resource_log_interaction_type check (interaction_type in ('O','D','U')),
  constraint pk_resource_log primary key (id))
;

create table settings (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  val                       varchar(255),
  constraint uq_settings_1 unique (name),
  constraint pk_settings primary key (id))
;

create table streams (
  id                        bigint auto_increment not null,
  type                      varchar(1),
  latitude                  double,
  longtitude                double,
  description               varchar(255),
  public_access             tinyint(1) default 0,
  public_search             tinyint(1) default 0,
  frozen                    tinyint(1) default 0,
  history_size              bigint,
  last_updated              bigint,
  secret_key                varchar(255),
  owner_id                  bigint,
  resource_id               bigint,
  version                   integer not null,
  constraint ck_streams_type check (type in ('U','D','S')),
  constraint pk_streams primary key (id))
;

create table parsers (
  id                        bigint auto_increment not null,
  resource_id               bigint,
  stream_id                 bigint,
  input_parser              varchar(255),
  input_type                varchar(255),
  timeformat                varchar(255),
  data_group                integer,
  time_group                integer,
  number_of_points          integer,
  constraint pk_parsers primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(255) not null,
  user_name                 varchar(255) not null,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  description               varchar(2048),
  latitude                  double,
  longitude                 double,
  creation_date             datetime not null,
  last_login                datetime,
  token                     varchar(255),
  admin                     tinyint(1) default 0,
  version                   integer not null,
  constraint uq_users_email unique (email),
  constraint uq_users_user_name unique (user_name),
  constraint pk_users primary key (id))
;

create table vfiles (
  id                        bigint auto_increment not null,
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

create table users_streams (
  users_id                       bigint not null,
  streams_id                     bigint not null,
  constraint pk_users_streams primary key (users_id, streams_id))
;
alter table actuators add constraint fk_actuators_owner_1 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_actuators_owner_1 on actuators (owner_id);
alter table data_point_double add constraint fk_data_point_double_stream_2 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_double_stream_2 on data_point_double (stream_id);
alter table data_point_string add constraint fk_data_point_string_stream_3 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_string_stream_3 on data_point_string (stream_id);
alter table functions add constraint fk_functions_owner_4 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_functions_owner_4 on functions (owner_id);
alter table representations add constraint fk_representations_parent_5 foreign key (parent_id) references resources (id) on delete restrict on update restrict;
create index ix_representations_parent_5 on representations (parent_id);
alter table resources add constraint fk_resources_owner_6 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_resources_owner_6 on resources (owner_id);
alter table resources add constraint fk_resources_parent_7 foreign key (parent_id) references resources (id) on delete restrict on update restrict;
create index ix_resources_parent_7 on resources (parent_id);
alter table resource_log add constraint fk_resource_log_resource_8 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_resource_log_resource_8 on resource_log (resource_id);
alter table streams add constraint fk_streams_owner_9 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_streams_owner_9 on streams (owner_id);
alter table streams add constraint fk_streams_resource_10 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_streams_resource_10 on streams (resource_id);
alter table parsers add constraint fk_parsers_resource_11 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_parsers_resource_11 on parsers (resource_id);
alter table parsers add constraint fk_parsers_stream_12 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_parsers_stream_12 on parsers (stream_id);
alter table vfiles add constraint fk_vfiles_owner_13 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_vfiles_owner_13 on vfiles (owner_id);
alter table vfiles add constraint fk_vfiles_linkedStream_14 foreign key (linked_stream_id) references streams (id) on delete restrict on update restrict;
create index ix_vfiles_linkedStream_14 on vfiles (linked_stream_id);



alter table functions_streams add constraint fk_functions_streams_functions_01 foreign key (functions_id) references functions (id) on delete restrict on update restrict;

alter table functions_streams add constraint fk_functions_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

alter table users_streams add constraint fk_users_streams_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_streams add constraint fk_users_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table actuators;

drop table data_point_double;

drop table data_point_string;

drop table functions;

drop table functions_streams;

drop table operators;

drop table representations;

drop table resources;

drop table resource_log;

drop table settings;

drop table streams;

drop table users_streams;

drop table parsers;

drop table users;

drop table vfiles;

SET FOREIGN_KEY_CHECKS=1;

