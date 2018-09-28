import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BarChartComponentComponent } from './bar-chart-component.component';
import {MaterialModule} from "../../material.module";
import {ChartsModule} from "ng2-charts";

describe('BarChartComponentComponent', () => {
  let component: BarChartComponentComponent;
  let fixture: ComponentFixture<BarChartComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BarChartComponentComponent ],
      imports: [
        MaterialModule,
        ChartsModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BarChartComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
