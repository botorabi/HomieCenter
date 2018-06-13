import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAuthenticationComponent } from './view-authentication.component';

describe('ViewAuthenticationComponent', () => {
  let component: ViewAuthenticationComponent;
  let fixture: ComponentFixture<ViewAuthenticationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewAuthenticationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewAuthenticationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
