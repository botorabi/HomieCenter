import {Injectable} from '@angular/core';
import {UserStatus} from "./user-status";
import {ApiUserService} from "./api-user.service";
import {Camera} from "./camera";

@Injectable()
export class AppInformationService {

  name: string = "Homie Center";
  version: string = "";

  deviceInfoReady = false;
  cameraInfoReady = false;
  passedInitialDelay = false;

  userStatus: UserStatus = null;
  logoutTimeString: string = "";
  logoutTimer: Date;
  selectedCamera: Camera = null;
  switchDeviceCount = 0;
  heatControllerDeviceCount = 0;
  cameraCount = 0;
  camerasExpanded = true;
  devicesSwitchExpanded = true;
  devicesHeatControllerExpanded = true;

  private LogoutTimeoutSec = 30 * 60;

  constructor(private apiUserService: ApiUserService) {
    this.refreshLogoutTimer();
  }

  public setDeviceInfoReady(ready: boolean) : void {
    this.deviceInfoReady = ready;
  }

  public setCameraInfoReady(ready: boolean) : void {
    this.cameraInfoReady = ready;
  }

  public isAppInitialized() : boolean {
    return this.deviceInfoReady && this.cameraInfoReady;
  }

  public showNoDeviceHint() : boolean {
    let noDevices = this.cameraCount === 0 && this.switchDeviceCount === 0 && this.heatControllerDeviceCount === 0;
    return noDevices && this.passedInitialDelay;
  }

  public setUserStatus(userStatus: UserStatus) : void {
    this.userStatus = userStatus;
    this.version = userStatus ? userStatus.appVersion : "";
    this.periodicLogoutTimerUpdate(userStatus);

    this.passedInitialDelay = false;
    if (userStatus && userStatus.authenticated) {
      setTimeout(() => {
        this.passedInitialDelay = true;
      }, 5000);
    }
  }

  public isUserAuthenticated() : boolean {
    return this.userStatus && this.userStatus.authenticated;
  }

  public setSelectedCamera(camera: Camera) : void {
    this.selectedCamera = camera;
  }

  public setCameraCount(count: number) : void {
    this.cameraCount = count;
  }

  public setSwitchDeviceCount(count: number) : void {
    this.switchDeviceCount = count;
  }

  public setHeatControllerDeviceCount(count: number) : void {
    this.heatControllerDeviceCount = count;
  }

  public refreshLogoutTimer() : void {
    this.logoutTimer = new Date();
    this.logoutTimeString = this.formatTime(this.getRemainingTime());
  }

  private periodicLogoutTimerUpdate(userStatus: UserStatus) : void {
    if (userStatus && userStatus.authenticated) {
      setTimeout(() => {
        let timeRemaining = this.getRemainingTime();
        if (timeRemaining < 0) {
          this.apiUserService.logout(null);
          window.location.href = '';
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
