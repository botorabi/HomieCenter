import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DialogTwoButtonsComponent} from './dialog-two-buttons.component';
import {MaterialModule} from "../material.module";

describe('DialogTwoButtonsComponent', () => {
  let component: DialogTwoButtonsComponent;
  let fixture: ComponentFixture<DialogTwoButtonsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogTwoButtonsComponent ],
      imports: [
        MaterialModule
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogTwoButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
