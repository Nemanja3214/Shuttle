import {AfterViewInit, Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {PanicDTO, Ride} from "../../../ride/ride.service";
import * as L from "leaflet";
import {User} from "../../../services/register/register.service";

@Component({
  selector: 'app-admin-panic-details',
  templateUrl: './admin-panic-details.component.html',
  styleUrls: ['./admin-panic-details.component.css']
})
export class AdminPanicDetailsComponent implements AfterViewInit, OnChanges{
    @Input()
    selectedPanic: PanicDTO | undefined;

    private map!: L.Map;
    private route: L.Routing.Control | null = null;
    protected selectedUser: User | null = null;
    protected selectedRide: Ride | null = null;

    @ViewChild('leafletMap')
    private mapElement!: ElementRef;

    ngAfterViewInit(): void {
        this.initMap("map");
    }

    ngOnDestroy(): void {
        if (this.map) {
            this.map = this.map.remove();
        }
    }


    ngOnChanges(changes: SimpleChanges): void {

    }


    protected onRideSelected(row: Ride): void {
        if (this.selectedRide != row) {
            this.selectedRide = row;
            this.drawRouteFrom(this.selectedRide);
        }
    }

    private initMap(id: string): void {
        this.map = L.map(this.mapElement.nativeElement, {center: [45.2396, 19.8227], zoom: 13 });
        const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18, minZoom: 3,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        });
        tiles.addTo(this.map);
    }

    private clearRoute(): void {
        if (this.route != null) {
            this.map?.removeControl(this.route);
        }
        this.route = null;
    }

    private drawRouteFrom(ride: Ride) {
        const A_loc = ride.locations[0].departure;
        const B_loc = ride.locations.at(-1)!.destination;
        const A: L.LatLng = new L.LatLng(A_loc.latitude, A_loc.longitude);
        const B: L.LatLng = new L.LatLng(B_loc.latitude, B_loc.longitude);
        this.drawRoute(A, B);
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

}
