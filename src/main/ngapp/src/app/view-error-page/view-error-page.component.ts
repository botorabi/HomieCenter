import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-view-error-page',
  templateUrl: './view-error-page.component.html',
  styleUrls: ['./view-error-page.component.css']
})
export class ViewErrorPageComponent implements OnInit {

  message: string;

  constructor() { }

  ngOnInit() {
  }
}
