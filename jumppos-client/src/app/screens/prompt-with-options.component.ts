import { Component, OnInit } from '@angular/core';
import {SessionService} from '../session.service';
import {ChooseOptionsComponent} from './choose-options.component';
import {PromptInputComponent} from '../common/controls/prompt-input.component';

@Component({
  selector: 'app-prompt-with-options',
  templateUrl: './prompt-with-options.component.html'
})
export class PromptWithOptionsComponent extends ChooseOptionsComponent implements OnInit {

  promptInputCallback: Function;

  constructor(public session: SessionService) {
      super(session);
  }

  public ngOnInit(): void {
    this.promptInputCallback = this.onPromptInputEnter.bind(this);
  }

  onPromptInputEnter($event, promptInput: PromptInputComponent): void {
    if (promptInput.responseText) {
        this.session.response = promptInput.responseText;
        this.session.screen.responseText = null;
        promptInput.responseText = null;
        this.session.onAction(this.session.screen.action);
        $event.target.disabled = true;
    }
  }
}
