--liquibase formatted sql
--changeset navpil:create_car_table (note the underscore)
create table car (
  id uniqueidentifier primary key,
  brand varchar(256),
  max_speed int
)

--rollback drop table car
