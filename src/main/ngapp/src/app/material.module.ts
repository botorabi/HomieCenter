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
  MatProgressSpinnerModule,
  MatStepperModule,
  MatSlideToggleModule,
  MatTableModule,
  MatToolbarModule
} from "@angular/material";
import {ReactiveFormsModule} from "@angular/forms";

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
  MatPaginatorModule,
  MatProgressSpinnerModule,
  MatIconModule,
  MatDialogModule,
  MatExpansionModule,
  MatBadgeModule,
  MatStepperModule,
  MatSlideToggleModule,
  ReactiveFormsModule];

@NgModule({
  imports: [modules],
  exports: [modules]
})
export class MaterialModule { }
