import {Injectable} from '@angular/core';
import {UserStatus} from "./user-status";
import {ApiUserService} from "./api-user.service";
import {Camera} from "./camera";

@Injectable()
export class AppInformationService {

  name: string = "Homie Center";
  version: string = "0.9.2";

  userStatus: UserStatus = null;
  logoutTimeString: string = "";
  logoutTimer: Date;
  selectedCamera: Camera = null;
  switchDeviceCount = 0;
  cameraCount = 0;
  camerasExpanded = true;
  switchDevicesExpanded = true;

  private LogoutTimeoutSec = 30 * 60;

  constructor(private apiUserService: ApiUserService) {
    this.refreshLogoutTimer();
  }

  public setUserStatus(userStatus: UserStatus) {
    this.userStatus = userStatus;
    this.periodicLogoutTimerUpdate(userStatus);
  }

  public isUserAuthenticated() : boolean {
    return this.userStatus && this.userStatus.authenticated;
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

  public refreshLogoutTimer() {
    this.logoutTimer = new Date();
    this.logoutTimeString = this.formatTime(this.getRemainingTime());
  }

  private periodicLogoutTimerUpdate(userStatus: UserStatus) {
    if (userStatus) {
      window.setTimeout(() => {
        let timeRemaining = this.getRemainingTime();
        if (timeRemaining < 0) {
          this.apiUserService.logout(null);
          window.location.href = "/";
        }
        else {
          this.logoutTimeString = this.formatTime(timeRemaining);
          this.periodicLogoutTimerUpdate(this.userStatus);
        }
      }, 10000);
    }
  }

  private getRemainingTime() : number {
    let timeDiff = Date.now() - this.logoutTimer.getTime();
    let timeRemaining = this.LogoutTimeoutSec - timeDiff / 1000;
    return timeRemaining;
  }

  private formatTime(timeRemaining: number) : string {
    let seconds = Math.floor(timeRemaining);
    let hours = Math.floor(seconds / 3600);
    let minutes = Math.floor((seconds - (hours * 3600)) / 60);
    //let seconds = Math.floor(seconds) % 60;
    return "" + this.formatTimeDigits(minutes) + " min";
  }

  private formatTimeDigits(value: number) : string {
    return "" + ((value < 9) ? ("0" + value) : value);
  }
}
