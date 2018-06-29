import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ApiAuthService} from "../service/api-auth.service";
import {AppInformationService} from "../service/app-information.service";
import {User} from "../service/user";

@Component({
  selector: 'app-view-login',
  templateUrl: './view-authentication.component.html',
  styleUrls: ['./view-authentication.component.css']
})
export class ViewAuthenticationComponent implements OnInit {

  error: string;
  credentials = {login: '', password: ''};

  constructor(private appInfo: AppInformationService,
              private apiAuthService: ApiAuthService,
              private router: Router) {
  }

  ngOnInit() {
  }

  login() {
    this.appInfo.setUser(null);
    this.router.navigate(['/login']);
    this.error = null;
    return false;
  }
}
