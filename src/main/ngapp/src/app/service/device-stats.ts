export class StatsValues {
  grid = 0;
  values: Array<number>;
}

export class Stats {
  stats: Array<StatsValues>;
}

export class DeviceStats {
  ain = "";
  temperature: Stats;
  power: Stats;
  energy: Stats;
}
