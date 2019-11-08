import {Component, OnInit, ViewChild} from '@angular/core';
import {Device} from "../service/device";
import {DeviceSwitch} from "../service/device-switch";
import {DeviceHeatController} from "../service/device-heat-controller";
import {ApiDeviceService} from "../service/api-device.service";
import {AppInformationService} from "../service/app-information.service";
import {ViewSwitchDevicesComponent} from "../view-switch-devices/view-switch-devices-component";
import {ViewHeatControllerDevicesComponent} from "../view-heat-controller-devices/view-heat-controller-devices.component";

@Component({
  selector: 'app-view-devices',
  templateUrl: './view-devices.component.html',
  styleUrls: ['./view-devices.component.css']
})
export class ViewDevicesComponent implements OnInit {

  public error;

  @ViewChild('switchView', { static: true })
  private switchView: ViewSwitchDevicesComponent;

  @ViewChild('heatControllerView', { static: true })
  private heatControllerView: ViewHeatControllerDevicesComponent;

  private UPDATE_INTERVAL = 30 * 1000;

  constructor(private apiDeviceService: ApiDeviceService,
              public appInfoService: AppInformationService) {
  }

  ngOnInit() {
    this.switchView.setDevicesComponent(this);
    this.heatControllerView.setDevicesComponent(this);
    this.appInfoService.refreshLogoutTimer();

    setTimeout(() => {
      this.periodicUpdate();
    }, 500);
  }

  private periodicUpdate() : void {
    this.updateDevices();
    setTimeout(() => {
      this.periodicUpdate();
    }, this.UPDATE_INTERVAL);
  }

  public updateDevices() : void {
    this.error = null;
    this.apiDeviceService.deviceList((devices: Array<Device>, errorStatus: string) => {
      if (devices === null) {
        this.error = errorStatus;
      }
      else {
        this.updateSwitchDevices(devices);
        this.updateHeatControllerDevices(devices);
      }
      this.appInfoService.setDeviceInfoReady(true);
    });
  }

  private updateSwitchDevices(devices: Array<Device>) {
    let switchDevices: Array<Device> =
      devices.filter((value: Device, index: number, array: Device[]) => {
        return value.deviceType === "SWITCH";
      });

    this.switchView.updateDevices(<Array<DeviceSwitch>>switchDevices);
  }

  private updateHeatControllerDevices(devices: Array<Device>) {
    let heatControllerDevices: Array<Device> =
      devices.filter((value: Device, index: number, array: Device[]) => {
        return value.deviceType === "HEATCONTROLLER";
      });

    this.heatControllerView.updateDevices(<Array<DeviceHeatController>>heatControllerDevices);
  }
}
