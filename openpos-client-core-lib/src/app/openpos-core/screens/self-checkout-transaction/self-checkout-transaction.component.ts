import { DeviceService } from '../../services/device.service';
import { ISellItem } from '../../common/isellitem';
import { IScreen } from '../../common/iscreen';
import { Component, ViewChild, AfterViewInit, AfterContentInit, DoCheck, OnInit } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { AbstractApp } from '../../common/abstract-app';
import { ObservableMedia } from '@angular/flex-layout';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-self-checkout-transaction',
  templateUrl: './self-checkout-transaction.component.html',
  styleUrls: ['./self-checkout-transaction.component.scss']
})
export class SelfCheckoutTransactionComponent implements AfterViewInit, DoCheck, IScreen, OnInit {


  @ViewChild('box') vc;
  initialized = false;

  public overFlowListSize: Observable<number>;

  public items: ISellItem[];

  constructor(public session: SessionService, devices: DeviceService, private observableMedia: ObservableMedia) {

  }

  show(session: SessionService, app: AbstractApp) {
  }

  ngDoCheck(): void {
    if (typeof this.session.screen !== 'undefined') {
      this.items = this.session.screen.items;
    }
  }

  ngOnInit(): void {
    const sizeMap = new Map([
      ['xs', 3],
      ['sm', 3],
      ['md', 4],
      ['lg', 5],
      ['xl', 5]
    ]);

    let startSize: number = 3;
    sizeMap.forEach((size, mqAlias) => {
      if (this.observableMedia.isActive(mqAlias)) {
        startSize = size;
      }
    });
    this.overFlowListSize = this.observableMedia.asObservable().map(
      change => {
        return sizeMap.get(change.mqAlias);
      }
    ).startWith(startSize);
  }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

  onEnter(value: string) {
    this.session.response = value;
    this.session.onAction('Next');
  }

  onScanInputEnter(value): void {
    this.session.onAction('Next', value);
  }

}
