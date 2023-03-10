import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Driver, DriverService } from 'src/app/driver/driver.service';
import { Vehicle, VehicleService, VehicleType } from 'src/app/vehicle/vehicle.service';

@Component({
    selector: 'app-create-driver',
    templateUrl: './create-driver.component.html',
    styleUrls: ['./create-driver.component.css']
})
export class CreateDriverComponent implements OnInit, OnDestroy {
    vehicleTypes: Array<VehicleType> = [];
    seatsPossible: Array<Number> = [1, 2, 3, 4, 5, 6, 7, 8];
    formGroup: FormGroup;

    ngOnInit(): void {
        document.body.className = "body-gradient1"; // Defined in src/styles.css

        this.vehicleService.getTypes().subscribe({
            next: res => {
                this.vehicleTypes = res;
            }
        });
    }

    ngOnDestroy() {
        document.body.className = "";
    }

    constructor(private readonly formBuilder: FormBuilder, private driverService: DriverService, private vehicleService: VehicleService) {
        this.formGroup = this.formBuilder.group({
            name: ['', [Validators.required]],
            surname: ['', [Validators.required]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required]],
            phone: ['', [Validators.required, Validators.pattern('[0-9]{3}-[0-9]{3}-[0-9]{4}')]],
            address: ['', [Validators.required]],
            profilePicture: [],

            vehicleModel: ['', [Validators.required]],
            vehicleType: ['', [Validators.required]],
            vehicleRegtable: ['', [Validators.required]],
            vehicleSeats: ['', [Validators.required]],
            vehicleBabies: [false],
            vehiclePets: [false],
        });
    }

    createDriver(): void {
        if (this.formGroup.valid) {
            let result = this.formGroup.value;

            const driver: Driver = {
                name: result.name,
                surname: result.surname,
                profilePicture: result.profilePicture,
                telephoneNumber: result.phone,
                address: result.address,
                email: result.email,
                password: result.password
            };
            let vehicle: Vehicle = {
                vehicleType: result.vehicleType,
                model: result.vehicleModel,
                licenseNumber: result.vehicleRegtable,
                passengerSeats: result.vehicleSeats,
                babyTransport: result.vehicleBabies,
                petTransport: result.vehiclePets
            }

            const resultDriver: Observable<Object> = this.driverService.add(driver);
            resultDriver.subscribe(response => {
                console.log(response);
                vehicle.driverId = (response as Driver).id;

                const resultVehicle: Observable<Object> = this.vehicleService.add(vehicle);
                resultVehicle.subscribe(response => console.log(response));
            });
        }
    }
}
