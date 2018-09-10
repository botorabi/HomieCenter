import {Device} from "./device";

export class DeviceSwitch extends Device {
  on = false;
  voltage = 0;
  power = 0;
  energy = 0;
  temperature = 0;
  temperatureOffset = 0;
}
