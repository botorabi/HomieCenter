import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewUserEditComponent } from './view-user-edit.component';

describe('ViewUserEditComponent', () => {
  let component: ViewUserEditComponent;
  let fixture: ComponentFixture<ViewUserEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewUserEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewUserEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
