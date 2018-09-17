import {Component, OnInit} from '@angular/core';
import {AppInformationService} from "../service/app-information.service";
import {ApiUserService} from "../service/api-user.service";

@Component({
  selector: 'app-view-home',
  templateUrl: './view-home.component.html',
  styleUrls: ['./view-home.component.css']
})
export class ViewHomeComponent implements OnInit {

  constructor(public appInfoService: AppInformationService,
              private apiUserService: ApiUserService) {
  }

  ngOnInit() {
    this.apiUserService.getStatus((userStatus) => {
      this.appInfoService.setUserStatus(userStatus);
    });
  }
}
