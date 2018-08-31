import { Component, OnInit } from '@angular/core';
import {ApiDeviceService} from "../service/api-device.service";
import {Device} from "../service/device";
import {AppInformationService} from "../service/app-information.service";
import {animate, style, transition, trigger} from "@angular/animations";


@Component({
  selector: 'app-view-switch-devices',
  templateUrl: './view-switch-devices-component.html',
  styleUrls: ['./view-switch-devices-component.css'],
  animations: [
    trigger('detailsFading', [
      transition('void => *', [
        style({height: '0'}),
        animate(150, style({height: '*'}))
      ]),
      transition('* => void', [
        style({height: '*'}),
        animate(250, style({height: '0'}))
      ])
    ])
  ]
})
export class ViewSwitchDevicesComponent implements OnInit {

  public devices: Array<Device>;
  public error;

  private UPDATE_INTERVAL = 30 * 1000;
  private collapsedWidgets = new Map<string, boolean>();

  constructor(private apiDeviceService: ApiDeviceService,
              private appInfoService: AppInformationService) { }

  ngOnInit() {
    this.appInfoService.refreshLogoutTimer();
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
        //! TODO update the devices in a way that the widgets do not get reset!
        this.devices = devices;
        this.appInfoService.setSwitchDeviceCount(this.devices.length);
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
    device.unlocked = false;
    this.apiDeviceService.deviceSwitch(device.id, !device.on, () => {
      setTimeout(() => {
        this.updateDevices();
      }, 100);
    });
  }

  public onToggleLock(device: Device) {
    device.unlocked = !device.unlocked;
  }

  public getTitleClass(name: string) {
     return name.length > 9 ? "title-small" : "title-normal";
  }

  public getDeviceSummary(device: Device) {
    if (!device.present) {
      return "Device Not Available!";
    }

    let summary = "";
    summary += device.on ? "On" : "Off";
    if (device.on) {
      summary += " / " + this.getPower(device) + " Watt";
    }
    summary += " / " + this.getTemperature(device) + "°C";
    return summary;
  }

  public isWidgetCollapse(device): boolean {
    return this.collapsedWidgets.has(device.ain) ?
      this.collapsedWidgets.get(device.ain) : true;
  }

  public onToggleWidgetCollapse(device): boolean {
    let collapsed = this.isWidgetCollapse(device);
    this.collapsedWidgets.set(device.ain, !collapsed);
    return !collapsed;
  }

  public getWidgetCollapseSymbol(device: Device) {
    return this.isWidgetCollapse(device) ? "keyboard_arrow_down" : "keyboard_arrow_up";
  }
}
