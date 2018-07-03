import {Component} from '@angular/core';
import {AppInformationService} from "./service/app-information.service";
import {ApiUserService} from "./service/api-user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(public appInfoService: AppInformationService,
              private apiAuthService: ApiUserService,
              private router: Router) {
  }

  onHome() {
    this.appInfoService.setSelectedCamera(null);
    window.location.href = "/";
  }

  onLogin() {
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(["login"]);
  }

  onLogout() {
    window.location.href = "/logout";
  }

  onCreateCamera() {
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(["camera-edit"]);
  }

  onAbout() {
    this.router.navigate(["about"]);
  }

  onUsers() {
    this.router.navigate(["user"]);
  }
}
