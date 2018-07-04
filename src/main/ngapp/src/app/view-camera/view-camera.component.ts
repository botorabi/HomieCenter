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
              private appInfoService: AppInformationService,
              private apiDeviceService: ApiDeviceService) {
  }

  ngOnInit() {
    this.apiDeviceService.cameraList((cameras: Array<Camera>, error: string) => {
      this.cameras = cameras;
      if (this.cameras) {
        this.appInfoService.setCameraCount(this.cameras.length);
      }
    });
  }

  onDetails(camera: Camera) {
    if (this.getCameraLink(camera.urlTag) == "") {
      return;
    }
    this.appInfoService.setSelectedCamera(camera);
    this.router.navigate(["camera-details"]);
  }

  onEdit(camera: Camera) {
    this.appInfoService.setSelectedCamera(camera);
    this.router.navigate(["camera-edit"]);
  }

  onToggleIFrameSize(camera: Camera) {
    if (this.getCameraLink(camera.previewUrlTag) == "") {
      return;
    }
    let iframeId = "camera-iframe-" + camera.id;
    let iframe: HTMLIFrameElement = <HTMLIFrameElement>document.getElementById(iframeId);
    if (!iframe) {
      console.log("ERROR: could not find iframe " + iframeId);
      return;
    }
    if (iframe.height == "200") {
      iframe.width = "480";
      iframe.height = "640";
    }
    else {
      iframe.height = iframe.width = "200";
    }
  }

  onRefreshPreview(camera: Camera) {
    if (this.getCameraLink(camera.previewUrlTag) == "") {
      return;
    }
    let iframeId = "camera-iframe-" + camera.id;
    let iframe: HTMLIFrameElement = <HTMLIFrameElement>document.getElementById(iframeId);
    if (!iframe) {
      console.log("ERROR: could not find iframe " + iframeId);
      return;
    }
    iframe.src = camera.previewUrlTag;
  }

  getCameraLink(tag: string) : string {
    // filter out missing paths
    if (tag == "/") {
      return "";
    }
    return tag;
  }

  getCameraName(camera: Camera) {
    let name = camera.name;
    if (name.length > 12) {
      name = name.substr(0, 11) + "...";
    }
    return name;
  }
}
