--liquibase formatted sql
--changeset author:jimmy
create table car (
  id uniqueidentifier primary key,
  brand varchar(256),
  max_speed int
)

--rollback drop table car
