import {Device} from "../service/device";

export class DeviceWidgetUpdater {

  public devices: Array<Device>;

  constructor(devices: Array<Device>) {
    this.devices = devices;
  }

  public updateDevices(devices: Array<Device>) : void {
    this.addOrUpdateDevices(devices);
    this.removeDeletedDevices(devices);
  }

  protected addOrUpdateDevices(devices: Array<Device>) : void {
    devices.forEach((device: Device, index: number, array: Device[]) => {
      let existingDevice = this.findDeviceByAIN(this.devices, device.ain);
      if (existingDevice) {
        this.copyDeviceData(device, existingDevice);
      }
      else {
        this.devices.push(device);
      }
    });
  }

  protected removeDeletedDevices(devices: Array<Device>) : void {
    for (let index = this.devices.length - 1; index >= 0; index -= 1) {
      let existingDevice = this.findDeviceByAIN(devices, this.devices[index].ain);
      if (!existingDevice) {
        this.devices.splice(index, 1);
      }
    }
  }

  public findDeviceByAIN(devices: Array<Device>, ain: string) : Device {
    if (!devices) {
      return null;
    }
    for (let index = 0; index < devices.length; index++) {
      let device = devices[index];
      if (device.ain === ain) {
        return device;
      }
    }
    return null;
  }

  protected copyDeviceData(source: Device, destination: Device) : void {
    this.copyDeviceFieldIfChanged(source, destination, 'ain');
    this.copyDeviceFieldIfChanged(source, destination, 'id');
    this.copyDeviceFieldIfChanged(source, destination, 'name');
    this.copyDeviceFieldIfChanged(source, destination, 'present');
    this.copyDeviceFieldIfChanged(source, destination, 'firmware');
    this.copyDeviceFieldIfChanged(source, destination, 'power');
    this.copyDeviceFieldIfChanged(source, destination, 'voltage');
    this.copyDeviceFieldIfChanged(source, destination, 'energy');
    this.copyDeviceFieldIfChanged(source, destination, 'on');
    this.copyDeviceFieldIfChanged(source, destination, 'temperature');
    this.copyDeviceFieldIfChanged(source, destination, 'temperatureOffset');
  }

  protected copyDeviceFieldIfChanged(source: Device, destination: Device, fieldName: string) : boolean {
    if (source[fieldName] !== destination[fieldName]) {
      destination[fieldName] = source[fieldName];
      return true;
    }
  }
}
