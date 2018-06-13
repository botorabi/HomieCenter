import {Component, OnInit} from '@angular/core';
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-home',
  templateUrl: './view-home.component.html',
  styleUrls: ['./view-home.component.css']
})
export class ViewHomeComponent implements OnInit {

  constructor(public appInfo: AppInformationService) {
  }

  ngOnInit() {
  }
}
