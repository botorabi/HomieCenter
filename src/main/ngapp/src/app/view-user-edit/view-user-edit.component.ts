import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../service/user";
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-user-edit',
  templateUrl: './view-user-edit.component.html',
  styleUrls: ['./view-user-edit.component.css']
})
export class ViewUserEditComponent implements OnInit {

  user = new User();
  passwordRepeat: string;
  sub: any;
  error: string;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private apiUserService: ApiUserService,
              public appInfoService: AppInformationService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      let userId = +params['id'];
      if (userId != 0) {
        this.getUserDetails(userId);
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  onApply() {
    if (this.user.password != this.passwordRepeat) {
      this.error = "Passwords mismatch!";
      return;
    }

    this.error = null;
    this.apiUserService.editUser(this.user, (user: User) => {
      if (user == null) {
        this.error = "Could not update user!";
      }
      else {
        this.user = user;
        this.router.navigate(["/user"]);
      }
    });
  }

  onCreate() {
    if (this.user.password != this.passwordRepeat) {
      this.error = "Passwords mismatch!";
      return;
    }
    this.error = null;
    this.apiUserService.createUser(this.user, (user: User) => {
      if (user == null) {
        this.error = "Could not create user! You may want to try a different login name.";
      }
      else {
        this.user = user;
        this.router.navigate(["/user"]);
      }
    });
  }

  canModifyAdminFlag() {
      return (this.appInfoService.userStatus.role == 'ADMIN') && (this.appInfoService.userStatus.name != this.user.userName);
  }

  private getUserDetails(userId: number) {
    this.apiUserService.getUser(userId, (user: User) => this.user = user);
  }
}
