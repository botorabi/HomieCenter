import {Routes} from "@angular/router";
import {ViewAboutComponent} from "./view-about/view-about.component";
import {ViewHomeComponent} from "./view-home/view-home.component";
import {ViewAuthenticationComponent} from "./view-authentication/view-authentication.component";
import {ViewCameraDetailsComponent} from "./view-camera-details/view-camera-details.component";
import {ViewCameraEditComponent} from "./view-camera-edit/view-camera-edit.component";
import {ViewUserComponent} from "./view-user/view-user.component";
import {ViewUserEditComponent} from "./view-user-edit/view-user-edit.component";
import {ViewErrorPageComponent} from "./view-error-page/view-error-page.component";

export const appRoutes: Routes = [
  { path: 'nav/about', component: ViewAboutComponent },
  { path: 'nav/login', component: ViewAuthenticationComponent },
  { path: 'nav/camera-edit', component: ViewCameraEditComponent },
  { path: 'nav/camera-details', component: ViewCameraDetailsComponent },
  { path: 'nav/user', component: ViewUserComponent },
  { path: 'nav/user-edit/:id', component: ViewUserEditComponent },
  { path: 'nav/error', component: ViewErrorPageComponent },
  { path: '', component: ViewHomeComponent },
  { path: '**', component: ViewErrorPageComponent, data: { error: 404 } }
];
