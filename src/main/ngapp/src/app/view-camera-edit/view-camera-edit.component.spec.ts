import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewCameraEditComponent } from './view-camera-edit.component';

describe('ViewCameraEditComponent', () => {
  let component: ViewCameraEditComponent;
  let fixture: ComponentFixture<ViewCameraEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewCameraEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewCameraEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
