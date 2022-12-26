-- Vehicle types are hardcoded at the start and never changed.
-- Don't remove this!

insert into vehicle_type(name, price_PerKM) values ('Standard', 40);
insert into vehicle_type(name, price_PerKM) values ('Luxury', 80);
insert into vehicle_type(name, price_PerKM) values ('Van', 60);

-- Role types are hardcoded at the start and never changed.
-- Don't remove this!

insert into role(name) values('passenger');
insert into role(name) values('driver');
insert into role(name) values('admin');

-- Elementary user data

insert into generic_user(email, password, enabled) values ('bob@gmail.com', 'bob123', true);
insert into generic_user(email, password, enabled) values ('john@gmail.com', 'john123', true);
insert into generic_user(email, password, enabled) values ('troy@gmail.com', 'troy123', true);
insert into generic_user(email, password, enabled) values ('admin@gmail.com', 'admin', true);

insert into user_role(user_id, role_id) values (1, 2);
insert into user_role(user_id, role_id) values (2, 1);
insert into user_role(user_id, role_id) values (3, 1);
insert into user_role(user_id, role_id) values (4, 3);

-- Role specific user data

insert into driver(id, available, blocked, time_worked_today) values (1, true, false, 0);
insert into passenger(id) values (2);
insert into passenger(id) values (3);

-- Vehicle

insert into vehicle(vehicle_type_id, driver_id) values(1, 1);

----------------- Test ride, because swagger auth doesn't work and we don't have ride creation on the frontend yet.

insert into location(address, latitude, longitude) values ('Street 1', 45.267136, 19.833549);
insert into location(address, latitude, longitude) values ('Street 2', 44.267136, 20.833549);

insert into route() values();

insert into route_locations(route_id, locations_id) values(1, 1);
insert into route_locations(route_id, locations_id) values(1, 2);

insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport) values (0, 1, 1, 1, true, false);

insert into ride_passengers(ride_id, passengers_id) values (1, 2);
insert into ride_passengers(ride_id, passengers_id) values (1, 3);