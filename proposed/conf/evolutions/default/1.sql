# --- !Ups

create table settings (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  val                       varchar(255),
  constraint uq_settings_1 unique (name),
  constraint pk_settings primary key (id))
;

# --- !Downs

drop table settings;

