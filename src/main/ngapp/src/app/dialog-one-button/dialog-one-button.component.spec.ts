import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DialogOneButtonComponent } from './dialog-one-button.component';
import {MaterialModule} from "../material.module";

describe('DialogOneButtonComponent', () => {
  let component: DialogOneButtonComponent;
  let fixture: ComponentFixture<DialogOneButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogOneButtonComponent ],
      imports: [
        MaterialModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogOneButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
