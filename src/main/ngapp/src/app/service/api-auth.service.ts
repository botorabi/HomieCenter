import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {DOCUMENT} from "@angular/common";
import {User} from "./user";

@Injectable()
export class ApiAuthService {

  readonly location;
  readonly port = 8080;

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'withCredentials': 'true'})
  };

  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) private document) {
    this.location = document.location.protocol + '//' + this.document.location.hostname + ':' + this.port;
  }

  public authenticate(credentials: any, callback: any) {
    this.http.post(this.location + '/user/login', JSON.stringify(credentials), this.httpOptions)
      .subscribe(
        response => {
          let user = this.createUser(response);
          if (callback) {
            callback(user, null);
          }
        },
        error => {
          if (callback) {
            callback(null, error.status);
          }
        });
  }

  public logout(callback: any) {
    this.http.get(this.location + '/user/logout')
      .subscribe(response => {
        if (callback) {
          callback();
        }
      });
  }

  public getStatus(callback: any) {
    this.http.get(this.location + '/user/status')
      .subscribe(response => {
        let user = this.createUser(response);
        if (callback) {
          callback(user);
        }
      });
  }

  private createUser(userJson: any): User {
    let user = new User();
    user.authenticated = userJson.authenticated;
    user.loginTime = new Date();
    return user;
  }
}
