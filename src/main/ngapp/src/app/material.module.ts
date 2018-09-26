import {NgModule} from '@angular/core';
import {
  MatBadgeModule,
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatDialogModule,
  MatExpansionModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatSlideToggleModule,
  MatStepperModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule
} from "@angular/material";
import {ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {animate, style, transition, trigger} from "@angular/animations";

const modules = [
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatToolbarModule,
  MatInputModule,
  MatDatepickerModule,
  MatMenuModule,
  MatNativeDateModule,
  MatListModule,
  MatTableModule,
  MatTabsModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatIconModule,
  MatDialogModule,
  MatExpansionModule,
  MatBadgeModule,
  MatStepperModule,
  MatSlideToggleModule,
  ReactiveFormsModule,
  BrowserAnimationsModule
];

@NgModule({
  imports: [modules],
  exports: [modules]
})
export class MaterialModule { }

export const AnimationRotation = trigger(
  'iconRotation',
  [
    transition('void => *', [
      style({transform: 'rotate(-3600deg)'}),
      animate('17500ms ease-out')
    ]),
    transition('* => void', animate('1ms ease-in'))
  ]
);

export const AnimationWidgetSpan = trigger(
  'widgetSpan',
  [
    transition('void => *', [
      style({height: '0'}),
      animate(150, style({height: '*'}))
    ]),
    transition('* => void', [
      style({height: '*'}),
      animate(250, style({height: '0'}))
    ])
  ]
);
