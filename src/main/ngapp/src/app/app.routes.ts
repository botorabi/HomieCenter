import {Routes} from "@angular/router";
import {ViewAboutComponent} from "./view-about/view-about.component";
import {ViewHomeComponent} from "./view-home/view-home.component";
import {ViewAuthenticationComponent} from "./view-authentication/view-authentication.component";
import {ViewCameraDetailsComponent} from "./view-camera-details/view-camera-details.component";
import {ViewCameraEditComponent} from "./view-camera-edit/view-camera-edit.component";

export const appRoutes: Routes = [
  { path: 'about', component: ViewAboutComponent },
  { path: 'authentication', component: ViewAuthenticationComponent },
  { path: 'camera-edit', component: ViewCameraEditComponent },
  { path: 'camera-details', component: ViewCameraDetailsComponent },
  { path: '', component: ViewHomeComponent }
];
