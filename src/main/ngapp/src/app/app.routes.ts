import {Routes} from "@angular/router";
import {ViewAboutComponent} from "./view-about/view-about.component";
import {ViewHomeComponent} from "./view-home/view-home.component";
import {ViewAuthenticationComponent} from "./view-authentication/view-authentication.component";
import {ViewCameraDetailsComponent} from "./view-camera-details/view-camera-details.component";
import {ViewCameraEditComponent} from "./view-camera-edit/view-camera-edit.component";
import {ViewUserComponent} from "./view-user/view-user.component";
import {ViewUserEditComponent} from "./view-user-edit/view-user-edit.component";

export const appRoutes: Routes = [
  { path: 'about', component: ViewAboutComponent },
  { path: 'login', component: ViewAuthenticationComponent },
  { path: 'camera-edit', component: ViewCameraEditComponent },
  { path: 'camera-details', component: ViewCameraDetailsComponent },
  { path: 'user', component: ViewUserComponent },
  { path: 'user-edit/:id', component: ViewUserEditComponent },
  { path: '', component: ViewHomeComponent }
];
