import { Component, OnInit } from '@angular/core';
import {ApiDeviceService} from "../service/api-device.service";
import {Device} from "../service/device";
import {AppInformationService} from "../service/app-information.service";

@Component({
  selector: 'app-view-switch-devices',
  templateUrl: './view-switch-devices-component.html',
  styleUrls: ['./view-switch-devices-component.css']
})
export class ViewSwitchDevicesComponent implements OnInit {

  public devices: Array<Device>;
  public error;

  private UPDATE_INTERVAL = 30 * 1000;

  constructor(private apiDeviceService: ApiDeviceService,
              private appInfo: AppInformationService) { }

  ngOnInit() {
    this.periodicUpdate();
  }

  private periodicUpdate() {
    this.updateDevices();
    setTimeout(() => {
      this.periodicUpdate();
    }, this.UPDATE_INTERVAL);
  }

  private updateDevices() {
    this.error = null;
    this.apiDeviceService.deviceList((devices: Array<Device>, errorStatus: string) => {
      if (devices === null) {
        this.error = errorStatus;
      }
      else {
        this.devices = devices;
        this.appInfo.setSwitchDeviceCount(this.devices.length);
      }
    });
  }

  public getVoltage(device: Device) {
    return (device.voltage / 1000).toFixed(1);
  }

  public getPower(device: Device) {
    return (device.power / 1000).toFixed(1);
  }

  public getTemperature(device: Device) {
    return (device.temperature + device.temperatureOffset) / 10;
  }

  public onToggleState(device: Device) {
    if (!device.present || !device.unlocked) {
      return;
    }
    this.apiDeviceService.deviceSwitch(device.id, !device.on, () => {
      setTimeout(() => {
        this.updateDevices();
      }, 1000);
    });
  }

  public onToggleLock(device: Device) {
    device.unlocked = !device.unlocked;
  }
}
