--liquibase formatted sql
--changeset navpil:create_garages (note the underscore)
create table garage (
  id uniqueidentifier primary key,
  name varchar(256),
  year_built int
)

--rollback drop table garage
