-- Vehicle types are hardcoded at the start and never changed.
-- Don't remove this!

insert into vehicle_type(name, price_PerKM) values ('Standard', 40);
insert into vehicle_type(name, price_PerKM) values ('Luxury', 80);
insert into vehicle_type(name, price_PerKM) values ('Van', 60);

-- Driver

insert into credentials(id, email, password) values (1, 'bob@gmail.com', 'bob123');
insert into credentials(id, email, password) values (2, 'john@gmail.com', 'john123');
insert into credentials(id, email, password) values (3, 'troy@gmail.com', 'troy123');

insert into generic_user(id, name, credentials_id, logged_in) values (1, 'Bob', 1, true);
insert into generic_user(id, name, credentials_id, logged_in) values (2, 'John', 2, true);
insert into generic_user(id, name, credentials_id, logged_in) values (3, 'Troy', 3, true);

insert into driver(id, available, blocked) values (1, true, false);
insert into passenger(id) values (2);
insert into passenger(id) values (3);

insert into vehicle(id, vehicle_type_id, driver_id) values(1, 1, 1);

