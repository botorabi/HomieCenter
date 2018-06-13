import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSwitchDevicesComponent } from './view-switch-devices-component';

describe('ViewSwitchDevicesComponent', () => {
  let component: ViewSwitchDevicesComponent;
  let fixture: ComponentFixture<ViewSwitchDevicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewSwitchDevicesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewSwitchDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
