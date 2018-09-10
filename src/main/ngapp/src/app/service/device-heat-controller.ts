import {Device} from "./device";

export class DeviceHeatController extends Device {
  batteryLow = false;
  batteryLevel = 0;
  currentTemperature = 0;
  setTemperature = 0;
  comfortTemperature = 0;
  economyTemperature = 0;
  windowOpen = false;
  errorCode = 0;
}
