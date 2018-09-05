import {Component, OnInit} from '@angular/core';
import {AppInformationService} from "./service/app-information.service";
import {ApiUserService} from "./service/api-user.service";
import {NavigationEnd, Router} from "@angular/router";
import {UserStatus} from "./service/user-status";

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
    this.apiUserService.getStatus((userStatus: UserStatus) => {
      this.appInfoService.setUserStatus(userStatus);
    });

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.handleNavigation(event.url);
      }
    });
  }

  private handleNavigation(url: string) {
    let userInteraction =
      url.startsWith("/nav/") ||
      url.startsWith("nav/") ||
      url.length == 0 ||
      url == '/';

    if (userInteraction) {
      this.appInfoService.refreshLogoutTimer();
    }
  }

  getMenuTitle() : string {
    let currentRoute = this.router.url;
    if (currentRoute.startsWith('/')) {
      currentRoute = currentRoute.substr(1);
    }
    if (currentRoute.startsWith('nav/user-edit')) {
      return 'Edit User';
    }

    let title = '';
    switch(currentRoute) {
      case '':
        title = 'Homie Center';
        break;
      case 'nav/login':
        title = 'Login';
        break;
      case 'nav/about':
        title = 'About';
        break;
      case 'nav/camera-edit':
        title = 'Edit Camera';
        break;
      case 'nav/camera-details':
        title = 'Camera' + (this.appInfoService.selectedCamera ? ': ' + this.appInfoService.selectedCamera.name : '');
        break;
      case 'nav/user':
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
    this.router.navigate(['/nav/login']);
  }

  onLogout() {
    this.apiUserService.logout(null);
    window.location.href = '/';
  }

  onCreateCamera() {
    this.appInfoService.refreshLogoutTimer();
    this.appInfoService.setSelectedCamera(null);
    this.router.navigate(['/nav/camera-edit']);
  }

  onAbout() {
    this.appInfoService.refreshLogoutTimer();
    this.router.navigate(['/nav/about']);
  }

  onUsers() {
    this.router.navigate(['/nav/user']);
  }
}
