import {Component} from '@angular/core';
import {AppInformationService} from "./service/app-information.service";
import {ApiAuthService} from "./service/api-auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(public appInfo: AppInformationService,
              private apiAuthService: ApiAuthService,
              private router: Router) {
  }

  onHome() {
    this.appInfo.setSelectedCamera(null);
    window.location.href = "/";
  }

  onLogin() {
    this.appInfo.setSelectedCamera(null);
    this.router.navigate(["login"]);
  }

  onLogout() {
    this.apiAuthService.logout(() => {
      window.location.href = "/";
    });
  }

  onCreateCamera() {
    this.appInfo.setSelectedCamera(null);
    this.router.navigate(["camera-edit"]);
  }

  onAbout() {
    this.router.navigate(["about"]);
  }
}
