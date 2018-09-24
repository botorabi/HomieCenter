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

  setTitle(title: string) : DialogTwoButtonsComponent {
    this.title = title;
    return this;
  }

  setContent(content: string) : DialogTwoButtonsComponent {
    this.content = content;
    return this;
  }

  setButton1Text(text: string) : DialogTwoButtonsComponent {
    this.buttonText1 = text;
    return this;
  }

  setButton2Text(text: string) : DialogTwoButtonsComponent {
    this.buttonText2 = text;
    return this;
  }
}
