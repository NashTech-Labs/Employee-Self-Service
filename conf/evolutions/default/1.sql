# --- First database schema

# --- !Ups

set ignorecase true;

create table employee (
  id                        bigint not null,
  name                      varchar(255) not null,
  address                   varchar(1000) not null,
  dob		                timestamp,
  joining_date              timestamp,
  designation               varchar(255) not null,
  constraint pk_employee primary key (id))
;

# --- !Downs

drop table if exists employee;
