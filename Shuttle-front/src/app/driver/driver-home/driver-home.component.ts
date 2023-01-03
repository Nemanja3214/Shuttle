import { AfterViewInit, Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { NavbarService } from 'src/app/navbar-module/navbar.service';
import { PanicDTO, Ride, RideService, RideStatus } from 'src/app/ride/ride.service';
import { SharedService } from 'src/app/shared/shared.service';
import { VehicleLocationDTO } from 'src/app/vehicle/vehicle.service';

@Component({
    selector: 'app-driver-home',
    templateUrl: './driver-home.component.html',
    styleUrls: ['./driver-home.component.css']
})
export class DriverHomeComponent implements OnInit, AfterViewInit {
    private iconCarAvailable!: L.Icon;
    private iconCarBusy!: L.Icon;
    private iconLuxAvailable!: L.Icon;
    private iconLuxBusy!: L.Icon;
    private iconVanAvailable!: L.Icon;
    private iconVanBusy!: L.Icon;

    private map!: L.Map;
    private route: L.Routing.Control | null = null;
    private carLayer!: L.LayerGroup;

    protected ride: Ride | null = null;

    /****************************************** General ******************************************/

    constructor(private navbarService: NavbarService,
                private sharedService: SharedService,
                private rideService: RideService) {
    }

    ngOnInit(): void {
        this.subscribeToSocketSubjects();
    }
    
    ngAfterViewInit(): void {
        this.initMap("map");
        this.initMapIcons();
    }

    private subscribeToSocketSubjects(): void {
        this.navbarService.getVehicleLocation().subscribe({
            next: (value: VehicleLocationDTO) => this.onFetchCurrentLocation(value),
            error: (error) => console.log(error)
        });

        this.navbarService.getRideDriver().subscribe({
            next: (value: Ride) => this.onFetchRide(value),
            error: (error) => console.log(error)          
        });
    }

    /******************************************** Map ********************************************/

    private initMap(id: string): void {
        this.map = L.map(id, {center: [45.2396, 19.8227], zoom: 13 });
        const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18, minZoom: 3,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        });
        tiles.addTo(this.map);
    }

    private initMapIcons(): void {
        this.iconCarAvailable = L.icon({
            iconUrl: 'assets/ico_car_avail.png',
            iconSize: [32, 32],
        });

        this.iconCarBusy = L.icon({
            iconUrl: 'assets/ico_car_busy.png',
            iconSize: [32, 32],
        });

        this.iconLuxAvailable = L.icon({
            iconUrl: 'assets/ico_luxury_avail.png',
            iconSize: [32, 32],
        });

        this.iconLuxBusy = L.icon({
            iconUrl: 'assets/ico_luxury_busy.png',
            iconSize: [32, 32],
        });

        this.iconVanAvailable = L.icon({
            iconUrl: 'assets/ico_van_avail.png',
            iconSize: [32, 32],
        });

        this.iconVanBusy = L.icon({
            iconUrl: 'assets/ico_van_busy.png',
            iconSize: [32, 32],
        });
    }

    private onFetchCurrentLocation(vehicle: VehicleLocationDTO): void {
        const loc: L.LatLng = new L.LatLng(vehicle.location.latitude, vehicle.location.longitude);

        if (this.map.getBounds().contains(loc)) {
            this.map.flyTo(loc);
        }
        this.drawMarker(vehicle);
    }

    private drawMarker(vehicle: VehicleLocationDTO) {
        const icon_map = [
            [this.iconCarAvailable, this.iconLuxAvailable, this.iconVanAvailable],
            [this.iconCarBusy, this.iconLuxBusy, this.iconVanBusy],
        ];
        const ico = icon_map[vehicle.available ? 0 : 1][vehicle.vehicleTypeId - 1]; 
        const marker: L.Marker = L.marker(
            [vehicle.location.latitude, vehicle.location.longitude],
            {icon: ico}
        );
        
        if (this.map.hasLayer(this.carLayer)) {
            this.map.removeLayer(this.carLayer);
        }
        this.carLayer = new L.LayerGroup([marker]);
        this.map.addLayer(this.carLayer);
    }

    private drawRoute(A: L.LatLng, B: L.LatLng) {
        const waypoints = [A, B];

        this.clearRoute();
        this.route = L.Routing.control({
            waypoints: waypoints,
            collapsible: true,
            fitSelectedRoutes: true,
            routeWhileDragging: false,
            plan: L.Routing.plan(waypoints, { draggableWaypoints: false, addWaypoints: false }),
            lineOptions:
            {
                missingRouteTolerance: 0,
                extendToWaypoints: true,
                addWaypoints: false
            },
        }).addTo(this.map);
        this.route.hide();
    }

    private clearRoute(): void {
        if (this.route != null) {
            this.map?.removeControl(this.route);
        }
        this.route = null;
    }

    private hasRouteOnMap(): boolean {
        return this.route != null;
    }

    /******************************************** Ride********************************************/

    private onFetchRide(ride: Ride): void {
        // If the ride is cancelled/withdrawn/completed -> set this.ride to null.
        // Because we want to see the right panel ONLY for pending/active rides.

        console.log(ride);

        if ([RideStatus.Canceled, RideStatus.Rejected, RideStatus.Finished].includes(ride.status)) {
            this.ride = null;
            this.clearRoute();
            return;
        }

        // If the current ride is 'Accepted', ignore the upcoming ride (which can only be Pending).
        // The reason for this is that we don't want to bother the driver while he's working. Once
        // he finishes the current ride, he'll ask the backend to fetch again, and then he'll get
        // the other ride.

        if (this.ride && [RideStatus.Accepted].includes(this.ride.status)) {
            return;
        }
        
        this.ride = ride;

        if (!this.hasRouteOnMap()) {
            const A = this.ride.locations[0].departure;
            const B = this.ride.locations.at(-1)!.destination;

            const pointA = L.latLng(A.latitude, A.longitude);
            const pointB = L.latLng(B.latitude, B.longitude);

            this.drawRoute(pointA, pointB);
        }

        if (this.ride.status == RideStatus.Accepted) {
            this.navbarService.setCanDriverChangeActiveState(false);
            this.navbarService.setDriverActiveState(true);        
        }
    }

    protected hasCurrentRide(): boolean {
        return this.ride != null;
    }

    protected onRideBegin(): void {
        if (!this.ride) {
            return;
        }
   
        this.rideService.accept(this.ride.id).subscribe({
            next: (ride: Ride) => {
                this.sharedService.showSnackBar("Ride started.", 3000);
                this.onFetchRide(ride);
                this.navbarService.setCanDriverChangeActiveState(false);
                this.navbarService.setDriverActiveState(true);
            },
            error: (error) => this.sharedService.showSnackBar("Cannot begin ride.", 3000)
        });
    }

    protected onRideFinish(): void {
        if (!this.ride) {
            return;
        }
   
        this.rideService.end(this.ride.id).subscribe({
            next: (ride: Ride) => {
                this.sharedService.showSnackBar("Ride finished.", 3000);
                this.onFetchRide(ride);
                this.navbarService.driverRequestToFetchRide();
                this.navbarService.setCanDriverChangeActiveState(true);
            },
            error: (error) => this.sharedService.showSnackBar("Cannot finish ride.", 3000)
        });
    }

    protected onRideReject(reason: string): void {
        if (!this.ride) {
            return;
        }
   
        this.rideService.reject(this.ride.id, reason).subscribe({
            next: (ride: Ride) => {
                this.sharedService.showSnackBar("Ride rejected.", 3000);
                this.onFetchRide(ride);
                this.navbarService.driverRequestToFetchRide();
                this.navbarService.setCanDriverChangeActiveState(true);
            },
            error: (error) => this.sharedService.showSnackBar("Cannot reject ride.", 3000)
        });
    }
    
    protected onRidePanic(reason: string): void {
        if (!this.ride) {
            return;
        }
   
        this.rideService.panic(this.ride.id, reason).subscribe({
            next: (panicDTO: PanicDTO) => {
                this.sharedService.showSnackBar("Ride aborted. The staff has been notified", 3000);
                this.onFetchRide(panicDTO.ride);
                this.navbarService.driverRequestToFetchRide();
                this.navbarService.setCanDriverChangeActiveState(true);
            },
            error: (error) => this.sharedService.showSnackBar("Cannot panic ride.", 3000)
        });
    }
}
