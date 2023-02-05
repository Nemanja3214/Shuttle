-------------- Pseudo-enum tables

insert into vehicle_type(name, price_PerKM) values ('STANDARD', 40);
insert into vehicle_type(name, price_PerKM) values ('LUXURY', 80);
insert into vehicle_type(name, price_PerKM) values ('VAN', 60);
insert into role(name) values('passenger');
insert into role(name) values('driver');
insert into role(name) values('admin');



--------------- Reports(?)


-- Rule of thumb: DON'T PUT CUSTOM IDs FOR ENTITIES THAT YOU WILL BE INSERTING MANUALLY
-- save(Entity) will use 'default' for ID. If you manually have ID 1,2,etc. used up here,
-- hibernate will throw primary key violation. Setting the ID manually for the Entity, in
-- code doesn't work either.
-- Which entites are OK to manually put IDs for: users (if any other table: ask first!!!)
--