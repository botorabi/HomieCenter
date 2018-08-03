import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-login',
  templateUrl: './view-authentication.component.html',
  styleUrls: ['./view-authentication.component.css']
})
export class ViewAuthenticationComponent implements OnInit {

  error: string;
  credentials = {login: '', password: ''};

  constructor(private appInfoService: AppInformationService,
              private apiAuthService: ApiUserService,
              private router: Router) {
  }

  ngOnInit() {
  }

  onLogin() {
    this.appInfoService.setUserStatus(null);
    this.router.navigate(['/nav/login']);
    this.error = null;
    return false;
  }
}
