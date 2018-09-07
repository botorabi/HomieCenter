import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Device} from "./device";
import {Camera} from "./camera";

@Injectable()
export class ApiDeviceService {

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'withCredentials': 'true'})
  };

  constructor(
    private http: HttpClient) {
  }

  public deviceList(callback: any) {
    this.http.get('/api/device')
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
    this.http.get('/api/device/' + deviceId + "/" + (on ? "on" : "off"))
      .subscribe(response => {
        if (callback) {
          callback();
        }
      });
  }

  public deviceHeatControllerSetTemperature(deviceId: number, temperature: number, callback: any) {
    this.http.get('/api/device/' + deviceId + "/" + "temperature=" + temperature)
      .subscribe(response => {
        if (callback) {
          callback();
        }
      });
  }

  public cameraList(callback: any) {
    this.http.get('/api/camera')
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
    let create = camera.id == '';
    let restUrl = create ? '/api/camera/create' : '/api/camera/update';
    let observer;
    if (create) {
      observer = this.http.post(restUrl, JSON.stringify(camera), this.httpOptions);
    }
    else {
      observer = this.http.put(restUrl, JSON.stringify(camera), this.httpOptions);
    }
    observer.subscribe(
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
    this.http.post('/api/camera/delete/' + camera.id, this.httpOptions).
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
