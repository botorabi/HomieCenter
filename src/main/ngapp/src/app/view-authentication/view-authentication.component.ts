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
    this.error = null;
    this.apiAuthService.authenticate(this.credentials, (user: User, error: string) => {
      if (error) {
        if (error == "403") {
          this.error = "Wrong user name or password! Try again.";
        }
        else {
          this.error = "An error occurred! Error code: " + error;
        }
      }
      else if (user.authenticated) {
        this.appInfo.setUser(user);
        this.router.navigate(['']);
      }
    });
    return false;
  }
}
