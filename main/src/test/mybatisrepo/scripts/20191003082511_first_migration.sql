-- // First migration.
-- Migration SQL that makes the change goes here.

create table car (
  id uniqueidentifier primary key,
  brand varchar(256),
  max_speed int
)

-- //@UNDO
-- SQL to undo the change goes here.

drop table car
