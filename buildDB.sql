
CREATE DATABASE IF NOT EXISTS sicsth2;
GRANT ALL PRIVILEGES ON sicsth2.* TO 'sicsthsense'@'localhost' IDENTIFIED BY 'sicsdev';
USE sicsth2;

create table IF NOT EXISTS actuators (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  input_parser              varchar(255),
  constraint pk_actuators primary key (id))
;

create table IF NOT EXISTS data_point_double (
  id                        bigint auto_increment not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      double,
  constraint uq_data_point_double_1 unique (stream_id,timestamp),
  constraint pk_data_point_double primary key (id))
;

create table IF NOT EXISTS data_point_string (
  id                        bigint auto_increment not null,
  stream_id                 bigint,
  timestamp                 bigint,
  data                      varchar(160),
  constraint uq_data_point_string_1 unique (stream_id,timestamp),
  constraint pk_data_point_string primary key (id))
;

create table IF NOT EXISTS functions (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  constraint pk_functions primary key (id))
;

create table IF NOT EXISTS operators (
  id                        bigint auto_increment not null,
  constraint pk_operators primary key (id))
;

create table IF NOT EXISTS resources (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  label                     varchar(255),
  polling_period            bigint,
  last_polled               bigint,
  last_posted               bigint,
  polling_url               varchar(255),
  polling_authentication_key varchar(255),
  description               varchar(255),
  parent_id                 bigint,
  secret_key                varchar(255),
  version                   integer not null,
  constraint uq_resources_1 unique (owner_id,parent_id,label),
  constraint pk_resources primary key (id))
;

create table IF NOT EXISTS resource_log (
  id                        bigint auto_increment not null,
  resource_id               bigint,
  creation_timestamp        bigint,
  response_timestamp        bigint,
  parsed_successfully       tinyint(1) default 0,
  is_poll                   tinyint(1) default 0,
  body                      varchar(8192),
  method                    varchar(255),
  host_name                 varchar(255),
  uri                       varchar(255),
  headers                   varchar(4096),
  message                   varchar(4096),
  version                   integer not null,
  constraint uq_resource_log_1 unique (resource_id,is_poll),
  constraint pk_resource_log primary key (id))
;

create table IF NOT EXISTS settings (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  val                       varchar(255),
  constraint uq_settings_1 unique (name),
  constraint pk_settings primary key (id))
;

create table IF NOT EXISTS streams (
  id                        bigint auto_increment not null,
  type                      varchar(1),
  latitude                  double,
  longitude                double,
  description               varchar(255),
  public_access             tinyint(1) default 0,
  public_search             tinyint(1) default 0,
  frozen                    tinyint(1) default 0,
  history_size              bigint,
  last_updated              bigint,
  secret_key                varchar(255),
  owner_id                  bigint,
  resource_id               bigint,
	function									varchar(255),
  version                   integer not null,
  constraint ck_streams_type check (type in ('U','D','S')),
  constraint pk_streams primary key (id))
;

create table IF NOT EXISTS parsers (
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

create table IF NOT EXISTS users (
  id                        bigint auto_increment not null,
  email                     varchar(255) not null,
  username                 varchar(255) not null,
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
  constraint uq_users_username unique (username),
  constraint pk_users primary key (id))
;

create table IF NOT EXISTS dependents (
  id                      bigint auto_increment not null,
  stream_id               bigint,
  dependent_id            bigint,
  constraint pk_dependents primary key (id))
;

create table IF NOT EXISTS triggers (
  id                      bigint auto_increment not null,
  stream_id               bigint not null,
  url											varchar(255) not null,
  operator								varchar(5) not null,
  operand									double not null,
  payload									varchar(255),
  constraint pk_triggers primary key (id))
;

create table IF NOT EXISTS vfiles (
  id                        bigint auto_increment not null,
  path                      varchar(255) not null,
  owner_id                  bigint,
  type                      varchar(1) not null,
  linked_stream_id          bigint,
  constraint ck_vfiles_type check (type in ('F','D')),
  constraint uq_vfiles_1 unique (linked_stream_id,path),
  constraint pk_vfiles primary key (id))
;


create table IF NOT EXISTS functions_streams (
  functions_id                   bigint not null,
  streams_id                     bigint not null,
  constraint pk_functions_streams primary key (functions_id, streams_id))
;

create table IF NOT EXISTS users_streams (
  users_id                       bigint not null,
  streams_id                     bigint not null,
  constraint pk_users_streams primary key (users_id, streams_id))
;
alter table actuators add constraint fk_actuators_owner_1 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_actuators_owner_1 on actuators (owner_id);
#alter table data_point_double add constraint fk_data_point_double_stream_2 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
#alter table data_point_double add constraint fk_data_point_double_stream_2 foreign key (stream_id) references streams (id) on delete set null on update restrict;
create index ix_data_point_double_stream_2 on data_point_double (stream_id);
alter table data_point_string add constraint fk_data_point_string_stream_3 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_data_point_string_stream_3 on data_point_string (stream_id);
alter table functions add constraint fk_functions_owner_4 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_functions_owner_4 on functions (owner_id);
alter table resources add constraint fk_resources_owner_5 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_resources_owner_5 on resources (owner_id);
#alter table resources add constraint fk_resources_parent_6 foreign key (parent_id) references resources (id) on delete restrict on update restrict;
#create index ix_resources_parent_6 on resources (parent_id);
alter table resource_log add constraint fk_resource_log_resource_7 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_resource_log_resource_7 on resource_log (resource_id);
#alter table streams add constraint fk_streams_owner_8 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_streams_owner_8 on streams (owner_id);
#alter table streams add constraint fk_streams_resource_9 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_streams_resource_9 on streams (resource_id);
alter table parsers add constraint fk_parsers_resource_10 foreign key (resource_id) references resources (id) on delete restrict on update restrict;
create index ix_parsers_resource_10 on parsers (resource_id);
alter table parsers add constraint fk_parsers_stream_11 foreign key (stream_id) references streams (id) on delete restrict on update restrict;
create index ix_parsers_stream_11 on parsers (stream_id);
alter table vfiles add constraint fk_vfiles_owner_12 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_vfiles_owner_12 on vfiles (owner_id);
#Liam:causes awkwardness
#alter table vfiles add constraint fk_vfiles_linkedStream_13 foreign key (linked_stream_id) references streams (id) on delete restrict on update restrict;
create index ix_vfiles_linkedStream_13 on vfiles (linked_stream_id);

create index ix_dependents_stream_14 on dependents (stream_id);
create index ix_dependents_dependent_15 on dependents (dependent_id);
create index ix_triggers_stream_16 on triggers (stream_id);

alter table functions_streams add constraint fk_functions_streams_functions_01 foreign key (functions_id) references functions (id) on delete restrict on update restrict;

alter table functions_streams add constraint fk_functions_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

alter table users_streams add constraint fk_users_streams_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_streams add constraint fk_users_streams_streams_02 foreign key (streams_id) references streams (id) on delete restrict on update restrict;

