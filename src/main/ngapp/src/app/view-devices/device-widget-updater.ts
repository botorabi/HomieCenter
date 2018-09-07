import {Device} from "../service/device";

export abstract class DeviceWidgetUpdater {

  public devices: Array<Device>;

  protected constructor(devices: Array<Device>) {
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

  protected copyDeviceData(source: Device, destination: Device) : void {
    this.copyDeviceFieldIfChanged(source, destination, 'ain');
    this.copyDeviceFieldIfChanged(source, destination, 'id');
    this.copyDeviceFieldIfChanged(source, destination, 'name');
    this.copyDeviceFieldIfChanged(source, destination, 'present');
    this.copyDeviceFieldIfChanged(source, destination, 'firmware');
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

  protected copyDeviceFieldIfChanged(source: Device, destination: Device, fieldName: string) : boolean {
    if (source[fieldName] !== destination[fieldName]) {
      destination[fieldName] = source[fieldName];
      return true;
    }
  }
}
