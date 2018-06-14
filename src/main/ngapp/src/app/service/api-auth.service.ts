import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "./user";

@Injectable()
export class ApiAuthService {

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'withCredentials': 'true'})
  };

  constructor(
    private http: HttpClient) {
  }

  public authenticate(credentials: any, callback: any) {
    this.http.post('/user/login', JSON.stringify(credentials), this.httpOptions)
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
    this.http.get('/user/logout')
      .subscribe(response => {
        if (callback) {
          callback();
        }
      });
  }

  public getStatus(callback: any) {
    this.http.get('/user/status')
      .subscribe(response => {
        let user = this.createUser(response);
        if (callback) {
          callback(user);
        }
      });
  }

  private createUser(userJson: any): User {
    let user = new User();
    user.name = userJson.name;
    user.authenticated = userJson.authenticated;
    user.loginTime = new Date();
    return user;
  }
}
