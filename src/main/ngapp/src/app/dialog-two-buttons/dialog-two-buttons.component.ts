import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-dialog-two-buttons',
  templateUrl: './dialog-two-buttons.component.html',
  styleUrls: ['./dialog-two-buttons.component.css']
})
export class DialogTwoButtonsComponent implements OnInit {

  title = "[Title]";
  content = "[Content]";
  buttonText1 = "[Button1]";
  buttonText2 = "[Button2]";

  constructor() { }

  ngOnInit() {
  }

  setTitle(title: string) : void {
    this.title = title;
  }

  setContent(content: string) : void {
    this.content = content;
  }

  setButton1Text(text: string) : void {
    this.buttonText1 = text;
  }

  setButton2Text(text: string) : void {
    this.buttonText2 = text;
  }
}
