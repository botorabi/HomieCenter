import {Inject, Injectable} from '@angular/core';
import {DOCUMENT} from "@angular/common";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Device} from "./device";
import {Camera} from "./camera";

@Injectable()
export class ApiDeviceService {

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

  public deviceList(callback: any) {
    this.http.get(this.location + '/device')
      .subscribe(
        (response: Device[]) => {
          let devices = <Array<Device>>(response);
          if (callback) {
            callback(devices, null);
          }
        },
        error => {
          if (callback) {
            callback(null, error.status);
          }
        });
  }

  public deviceSwitch(deviceId: number, on: boolean, callback: any) {
    this.http.get(this.location + '/device/' + deviceId + "/" + (on ? "on" : "off"))
      .subscribe(response => {
        if (callback) {
          callback();
        }
      });
  }

  public cameraList(callback: any) {
    this.http.get(this.location + '/device/camera')
      .subscribe(
        (response: Camera[]) => {
          let cameras = <Array<Camera>>(response);
          if (callback) {
            callback(cameras, null);
          }
        },
        error => {
          if (callback) {
            callback(null, error.status);
          }
        });
  }

  public cameraCreateOrUpdate(camera: Camera, callback: any) {
    this.http.post(this.location + "/device/cameraCreateOrUpdate", JSON.stringify(camera), this.httpOptions).
      subscribe(
        (response: Camera) => {
        if (callback) {
          callback(response, null);
        }
      },
      error => {
        if (callback) {
          callback(null, error.status);
        }
      });
  }

  public cameraDelete(camera: Camera, callback: any) {
    this.http.post(this.location + "/device/cameraDelete/" + camera.id, this.httpOptions).
    subscribe(
      response => {
        if (callback) {
          callback(true);
        }
      },
      error => {
        if (callback) {
          callback(false);
        }
      });
  }
}
