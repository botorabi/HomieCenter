import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ViewErrorPageComponent } from './view-error-page.component';

describe('ViewErrorPageComponent', () => {
  let component: ViewErrorPageComponent;
  let fixture: ComponentFixture<ViewErrorPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewErrorPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewErrorPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
