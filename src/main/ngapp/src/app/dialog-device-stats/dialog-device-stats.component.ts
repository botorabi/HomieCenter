import {Component, OnInit, ViewChild} from '@angular/core';
import {DeviceStats, Stats} from "../service/device-stats";
import {AnimationRotation} from "../material.module";
import {
  BarChartComponentComponent,
  BarChartSamples
} from "../charts/bar-chart-component/bar-chart-component.component";

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

  @ViewChild("energyChart")
  private energyChart : BarChartComponentComponent;

  @ViewChild("temperatureChart")
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

  setStats(deviceStats: DeviceStats) : DialogDeviceStatsComponent {
    this.deviceStats = deviceStats;
    this.setupData();
    return this;
  }

  private setupData() : void {
    if (!this.deviceStats) {
      return;
    }

    let labels = [];
	//! TODO provide a choice for the time span! Currently we stick at the first choice which is seems to be always for one year.
    let samples = this.createChartData(this.deviceStats.energy, 0, 0.001);
    for (let i = samples.data.length - 1; i >= 0; --i) {
      labels.push('' + (-(i + 1)));
    }
    this.energyChart
      .setXAxisLabel('Time / ' + this.calculateSamplePeriod(this.deviceStats.energy, 0))
      .setYAxisLabel('kWh')
      .setSamples([samples])
      .setLabels(labels);

    labels = [];
    samples = this.createChartData(this.deviceStats.temperature, 0, 0.1);
    for (let i = samples.data.length - 1; i >= 0; --i) {
      labels.push('' + (-Math.floor(i/4) - 1));
    }
    this.temperatureChart
      .setXAxisLabel('Time / ' + this.calculateSamplePeriod(this.deviceStats.temperature, 0))
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

  private calculateSamplePeriod(deviceStats: Stats, index: number) : string {
    let period = 0;
    let text = '';
    if (deviceStats.stats.length > index) {
      let stats = deviceStats.stats[index];
      if (stats.grid >= 2678400) {
        period = (stats.grid / (24 * 31 * 3600));
        text = '' + (period > 1 ? period.toFixed(0) + ' ' : '') + 'Month';
      }
      else if (stats.grid >= 86400) {
        period = (stats.grid / 3600);
        text = '' + (period > 1 ? period.toFixed(0) + ' ' : '') + 'Hour';
      }
      else if (stats.grid >= 900) {
        period = (stats.grid / 60);
        text = '' + (period > 1 ? period.toFixed(0) + ' ' : '') + 'Minute';
      }
      if (period > 1) {
        text += "s";
      }
    }

    return text;
  }
}
