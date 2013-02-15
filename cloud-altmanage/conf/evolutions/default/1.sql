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
  description               varchar(255),
  location                  varchar(255),
  user_id                   bigint,
  constraint uq_end_point_1 unique (user_id,label),
  constraint pk_end_point primary key (id))
;

create table file (
  uuid                      bigint,
  path                      varchar(255),
  owner                     varchar(255),
  type                      integer,
  ref_id                    bigint,
  constraint ck_file_type check (type in (0,1)),
  constraint uq_file_1 unique (owner,path))
;

create table pipeline (
  id                        bigint not null,
  path                      varchar(255),
  label                     varchar(255),
  end_point_id              bigint,
  user_id                   bigint,
  polling_period            bigint,
  last_polled               bigint,
  last_updated              bigint,
  input_parser              varchar(255),
  public_access             boolean,
  type                      integer,
  xpath_string              varchar(255),
  constraint ck_pipeline_type check (type in (0,1)),
  constraint uq_pipeline_1 unique (end_point_id,path),
  constraint pk_pipeline primary key (id))
;

create table resource (
  id                        bigint not null,
  path                      varchar(255),
  label                     varchar(255),
  end_point_id              bigint,
  user_id                   bigint,
  polling_period            bigint,
  last_polled               bigint,
  last_updated              bigint,
  input_parser              varchar(255),
  public_access             boolean,
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


create table pipeline_user (
  pipeline_id                    bigint not null,
  user_id                        bigint not null,
  constraint pk_pipeline_user primary key (pipeline_id, user_id))
;

create table resource_user (
  resource_id                    bigint not null,
  user_id                        bigint not null,
  constraint pk_resource_user primary key (resource_id, user_id))
;

create table user_resource (
  user_id                        bigint not null,
  resource_id                    bigint not null,
  constraint pk_user_resource primary key (user_id, resource_id))
;

create table user_end_point (
  user_id                        bigint not null,
  end_point_id                   bigint not null,
  constraint pk_user_end_point primary key (user_id, end_point_id))
;
create sequence data_point_seq;

create sequence end_point_seq;

create sequence pipeline_seq;

create sequence resource_seq;

create sequence user_seq;

alter table data_point add constraint fk_data_point_resource_1 foreign key (resource_id) references resource (id) on delete restrict on update restrict;
create index ix_data_point_resource_1 on data_point (resource_id);
alter table end_point add constraint fk_end_point_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_end_point_user_2 on end_point (user_id);
alter table pipeline add constraint fk_pipeline_endPoint_3 foreign key (end_point_id) references end_point (id) on delete restrict on update restrict;
create index ix_pipeline_endPoint_3 on pipeline (end_point_id);
alter table pipeline add constraint fk_pipeline_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_pipeline_user_4 on pipeline (user_id);
alter table resource add constraint fk_resource_endPoint_5 foreign key (end_point_id) references end_point (id) on delete restrict on update restrict;
create index ix_resource_endPoint_5 on resource (end_point_id);
alter table resource add constraint fk_resource_user_6 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_resource_user_6 on resource (user_id);



alter table pipeline_user add constraint fk_pipeline_user_pipeline_01 foreign key (pipeline_id) references pipeline (id) on delete restrict on update restrict;

alter table pipeline_user add constraint fk_pipeline_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table resource_user add constraint fk_resource_user_resource_01 foreign key (resource_id) references resource (id) on delete restrict on update restrict;

alter table resource_user add constraint fk_resource_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_resource add constraint fk_user_resource_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_resource add constraint fk_user_resource_resource_02 foreign key (resource_id) references resource (id) on delete restrict on update restrict;

alter table user_end_point add constraint fk_user_end_point_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_end_point add constraint fk_user_end_point_end_point_02 foreign key (end_point_id) references end_point (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists data_point;

drop table if exists end_point;

drop table if exists file;

drop table if exists pipeline;

drop table if exists pipeline_user;

drop table if exists resource;

drop table if exists resource_user;

drop table if exists user;

drop table if exists user_resource;

drop table if exists user_end_point;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists data_point_seq;

drop sequence if exists end_point_seq;

drop sequence if exists pipeline_seq;

drop sequence if exists resource_seq;

drop sequence if exists user_seq;

