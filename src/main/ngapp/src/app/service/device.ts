export class Device {
  deviceType = "";
  name = "";
  id: number;
  ain = "";
  firmware = "";
  present = false;

  /* Following states are used for UI handling */
  updating = false;
}
