import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewCameraDetailsComponent } from './view-camera-details.component';

describe('ViewCameraDetailsComponent', () => {
  let component: ViewCameraDetailsComponent;
  let fixture: ComponentFixture<ViewCameraDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewCameraDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewCameraDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
