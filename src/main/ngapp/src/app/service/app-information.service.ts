import {Injectable} from '@angular/core';
import {User} from "./user";
import {ApiAuthService} from "./api-auth.service";
import {Camera} from "./camera";

@Injectable()
export class AppInformationService {

  name: string = "Homie Center";
  version: string = "0.5.0";

  user: User;
  logoutTime: string;
  selectedCamera: Camera;
  switchDeviceCount = 0;
  cameraCount = 0;
  camerasExpanded = true;
  switchDevicesExpanded = true;

  private LogoutTimeoutSec = 30 * 60;

  constructor(private apiAuthService: ApiAuthService) {
    this.logoutTime = this.formatTime(this.LogoutTimeoutSec);
    this.apiAuthService.getStatus((user: User) => {
      if (user.authenticated) {
        this.setUser(user);
      }
      else {
        this.setUser(null);
      }
    });
  }

  public setUser(user: User) {
    this.user = user;
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
    this.updateLogoutTimer(this.user);
  }

  private updateLogoutTimer(user: User) {
    if (user) {
      let timeDiff = Date.now() - user.loginTime.getTime();
      let timeRemaining = this.LogoutTimeoutSec - timeDiff / 1000;
      window.setTimeout(() => {
        if (timeRemaining < 0) {
          this.apiAuthService.logout(() => {
            window.location.href = "";
          });
        }
        else {
          this.logoutTime = this.formatTime(timeRemaining);
          this.updateLogoutTimer(this.user);
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
