import {Component, OnInit} from '@angular/core';

export class BarChartSamples {
  label = "";
  data = new Array<number>();
}

@Component({
  selector: 'app-bar-chart-component',
  templateUrl: './bar-chart-component.component.html',
  styleUrls: ['./bar-chart-component.component.css']
})
export class BarChartComponentComponent implements OnInit {

  xAxisLabel = "";
  yAxisLabel = "";

  options:any = {
    title: false,
    scaleShowVerticalLines: false,
    responsive: true,
    tooltips: false,
    categoryPercentage: 0.9,
    barPercentage: 1.0,
    legend: {
      onClick: function() { /* disable the default on-click behaviour!*/},
      labels: {
        boxWidth: 0
      }
    },
    scales: {
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: this.xAxisLabel
        },
        ticks: {
          callback: function(value, index, values) {
            if (index == 0 || index == values.length || index == (values.length/2)) {
              return value;
            }
            return '';
          }
        }
      }],
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: this.yAxisLabel
        },
      }]
    }
  };

  type = 'bar';
  legend = true;

  barColor: string = '#3f51b5';
  colors: Array<any> = [
    {
      backgroundColor: this.barColor,
      borderColor: this.barColor,
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];

  samples: Array<BarChartSamples> = null;
  labels: string[];

  constructor() { }

  ngOnInit() {
  }

  setSamples(samples: Array<BarChartSamples>) : BarChartComponentComponent {
    if (!samples || samples.length < 1) {
      return this;
    }

    this.samples = null;
    // refresh the chart with a small delay letting the data change propagate properly
    setTimeout(() => {
      // create a deep copy of new data
      this.samples = JSON.parse(JSON.stringify(samples));;
    }, 100);

    return this;
  }

  setLabels(labels: string[]) : BarChartComponentComponent {
    this.labels = labels;
    return this;
  }

  setXAxisLabel(label: string) : BarChartComponentComponent {
    this.options.scales.xAxes[0].scaleLabel.labelString = label;
    return this;
  }

  setYAxisLabel(label: string) : BarChartComponentComponent {
    this.options.scales.yAxes[0].scaleLabel.labelString = label;
    return this;
  }
}
