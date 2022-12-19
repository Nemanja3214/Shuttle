-- Vehicle types are hardcoded at the start and never changed.
-- Don't remove this!

insert into vehicle_type(name, price_PerKM) values ('Standard', 40);
insert into vehicle_type(name, price_PerKM) values ('Luxury', 80);
insert into vehicle_type(name, price_PerKM) values ('Van', 60);

-- Driver

insert into credentials(id, email, password) values (1, 'bob@gmail.com', 'bob123');
insert into generic_user(id, name, credentials_id) values (1, 'Bob', 1);
insert into driver(id, available, blocked) values (1, true, false);