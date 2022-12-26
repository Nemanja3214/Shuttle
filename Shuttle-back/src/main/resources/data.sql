-- Vehicle types are hardcoded at the start and never changed.
-- Don't remove this!

insert into location(address, latitude, longitude) values ('Strazilovska 15', 50.762430, 15.109200)
insert into location(address, latitude, longitude) values ('Ruma', 45.007889, 19.822540)

 
insert into vehicle_type(name, price_PerKM) values ('Standard', 40)
insert into vehicle_type(name, price_PerKM) values ('Luxury', 80)
insert into vehicle_type(name, price_PerKM) values ('Van', 60)
insert into role(name) values('passenger')
insert into role(name) values('admin')
insert into role(name) values('driver')
insert into generic_user(address, email,enabled,last_password_reset_date,name,password,profile_picture,surname,telephone_number) values ('221b Baker Street','pera.peric@email.com',true,null,'peter','','','jackson','123')
insert into user_role(user_id, role_id) values (1,1)
insert into generic_user(address, email,enabled,last_password_reset_date,name,password,profile_picture,surname,telephone_number) values ('221b Baker Street','scarface@email.com',true,null,'Tony','','','Montana','123')
insert into user_role(user_id, role_id) values (2,2)
insert into generic_user(address, email,enabled,last_password_reset_date,name,password,profile_picture,surname,telephone_number) values ('221b Baker Street','taxi@email.com',true,null,'Robert','','','Deniro','123')
insert into user_role(user_id, role_id) values (3,3)

