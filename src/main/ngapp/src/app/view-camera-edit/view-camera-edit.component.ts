import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ApiDeviceService} from "../service/api-device.service";
import {Camera} from "../service/camera";
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-camera-edit',
  templateUrl: './view-camera-edit.component.html',
  styleUrls: ['./view-camera-edit.component.css']
})
export class ViewCameraEditComponent implements OnInit {

  camera: Camera;
  error: string;

  constructor(private router: Router,
              private appInfo: AppInformationService,
              private apiDeviceService: ApiDeviceService) {
  }

  ngOnInit() {
    if (!this.appInfo.selectedCamera) {
      this.camera = new Camera("", "", "", "", "");
    }
    else {
      this.camera = this.appInfo.selectedCamera;
    }
  }

  onApply() {
    this.apiDeviceService.cameraCreateOrUpdate(this.camera,(camera: Camera, error: string) => {
      if (error) {
        this.error = error;
      }
      else
        this.router.navigate([""]);
      });
  }

  onDelete() {
    this.apiDeviceService.cameraDelete(this.camera, (success: boolean) => {
      if (!success) {
        this.error = "Could not delete Camera!";
      }
      else
        this.router.navigate([""]);
    });
  }
}
