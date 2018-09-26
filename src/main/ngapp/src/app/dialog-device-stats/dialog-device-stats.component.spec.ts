import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeviceStatsComponent } from './dialog-device-stats.component';

describe('DialogDeviceStatsComponent', () => {
  let component: DialogDeviceStatsComponent;
  let fixture: ComponentFixture<DialogDeviceStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeviceStatsComponent ]
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
