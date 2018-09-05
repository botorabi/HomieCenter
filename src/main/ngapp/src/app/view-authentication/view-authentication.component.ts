import {Component, OnInit} from '@angular/core';
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-view-login',
  templateUrl: './view-authentication.component.html',
  styleUrls: ['./view-authentication.component.css']
})
export class ViewAuthenticationComponent implements OnInit {

  error: string;
  credentials = {login: '', password: ''};

  constructor(private appInfoService: AppInformationService,
              private apiUserService: ApiUserService,
              private router: Router) {
  }

  ngOnInit() {
  }

  onLogin() {
    if (this.credentials.login.length === 0 ||
      this.credentials.password.length === 0) {
      this.error = 'Please enter a valid user name and password.';
      return;
    }

    this.appInfoService.setUserStatus(null);
    this.error = null;
    this.apiUserService.login(this.credentials.login, this.credentials.password, (userStatus) => {
      this.appInfoService.setUserStatus(userStatus);
      if (userStatus && userStatus.authenticated) {
        this.router.navigate(['/']);
      }
      else {
        this.error = "Invalid Credentials!";
        this.credentials.password = '';
      }
    });
  }
}
