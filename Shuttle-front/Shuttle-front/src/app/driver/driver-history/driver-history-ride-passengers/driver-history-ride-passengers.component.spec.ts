import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverHistoryRidePassengersComponent } from './driver-history-ride-passengers.component';

describe('DriverHistoryRidePassengersComponent', () => {
  let component: DriverHistoryRidePassengersComponent;
  let fixture: ComponentFixture<DriverHistoryRidePassengersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverHistoryRidePassengersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverHistoryRidePassengersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
