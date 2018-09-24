import {Component, OnInit} from '@angular/core';
import {ViewDevicesComponent} from "../view-devices/view-devices.component";
import {ApiDeviceService} from "../service/api-device.service";
import {AppInformationService} from "../service/app-information.service";
import {Device} from "../service/device";
import {DeviceHeatController} from "../service/device-heat-controller";
import {HeatControllerWidgetUpdater} from "./heat-controller-widget-updater";
import {AnimationRotation, AnimationWidgetSpan} from "../material.module";
import {MatDialog} from "@angular/material";
import {DialogOneButtonComponent} from "../dialog-one-button/dialog-one-button.component";

@Component({
  selector: 'app-view-heat-controller-devices',
  templateUrl: './view-heat-controller-devices.component.html',
  styleUrls: ['./view-heat-controller-devices.component.css'],
  animations: [
    AnimationWidgetSpan,
    AnimationRotation
  ]
})
export class ViewHeatControllerDevicesComponent implements OnInit {

  public devices: Array<DeviceHeatController> = new Array<DeviceHeatController>();
  public error;

  private devicesComponent: ViewDevicesComponent;
  private collapsedWidgets = new Map<string, boolean>();
  private deviceHeatControllerUpdater: HeatControllerWidgetUpdater;

  constructor(private apiDeviceService: ApiDeviceService,
              private appInfoService: AppInformationService,
              private dialog: MatDialog) {

    this.deviceHeatControllerUpdater = new HeatControllerWidgetUpdater(this.devices);
  }

  ngOnInit() {
    //this.testSymbolIcons();
  }

  private testSymbolIcons() {
    //! Test the battery symbol
    setTimeout(() => {
      if (this.devices.length > 0) {
        this.devices[0].batteryLevel -= 5;
        if (this.devices[0].batteryLevel < 10) {
          this.devices[0].batteryLow = true;
          this.devices[0].errorCode = 2;
        }
        if (this.devices[0].batteryLevel < 5) {
          this.devices[0].batteryLevel = 100;
          this.devices[0].batteryLow = false;
          this.devices[0].errorCode = 0;
        }
        this.testSymbolIcons();
      }
    }, 1000);
  }

  public setDevicesComponent(devicesComponent: ViewDevicesComponent): void {
    this.devicesComponent = devicesComponent;
  }

  public updateDevices(devices: Array<DeviceHeatController>) {
    this.deviceHeatControllerUpdater.updateDevices(devices);
    this.appInfoService.setHeatControllerDeviceCount(this.devices.length);
  }

  public getTrimmedDeviceName(device: Device): string {
    let name = device.name;
    return name.length > 16 ? (name.substr(0, 15) + '...') : name;
  }

  public getDeviceSummary(device: DeviceHeatController): string {
    if (!device.present) {
      return 'Device Not Available!';
    }

    let summary = '';
    summary += this.getCurrentTemperature(device) + '°C';
    return summary;
  }

  public isWidgetCollapse(device: Device): boolean {
    return this.collapsedWidgets.has(device.ain) ?
      this.collapsedWidgets.get(device.ain) : true;
  }

  public onToggleWidgetCollapse(device: Device): boolean {
    let collapsed = this.isWidgetCollapse(device);
    this.collapsedWidgets.set(device.ain, !collapsed);
    return !collapsed;
  }

  public getWidgetCollapseSymbol(device: Device): string {
    return this.isWidgetCollapse(device) ? 'keyboard_arrow_down' : 'keyboard_arrow_up';
  }

  public getBatterySymbolStyle(device: DeviceHeatController) {
    if (device.batteryLow) {
      return 'battery-alert';
    }
    let style = 'battery-ok';
    let batteryLevel = device.batteryLevel;
    if (batteryLevel < 20) {
      style = 'battery-low';
    }
    else if (batteryLevel < 50) {
      style = 'battery-middle';
    }
    return style;
  }

  public getBatterySymbol(device: DeviceHeatController) {
    if (device.batteryLow) {
      return 'battery_alert';
    }

    let symbol = 'battery_full';

    /* NOTE: due to missing icons in Material+Icon we have currently to stick at battery_full symbol
    let batteryLevel = device.batteryLevel;
    if (batteryLevel < 20) {
      symbol = 'battery_20';
    }
    else if (batteryLevel < 30) {
      symbol = 'battery_30';
    }
    else if (batteryLevel < 50) {
      symbol = 'battery_50';
    }
    else if (batteryLevel < 60) {
      symbol = 'battery_60';
    }
    else if (batteryLevel < 80) {
      symbol = 'battery_80';
    }
    else if (batteryLevel < 90) {
      symbol = 'battery_90';
    }
    */
    return symbol;
  }

  public setTemperature(device: Device, temperature: number): void {
    device.updating = true;
    this.apiDeviceService.deviceHeatControllerSetTemperature(device.id, temperature, () => {
      // trigger the device update
      if (this.devicesComponent) {
        setTimeout(() => {
          this.devicesComponent.updateDevices();
          device.updating = false;
        }, 200);
      }
    });
  }

  public incrementTemperature(device: DeviceHeatController): void {
    if (device.updating) {
      return;
    }
    this.setTemperature(device, device.setTemperature + 1);
  }

  public decrementTemperature(device: DeviceHeatController): void {
    if (device.updating) {
      return;
    }
    this.setTemperature(device, device.setTemperature - 1);
  }

  private convertTemperature(temperature: number): string {
    return (temperature / 2.0).toFixed(1);
  }

  public getCurrentTemperature(device: DeviceHeatController): string {
    return this.convertTemperature(device.currentTemperature);
  }

  public getSetTemperature(device: DeviceHeatController): string {
    return this.convertTemperature(device.setTemperature);
  }

  public getComfortTemperature(device: DeviceHeatController): string {
    return this.convertTemperature(device.comfortTemperature);
  }

  public getEconomyTemperature(device: DeviceHeatController) {
    return this.convertTemperature(device.economyTemperature);
  }

  public onShowError(device: DeviceHeatController) {
    let message = '';
    switch (device.errorCode) {
      case 1:
        message = "The radiator control '@NAME@' is not ready for operation. Please make sure that the control is correctly mounted on the radiator valve.";
        break;
      case 2:
        message = 'Valve lift too short or battery power too low.';
        break;
      case 3:
        message = 'No valve movement possible.';
        break;
      case 4:
        message = 'Preparing installation.';
        break;
      case 5:
        message = 'The radiator control is in installation mode and can be mounted on the radiator valve.';
        break;
      case 6:
        message = 'The radiator control is now adjusting to the radiator valve lift.';
        break;
    }

    if (message.length > 0) {
      message = message.replace("@NAME@", device.name);
      this.openErrorDialog(message);
    }
  }

  private openErrorDialog(message: string) : void {
    const dialogRef = this.dialog.open(DialogOneButtonComponent);
    dialogRef.componentInstance
      .setTitle('Error Description')
      .setContent(message)
      .setButtonText("Ok");
  }
}
