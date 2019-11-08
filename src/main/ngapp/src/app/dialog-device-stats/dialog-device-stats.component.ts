import {Component, OnInit, ViewChild} from '@angular/core';
import {DeviceStats, Stats, StatsValues} from "../service/device-stats";
import {AnimationRotation} from "../material.module";
import {
  BarChartComponentComponent,
  BarChartSamples
} from "../charts/bar-chart-component/bar-chart-component.component";


export class TimeSpanSelection {
  value: number = 0;
  label: string = "";

  constructor(value: number, label: string) {
    this.value = value;
    this.label = label;
  }
}


@Component({
  selector: 'app-dialog-device-stats',
  templateUrl: './dialog-device-stats.component.html',
  styleUrls: ['./dialog-device-stats.component.css'],
  animations: [
    AnimationRotation
  ]
})
export class DialogDeviceStatsComponent implements OnInit {

  title = "[Title]";
  buttonText = "[Button]";
  deviceStats: DeviceStats;

  timeStamp = new Date();
  selectedEnergy = 0;
  selectionEnergy = new Array<TimeSpanSelection>();
  selectedTemperature = 0;
  selectionTemperature = new Array<TimeSpanSelection>();

  energyDisplay = false;
  temperatureDisplay = false;

  @ViewChild("energyChart", { static: false })
  private energyChart : BarChartComponentComponent;

  @ViewChild("temperatureChart", { static: false })
  private temperatureChart : BarChartComponentComponent;

  constructor() {
  }

  ngOnInit() {
  }

  setTitle(title: string) : DialogDeviceStatsComponent {
    this.title = title;
    return this;
  }

  setButtonText(text: string) : DialogDeviceStatsComponent {
    this.buttonText = text;
    return this;
  }

  enableEnergyDisplay(enable: boolean) : DialogDeviceStatsComponent {
    this.energyDisplay = enable;
    return this;
  }

  enableTemperatureDisplay(enable: boolean) : DialogDeviceStatsComponent {
    this.temperatureDisplay = enable;
    return this;
  }

  setStats(deviceStats: DeviceStats) : DialogDeviceStatsComponent {
    this.deviceStats = deviceStats;
    this.setupDataSelection();
    this.setupData();
    return this;
  }

  private setupDataSelection() : void {
    if (!this.deviceStats) {
      return;
    }

    if (this.energyDisplay) {
      let indexMonths = this.getNextGridIndex(this.deviceStats.energy.stats, 2678400 /*31 days*/);
      let indexHours = this.getNextGridIndex(this.deviceStats.energy.stats, 86400 /*24 hours*/);
      this.selectionEnergy = new Array<TimeSpanSelection>();
      if (indexMonths >= 0) {
        this.selectionEnergy.push(new TimeSpanSelection(indexMonths, "Months"));
      }
      if (indexHours >= 0) {
        this.selectionEnergy.push(new TimeSpanSelection(indexHours, "Days"));
      }
    }

    if (this.temperatureDisplay) {
      let indexHours = this.getNextGridIndex(this.deviceStats.temperature.stats, 900);
      this.selectionTemperature = new Array<TimeSpanSelection>();
      if (indexHours >= 0) {
        this.selectionTemperature.push(new TimeSpanSelection(0, "Hour / 15 Minutes"));
      }
    }
  }

  private getNextGridIndex(stats: Array<StatsValues>, seconds: number): number {
    let index = -1;
    let minDiff = 1000;
    for (let i = 0; i < stats.length; ++i) {
      if (Math.abs(stats[i].grid - seconds) < minDiff) {
        index = i;
      }
    }
    return index;
  }

  private setupData() : void {
    if (this.energyDisplay) {
      this.setupEnergyData();
    }
    if (this.temperatureDisplay) {
      this.setupTemperatureData();
    }
  }

  private setupEnergyData() : void {
    if (!this.deviceStats) {
      return;
    }

    let labels = [];
    let samples = this.createChartData(this.deviceStats.energy, this.selectedEnergy, 0.001);
    for (let i = samples.data.length - 1; i >= 0; --i) {
      labels.push('' + (-(i + 1)));
    }
    this.energyChart
      .setXAxisLabel('Time')
      .setYAxisLabel('kWh')
      .setSamples([samples])
      .setLabels(labels);
  }

  private setupTemperatureData() : void {
    if (!this.deviceStats) {
      return;
    }

    let labels = [];
    let samples = this.createChartData(this.deviceStats.temperature, this.selectedTemperature, 0.1);
    for (let i = samples.data.length - 1; i >= 0; --i) {
      labels.push('' + (-Math.floor(i/4) - 1));
    }
    this.temperatureChart
      .setXAxisLabel('Time')
      .setYAxisLabel('°C')
      .setSamples([samples])
      .setLabels(labels);
  }

  private createChartData(deviceStats: Stats, index: number, factor: number) : BarChartSamples {
    let samples = new BarChartSamples;
    if (deviceStats.stats.length > index) {
      let stats = deviceStats.stats[index];
      for (let i = stats.values.length - 1; i >= 0; --i) {
        let val = stats.values[i];
        if (val === -999) {
          val = 0;
        }
        samples.data.push(val * factor);
      }
    }
    return samples;
  }

  onSelectedEnergyTimeSpanChanged(event: any) : void {
    this.setupEnergyData();
  }

  onSelectedTemperatureTimeSpanChanged(event: any) : void {
    this.setupTemperatureData();
  }
}
