import {Component, OnInit} from '@angular/core';
import {Camera} from "../service/camera";
import {Router} from "@angular/router";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";

@Component({
  selector: 'app-view-camera',
  templateUrl: './view-camera.component.html',
  styleUrls: ['./view-camera.component.css']
})
export class ViewCameraComponent implements OnInit {

  cameras: Array<Camera>;
  error: string;

  constructor(private router: Router,
              private appInfo: AppInformationService,
              private apiDeviceService: ApiDeviceService) {
  }

  ngOnInit() {
    this.apiDeviceService.cameraList((cameras: Array<Camera>, error: string) => {
      this.cameras = cameras;
    });
  }

  onDetails(camera: Camera) {
    this.appInfo.setSelectedCamera(camera);
    this.router.navigate(["camera-details"]);
  }

  onEdit(camera: Camera) {
    this.appInfo.setSelectedCamera(camera);
    this.router.navigate(["camera-edit"]);
  }

  onToggleIFrameSize(cameraId: string) {
    let iframeId = "camera-iframe-" + cameraId;
    let iframe: HTMLIFrameElement = <HTMLIFrameElement>document.getElementById(iframeId);
    if (!iframe) {
      console.log("ERROR: could not find iframe " + iframeId);
      return;
    }
    if (iframe.height == "200") {
      iframe.height = iframe.width = "400";
    }
    else {
      iframe.height = iframe.width = "200";
    }
  }
}
