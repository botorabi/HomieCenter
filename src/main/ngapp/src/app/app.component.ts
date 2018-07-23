import {Component, OnInit} from '@angular/core';
import {AppInformationService} from "./service/app-information.service";
import {ApiUserService} from "./service/api-user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(public appInfoService: AppInformationService,
              private apiUserService: ApiUserService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.appInfoService.refreshLogoutTimer();
  }

  getMenuTitle() : string {
    let currentRoute = this.router.url;
    if (currentRoute.startsWith('/')) {
      currentRoute = currentRoute.substr(1);
    }
    if (currentRoute.startsWith('user-edit')) {
      return 'Edit User';
    }

    let title = '';
    switch(currentRoute) {
      case '':
        title = 'Homie Center';
        break;
      case 'login':
        title = 'Login';
        break;
      case 'about':
        title = 'About';
        break;
      case 'camera-edit':
        title = 'Edit Camera';
        break;
      case 'camera-details':
        title = 'Camera' + (this.appInfoService.selectedCamera ? ': ' + this.appInfoService.selectedCamera.name : '');
        break;
      case 'user':
        title = 'Users';
        break;
    }
    return title;
  }

  onHome() {
    this.appInfoService.refreshLogoutTimer();
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(['']);
  }

  onLogin() {
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(['login']);
  }

  onLogout() {
    this.apiUserService.logout(null);
    window.location.href = '/';
  }

  onCreateCamera() {
    this.appInfoService.refreshLogoutTimer();
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(['camera-edit']);
  }

  onAbout() {
    this.appInfoService.refreshLogoutTimer();
    this.router.navigate(['about']);
  }

  onUsers() {
    this.appInfoService.refreshLogoutTimer();
    this.router.navigate(['user']);
  }
}
