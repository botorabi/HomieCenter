import { Component, OnInit } from '@angular/core';
import {ApiDeviceService} from "../service/api-device.service";
import {Device} from "../service/device";

@Component({
  selector: 'app-view-switch-devices',
  templateUrl: './view-switch-devices-component.html',
  styleUrls: ['./view-switch-devices-component.css']
})
export class ViewSwitchDevicesComponent implements OnInit {

  public devices: Array<Device>;
  public error;

  private UPDATE_INTERVAL = 10 * 60 * 1000;

  constructor(private apiDeviceService: ApiDeviceService) { }

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

  public toggleState(device: Device) {
    if (!device.present) {
      return;
    }
    this.apiDeviceService.deviceSwitch(device.id, !device.on, () => {
      setTimeout(() => {
        this.updateDevices();
      }, 1000);
    });
  }
}
