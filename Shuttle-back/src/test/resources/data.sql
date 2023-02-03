-------------- Pseudo-enum tables

insert into vehicle_type(name, price_PerKM) values ('STANDARD', 40);
insert into vehicle_type(name, price_PerKM) values ('LUXURY', 80);
insert into vehicle_type(name, price_PerKM) values ('VAN', 60);
insert into role(name) values('passenger');
insert into role(name) values('driver');
insert into role(name) values('admin');

-------------- Users

insert into generic_user(email, password, enabled, blocked, active, name) values ('bob@gmail.com', '$2a$10$j2988SIGRINo0s4/F1ivJ.zBcyn39ap3sizeRs38z.zwzx9nxMpmm', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('john@gmail.com', '$2a$10$XrWH9VDQR2aCn9tThclQJOrNwhKYs525HG3X.9zI1MlG21F8mKw/2', true, false, false, 'John');
insert into generic_user(email, password, enabled, blocked, active, name) values ('troy@gmail.com', '$2a$10$RNBI5BuqlU8iUFoOCdeGc.V.afrcNyQSEs1t43JJ5TdXu9/wz86mi', true, false, false, 'Troy');
insert into generic_user(email, password, enabled, blocked, active, name) values ('admin@gmail.com', '$2a$10$GxRpGz0dRDEK52.VeoiDA.azoCStgfAZjficcK/El5hxKCDtUWHBm', true, false, false, 'Admin');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver1@gmail.com', '$2a$10$TSGeDlyusssAVvBnr//IpegdbvSHcmWwcjHc9dew1SIsT.3N8Uoda', true, false, false, 'DriverName_1');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver2@gmail.com', '$2a$10$2jxtILDGfYS9lUtuIMHDB.fZIx7JrAV/9ELBnkCTWaIisnuuP2Oo6', true, false, false, 'DriverName_2');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver3@gmail.com', '$2a$10$krk0jTL1y0eFbRqBB1jO9eZv.gSZkZ/vPzXwnZG1W3WDh/xZs8OIC', true, false, false, 'DriverName_3');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver4@gmail.com', '$2a$10$wEK.5n29HkO3NEJxVfPaYODNmRIPCfSHwwv77KrJ5JXKHWGdINPee', true, false, false, 'DriverName_4');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver5@gmail.com', '$2a$10$ePVkPF/GxUI2V4kSi8qhi.hRlCX/h5CgZtpdOoy05btsgbfaaCNOC', true, false, false, 'DriverName_5');
insert into generic_user(email, password, enabled, blocked, active, name) values ('driver6@gmail.com', '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'DriverName_6');

insert into generic_user(email, password, enabled, blocked, active, name) values ('p1@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');

insert into generic_user(email, password, enabled, blocked, active, name) values ('p2@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p3@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p4@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p5@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p6@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p7@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p8@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p9@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');
insert into generic_user(email, password, enabled, blocked, active, name) values ('p10@gmail.com',      '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'Bob');

insert into generic_user(email, password, enabled, blocked, active, name) values ('d1@gmail.com', '$2a$10$krk0jTL1y0eFbRqBB1jO9eZv.gSZkZ/vPzXwnZG1W3WDh/xZs8OIC', true, false, false, 'D1');
insert into generic_user(email, password, enabled, blocked, active, name) values ('d2@gmail.com', '$2a$10$wEK.5n29HkO3NEJxVfPaYODNmRIPCfSHwwv77KrJ5JXKHWGdINPee', true, false, false, 'D2');
insert into generic_user(email, password, enabled, blocked, active, name) values ('d3@gmail.com', '$2a$10$ePVkPF/GxUI2V4kSi8qhi.hRlCX/h5CgZtpdOoy05btsgbfaaCNOC', true, false, false, 'D3');
insert into generic_user(email, password, enabled, blocked, active, name) values ('d4@gmail.com', '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'D4');
insert into generic_user(email, password, enabled, blocked, active, name) values ('d5@gmail.com', '$2a$10$OBmqrkeGcidferI6rWbrG.umlSCyM6CFJ/APVBazwDEQiVuFguRse', true, false, false, 'D5');



insert into user_role(user_id, role_id) values (1, 2);
insert into user_role(user_id, role_id) values (2, 1);
insert into user_role(user_id, role_id) values (3, 1);
insert into user_role(user_id, role_id) values (4, 3);

insert into user_role(user_id, role_id) values (5, 2);
insert into user_role(user_id, role_id) values (6, 2);
insert into user_role(user_id, role_id) values (7, 2);
insert into user_role(user_id, role_id) values (8, 2);
insert into user_role(user_id, role_id) values (9, 2);
insert into user_role(user_id, role_id) values (10, 2);

insert into user_role(user_id, role_id) values (11, 1);

insert into user_role(user_id, role_id) values (12, 1);
insert into user_role(user_id, role_id) values (13, 1);
insert into user_role(user_id, role_id) values (14, 1);
insert into user_role(user_id, role_id) values (15, 1);
insert into user_role(user_id, role_id) values (16, 1);
insert into user_role(user_id, role_id) values (17, 1);
insert into user_role(user_id, role_id) values (18, 1);
insert into user_role(user_id, role_id) values (19, 1);
insert into user_role(user_id, role_id) values (20, 1);

insert into user_role(user_id, role_id) values (21, 2);
insert into user_role(user_id, role_id) values (22, 2);
insert into user_role(user_id, role_id) values (23, 2);
insert into user_role(user_id, role_id) values (24, 2);
insert into user_role(user_id, role_id) values (25, 2);

insert into driver(id, available, time_worked_today) values (1, true, 0);
insert into driver(id, available, time_worked_today) values (5, true, 0);
insert into driver(id, available, time_worked_today) values (6, true, 0);
insert into driver(id, available, time_worked_today) values (7, true, 0);
insert into driver(id, available, time_worked_today) values (8, true, 0);
insert into driver(id, available, time_worked_today) values (9, true, 0);
insert into driver(id, available, time_worked_today) values (10, true, 0);
insert into driver(id, available, time_worked_today) values (21, true, 0);
insert into driver(id, available, time_worked_today) values (22, true, 0);
insert into driver(id, available, time_worked_today) values (23, true, 0);
insert into driver(id, available, time_worked_today) values (24, true, 0);
insert into driver(id, available, time_worked_today) values (25, true, 0);

insert into passenger(id) values (2);
insert into passenger(id) values (3);
insert into passenger(id) values (11);
insert into passenger(id) values (12);
insert into passenger(id) values (13);
insert into passenger(id) values (14);
insert into passenger(id) values (15);
insert into passenger(id) values (16);
insert into passenger(id) values (17);
insert into passenger(id) values (18);
insert into passenger(id) values (19);
insert into passenger(id) values (20);

insert into location(latitude, longitude, address) values (45.235820, 19.803677, 'Novi Sad 1');
insert into location(latitude, longitude, address) values (45.233752, 19.816665, 'Novi Sad 2');
insert into location(latitude, longitude, address) values (45.244830, 19.846957, 'Novi Sad 3');
insert into location(latitude, longitude, address) values (45.249211, 19.816746, 'Novi Sad 4');
insert into location(latitude, longitude, address) values (45.260781, 19.832454, 'Futog 1');
insert into location(latitude, longitude, address) values (45.238922, 19.693419, 'Futog 2');
insert into location(latitude, longitude, address) values (45.236354, 19.715382, 'Futog 3');

insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(1, 1, 1, true, true, 1);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(5, 2, 2, true, true, 2);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(6, 3, 1, true, false, 3);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(7, 4, 1, false, true, 4);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(8, 5, 2, false, true, 5);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(9, 6, 1, false, false, 6);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(10, 7, 3, false, false, 7);

insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(21, 1, 1, true, true, 3);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(22, 1, 1, true, true, 3);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(23, 1, 1, true, true, 3);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(24, 1, 1, true, true, 3);
insert into vehicle(driver_id, current_location_id, vehicle_type_id, baby_transport, pet_transport, passenger_seats) values(25, 1, 1, true, true, 3);

--------------- Ride, location, etc.

-- Ride 1: used for getRide*(), readonly

insert into route default values;
insert into route_locations(route_id, locations_id) values(1, 1);
insert into route_locations(route_id, locations_id) values(1, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (0, 1, 1, 1, true, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (1, 2);

-- Ride 2: used for accept

insert into route default values;
insert into route_locations(route_id, locations_id) values(2, 1);
insert into route_locations(route_id, locations_id) values(2, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (0, 7, 1, 1, true, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (2, 12);

-- Ride 3: used for start

insert into route default values;
insert into route_locations(route_id, locations_id) values(3, 1);
insert into route_locations(route_id, locations_id) values(3, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (1, 6, 1, 1, false, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (3, 13);

-- Ride 4: used for finish

insert into route default values;
insert into route_locations(route_id, locations_id) values(4, 1);
insert into route_locations(route_id, locations_id) values(4, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes, start_time) values (2, 9, 1, 1, false, false, 123.4, 5.6, 100, CURRENT_TIMESTAMP);
insert into ride_passengers(ride_id, passengers_id) values (4, 14);

-- Ride 5: used for reject

insert into route default values;
insert into route_locations(route_id, locations_id) values(5, 1);
insert into route_locations(route_id, locations_id) values(5, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (1, 8, 1, 2, false, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (5, 15);

-- Ride 6: used for reject which is in a bad state, read only! The data is bad because there's multiple passengers-drivers for one ride. TODO

insert into route default values;
insert into route_locations(route_id, locations_id) values(6, 1);
insert into route_locations(route_id, locations_id) values(6, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (3, 8, 1, 2, false, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (6, 15);

-- Ride 7: used for withdraw

insert into route default values;
insert into route_locations(route_id, locations_id) values(7, 1);
insert into route_locations(route_id, locations_id) values(7, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (1, 21, 1, 1, false, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (7, 16);
insert into ride_passengers(ride_id, passengers_id) values (7, 17);

-- Ride 8: used for panic

insert into route default values;
insert into route_locations(route_id, locations_id) values(8, 1);
insert into route_locations(route_id, locations_id) values(8, 2);
insert into ride(status, driver_id, route_id, vehicle_type_id, pet_transport, baby_transport, total_cost, total_length, estimated_time_in_minutes) values (1, 22, 1, 1, false, false, 123.4, 5.6, 100);
insert into ride_passengers(ride_id, passengers_id) values (8, 18);


--------------- Favorite rides - 10 for John, 9 for Troy


insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (2);
insert into favorite_route(vehicle_type_id) values (3);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (2);
insert into favorite_route(vehicle_type_id) values (3);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (2);
insert into favorite_route(vehicle_type_id) values (3);
insert into favorite_route(vehicle_type_id) values (1);

insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);
insert into favorite_route(vehicle_type_id) values (1);

insert into favorite_route(vehicle_type_id, favorite_name, baby_transport, pet_transport, scheduled_time) values (1, 'abc', false, true, CURRENT_TIMESTAMP);

insert into favorite_route_passengers(favorite_route_id, passengers_id) values (1, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (2, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (3, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (4, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (5, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (6, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (7, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (8, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (9, 2);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (10, 2);

insert into favorite_route_passengers(favorite_route_id, passengers_id) values (11, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (12, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (13, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (14, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (15, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (16, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (17, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (18, 3);
insert into favorite_route_passengers(favorite_route_id, passengers_id) values (19, 3);

insert into favorite_route_passengers(favorite_route_id, passengers_id) values (20, 11);
insert into favorite_route_locations(favorite_route_id, locations_id) values(20, 1);
insert into favorite_route_locations(favorite_route_id, locations_id) values(20, 2);

--------------- Reports(?)


-- Rule of thumb: DON'T PUT CUSTOM IDs FOR ENTITIES THAT YOU WILL BE INSERTING MANUALLY
-- save(Entity) will use 'default' for ID. If you manually have ID 1,2,etc. used up here,
-- hibernate will throw primary key violation. Setting the ID manually for the Entity, in
-- code doesn't work either.
-- Which entites are OK to manually put IDs for: users (if any other table: ask first!!!)
--