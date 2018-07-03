import {Injectable} from '@angular/core';
import {UserStatus} from "./user-status";
import {ApiUserService} from "./api-user.service";
import {Camera} from "./camera";

@Injectable()
export class AppInformationService {

  name: string = "Homie Center";
  version: string = "0.5.0";

  userStatus: UserStatus;
  logoutTime: string;
  selectedCamera: Camera;
  switchDeviceCount = 0;
  cameraCount = 0;
  camerasExpanded = true;
  switchDevicesExpanded = true;

  private LogoutTimeoutSec = 30 * 60;

  constructor(private apiAuthService: ApiUserService) {
    this.logoutTime = this.formatTime(this.LogoutTimeoutSec);
    this.apiAuthService.getStatus((userStatus: UserStatus) => {
      if (userStatus.authenticated) {
        this.setUserStatus(userStatus);
      }
      else {
        this.setUserStatus(null);
      }
    });
  }

  public setUserStatus(userStatus: UserStatus) {
    this.userStatus = userStatus;
    this.refreshLogoutTimer();
  }

  public setSelectedCamera(camera: Camera) {
    this.selectedCamera = camera;
  }

  public setCameraCount(count: number) {
    this.cameraCount = count;
  }

  public setSwitchDeviceCount(count: number) {
    this.switchDeviceCount = count;
  }

  private refreshLogoutTimer() {
    this.updateLogoutTimer(this.userStatus);
  }

  private updateLogoutTimer(userStatus: UserStatus) {
    if (userStatus) {
      let timeDiff = Date.now() - userStatus.loginTime.getTime();
      let timeRemaining = this.LogoutTimeoutSec - timeDiff / 1000;
      window.setTimeout(() => {
        if (timeRemaining < 0) {
          window.location.href = "/logout";
        }
        else {
          this.logoutTime = this.formatTime(timeRemaining);
          this.updateLogoutTimer(this.userStatus);
        }
      }, 10000);
    }
  }

  private formatTime(timeRemaining: number) {
    let seconds = Math.floor(timeRemaining);
    let hours = Math.floor(seconds / 3600);
    let minutes = Math.floor((seconds - (hours * 3600)) / 60);
    //let seconds = Math.floor(seconds) % 60;
    return "" + this.formatTimeDigits(minutes) + " min";
  }

  private formatTimeDigits(value: number) {
    return "" + ((value < 9) ? ("0" + value) : value);
  }
}
