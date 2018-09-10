import {Component, OnInit} from '@angular/core';
import {ApiDeviceService} from "../service/api-device.service";
import {Device} from "../service/device";
import {SwitchWidgetUpdater} from "./switch-widget-updater";
import {DeviceSwitch} from "../service/device-switch";
import {AppInformationService} from "../service/app-information.service";
import {ViewDevicesComponent} from "../view-devices/view-devices.component";
import {AnimationRotation, AnimationWidgetSpan} from "../material.module";


@Component({
  selector: 'app-view-switch-devices',
  templateUrl: './view-switch-devices-component.html',
  styleUrls: ['./view-switch-devices-component.css'],
  animations: [
    AnimationWidgetSpan,
    AnimationRotation
  ]
})
export class ViewSwitchDevicesComponent implements OnInit {

  public devices: Array<DeviceSwitch> = new Array<DeviceSwitch>();
  public error;

  private devicesComponent: ViewDevicesComponent;
  private collapsedWidgets = new Map<string, boolean>();
  private deviceWidgetUpdater: SwitchWidgetUpdater;

  constructor(private apiDeviceService: ApiDeviceService,
              private appInfoService: AppInformationService) {

    this.deviceWidgetUpdater = new SwitchWidgetUpdater(this.devices);
  }

  ngOnInit() {
  }

  public setDevicesComponent(devicesComponent: ViewDevicesComponent) : void {
    this.devicesComponent = devicesComponent;
  }

  public updateDevices(devices: Array<DeviceSwitch>) {
    this.deviceWidgetUpdater.updateDevices(devices);
    this.appInfoService.setSwitchDeviceCount(this.devices.length);
  }

  public getVoltage(device: DeviceSwitch) : string {
    return (device.voltage / 1000).toFixed(1);
  }

  public getPower(device: DeviceSwitch) : string {
    return (device.power / 1000).toFixed(1);
  }

  public getTemperature(device: DeviceSwitch) : string {
    return ((device.temperature + device.temperatureOffset) / 10).toFixed(1);
  }

  public onToggleState(device: DeviceSwitch) : void {
    if (!device.present || !device.unlocked) {
      return;
    }
    device.unlocked = false;
    device.updating = true;
    this.apiDeviceService.deviceSwitch(device.id, !device.on, () => {
      // trigger the device update
      if (this.devicesComponent) {
        window.setTimeout(() => {
          this.devicesComponent.updateDevices();
          device.updating = false;
        }, 200);
      }
    });
  }

  public onToggleLock(device: Device) : void {
    device.unlocked = !device.unlocked;
  }

  public getTrimmedDeviceName(device: Device) : string {
    let name = device.name;
    return name.length > 12 ? (name.substr(0, 11) + '...') : name;
  }

  public getDeviceSummary(device: DeviceSwitch) : string {
    if (!device.present) {
      return 'Device Not Available!';
    }

    let summary = '';
    if (device.on) {
      summary += this.getPower(device) + ' Watt / ';
    }
    summary += this.getTemperature(device) + '°C';
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

  public getWidgetCollapseSymbol(device: Device) : string {
    return this.isWidgetCollapse(device) ? 'keyboard_arrow_down' : 'keyboard_arrow_up';
  }

  public getSwitchStateStyle(device: DeviceSwitch) : string {
    return device.on ? 'device-state-switch-on' : 'device-state-switch-off';
  }
}
