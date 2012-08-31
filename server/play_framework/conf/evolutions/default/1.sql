# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table measurement (
  id                        bigint not null,
  sensor_config_id          bigint not null,
  timestamp                 timestamp,
  value                     float,
  raw_value                 float,
  constraint pk_measurement primary key (id))
;

create table node (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_node primary key (id))
;

create table node_config (
  id                        bigint not null,
  deviceUid                 bigint not null,
  device_uid                bigint,
  creation_date             timestamp,
  name                      varchar(255),
  sampling_period           integer,
  description               varchar(255),
  constraint pk_node_config primary key (id))
;

create table sensor_config (
  id                        bigint not null,
  creation_date             timestamp,
  internal_id               integer,
  name                      varchar(255),
  pin                       integer,
  steps                     integer,
  class_name                varchar(255),
  is_sample                 boolean,
  units                     varchar(255),
  description               varchar(255),
  node_config_id            bigint,
  ref                       varchar(255),
  constraint pk_sensor_config primary key (id))
;

create table sensor_type (
  ref                       varchar(255) not null,
  description               varchar(255),
  constraint pk_sensor_type primary key (ref))
;

create sequence measurement_seq;

create sequence node_seq;

create sequence node_config_seq;

create sequence sensor_config_seq;

create sequence sensor_type_seq;

alter table measurement add constraint fk_measurement_sensor_config_1 foreign key (sensor_config_id) references sensor_config (id) on delete restrict on update restrict;
create index ix_measurement_sensor_config_1 on measurement (sensor_config_id);
alter table node_config add constraint fk_node_config_node_2 foreign key (deviceUid) references node (id) on delete restrict on update restrict;
create index ix_node_config_node_2 on node_config (deviceUid);
alter table sensor_config add constraint fk_sensor_config_nodeConfig_3 foreign key (node_config_id) references node_config (id) on delete restrict on update restrict;
create index ix_sensor_config_nodeConfig_3 on sensor_config (node_config_id);
alter table sensor_config add constraint fk_sensor_config_type_4 foreign key (ref) references sensor_type (ref) on delete restrict on update restrict;
create index ix_sensor_config_type_4 on sensor_config (ref);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists measurement;

drop table if exists node;

drop table if exists node_config;

drop table if exists sensor_config;

drop table if exists sensor_type;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists measurement_seq;

drop sequence if exists node_seq;

drop sequence if exists node_config_seq;

drop sequence if exists sensor_config_seq;

drop sequence if exists sensor_type_seq;

