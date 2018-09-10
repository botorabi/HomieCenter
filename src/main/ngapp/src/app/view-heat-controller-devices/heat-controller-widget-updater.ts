import {Device} from "../service/device";
import {DeviceWidgetUpdater} from "../view-devices/device-widget-updater";

export class HeatControllerWidgetUpdater extends DeviceWidgetUpdater {

  constructor(devices: Array<Device>) {
    super(devices);
  }

  protected copyDeviceData(source: Device, destination: Device) : void {
    super.copyDeviceData(source, destination);
    this.copyDeviceFieldIfChanged(source, destination, 'batteryLow');
    this.copyDeviceFieldIfChanged(source, destination, 'batteryLevel');
    this.copyDeviceFieldIfChanged(source, destination, 'currentTemperature');
    this.copyDeviceFieldIfChanged(source, destination, 'setTemperature');
    this.copyDeviceFieldIfChanged(source, destination, 'comfortTemperature');
    this.copyDeviceFieldIfChanged(source, destination, 'economyTemperature');
    this.copyDeviceFieldIfChanged(source, destination, 'windowOpen');
    this.copyDeviceFieldIfChanged(source, destination, 'errorCode');
  }
}
