import {AfterContentInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-view-login',
  templateUrl: './view-authentication.component.html',
  styleUrls: ['./view-authentication.component.css']
})
export class ViewAuthenticationComponent implements OnInit, AfterContentInit {

  error: string;
  credentials = {login: '', password: ''};
  @ViewChild('userLogin', { static: true }) userLogin: ElementRef;

  constructor(private appInfoService: AppInformationService,
              private apiUserService: ApiUserService,
              private router: Router) {
  }

  ngOnInit() {
  }

  ngAfterContentInit() {
    this.userLogin.nativeElement.focus();
  }

  onLogin() {
    if (this.credentials.login.length === 0 ||
      this.credentials.password.length === 0) {
      this.error = 'Please enter a valid user name and password.';
      return;
    }

    this.error = null;
    this.appInfoService.setUserStatus(null);
    this.apiUserService.login(this.credentials.login.toLowerCase(), this.credentials.password, (userStatus) => {
      if (userStatus && userStatus.authenticated) {
        setTimeout(() => {
          this.appInfoService.setUserStatus(userStatus);
          this.router.navigate(['/']);
        }, 500);
      }
      else {
        this.error = "Invalid Credentials!";
        this.credentials.password = '';
      }
    });
  }
}
