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
              public appInfoService: AppInformationService,
              private apiDeviceService: ApiDeviceService) {
  }

  ngOnInit() {
    this.apiDeviceService.cameraList((cameras: Array<Camera>, error: string) => {
      this.cameras = cameras;
      if (this.cameras) {
        this.appInfoService.setCameraCount(this.cameras.length);
      }
    });
    this.appInfoService.refreshLogoutTimer();
  }

  onDetails(camera: Camera) {
    if (this.getCameraLink(camera.urlTag) == "") {
      return;
    }
    this.appInfoService.setSelectedCamera(camera);
    this.router.navigate(["/nav/camera-details"]);
  }

  onEdit(camera: Camera) {
    this.appInfoService.setSelectedCamera(camera);
    this.router.navigate(["/nav/camera-edit"]);
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
    let width = 0;
    let height = 0;
    let doc = iframe.contentDocument.documentElement;
    if (iframe.height == "200") {
      if (doc.scrollWidth > doc.scrollHeight) {
        width = 640;
        height = 480;
      }
      else {
        width = 480;
        height = 640;
      }
    }
    else {
      height = width = 200;
    }
    this.adaptIFrameSize(iframe, width, height);
  }

  private adaptIFrameSize(iFrame: HTMLIFrameElement, width: number, height: number) : void {
    let windowWidth = window.innerWidth * 0.8;
    let scale = 1.0;
    if (windowWidth < width) {
      scale = windowWidth / width;
    }
    iFrame.width = '' + Math.floor(width * scale);
    iFrame.height = '' + Math.floor(height * scale);
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
    let thiz = this;
    iframe.onload = function() {
      thiz.onToggleIFrameSize(camera);
      thiz.onToggleIFrameSize(camera);
    }
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
