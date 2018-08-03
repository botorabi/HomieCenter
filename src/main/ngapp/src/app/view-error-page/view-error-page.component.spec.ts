import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewErrorPageComponent } from './view-error-page.component';

describe('ViewErrorPageComponent', () => {
  let component: ViewErrorPageComponent;
  let fixture: ComponentFixture<ViewErrorPageComponent>;

  beforeEach(async(() => {
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
