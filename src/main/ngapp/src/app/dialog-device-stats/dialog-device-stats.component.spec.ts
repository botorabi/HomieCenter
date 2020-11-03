import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DialogDeviceStatsComponent } from './dialog-device-stats.component';
import {MaterialModule} from "../material.module";
import {ChartsModule} from "ng2-charts";
import {BarChartComponentComponent} from "../charts/bar-chart-component/bar-chart-component.component";

describe('DialogDeviceStatsComponent', () => {
  let component: DialogDeviceStatsComponent;
  let fixture: ComponentFixture<DialogDeviceStatsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        DialogDeviceStatsComponent,
        BarChartComponentComponent
      ],
      imports: [
        MaterialModule,
        ChartsModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeviceStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
