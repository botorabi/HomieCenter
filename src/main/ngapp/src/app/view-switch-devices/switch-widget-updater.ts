import {Device} from "../service/device";
import {DeviceWidgetUpdater} from "../view-devices/device-widget-updater";

export class SwitchWidgetUpdater extends DeviceWidgetUpdater {

  constructor(devices: Array<Device>) {
    super(devices);
  }

  protected copyDeviceData(source: Device, destination: Device) : void {
    super.copyDeviceData(source, destination);
    this.copyDeviceFieldIfChanged(source, destination, 'power');
    this.copyDeviceFieldIfChanged(source, destination, 'voltage');
    this.copyDeviceFieldIfChanged(source, destination, 'energy');
    this.copyDeviceFieldIfChanged(source, destination, 'on');
    this.copyDeviceFieldIfChanged(source, destination, 'temperature');
    this.copyDeviceFieldIfChanged(source, destination, 'temperatureOffset');
  }
}
