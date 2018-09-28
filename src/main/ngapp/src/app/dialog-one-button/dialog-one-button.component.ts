import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-dialog-one-button',
  templateUrl: './dialog-one-button.component.html',
  styleUrls: ['./dialog-one-button.component.css']
})
export class DialogOneButtonComponent implements OnInit {

  title = "[Title]";
  content = "[Content]";
  buttonText = "[Button]";

  constructor() { }

  ngOnInit() {
  }

  setTitle(title: string) : DialogOneButtonComponent {
    this.title = title;
    return this;
  }

  setContent(content: string) : DialogOneButtonComponent {
    this.content = content;
    return this;
  }

  setButtonText(text: string) : DialogOneButtonComponent {
    this.buttonText = text;
    return this;
  }
}
