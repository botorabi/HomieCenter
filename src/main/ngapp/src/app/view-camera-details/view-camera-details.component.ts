import { Component, OnInit } from '@angular/core';
import {Camera} from "../service/camera";
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-camera-details',
  templateUrl: './view-camera-details.component.html',
  styleUrls: ['./view-camera-details.component.css']
})
export class ViewCameraDetailsComponent implements OnInit {

  camera: Camera;
  error: string;

  constructor(private appInfo: AppInformationService) { }

  ngOnInit() {
    this.camera = this.appInfo.selectedCamera;
  }
}
