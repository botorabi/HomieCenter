import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAboutComponent } from './view-about.component';

describe('ViewAboutComponent', () => {
  let component: ViewAboutComponent;
  let fixture: ComponentFixture<ViewAboutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewAboutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewAboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
