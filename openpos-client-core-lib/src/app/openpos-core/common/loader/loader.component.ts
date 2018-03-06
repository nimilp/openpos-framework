import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { LoaderState } from './loader';

import { SessionService } from '../../services/session.service';

import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'app-loader',
    templateUrl: 'loader.component.html',
    styleUrls: ['loader.component.scss']
})
export class LoaderComponent implements OnInit, OnDestroy {

    show = false;
    title: string = LoaderState.LOADING_TITLE;
    message: string = null;

    connected = true;
    loading = false;

    private subscription: any;

    constructor(
        public session: SessionService,
    ) { }

    ngOnInit() {
        this.subscription = this.session.loaderState.observable
            .subscribe((state: LoaderState) => {
                this.title = state.title ? state.title : LoaderState.LOADING_TITLE;
                this.message = state.message;
                this.show = state.show;
                this.connected = !state.show;
                this.loading = state.show;
            });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public getHiddenClass(): string {
        if (this.show) {
            return '';
        } else {
            return 'loader-hidden';
        }
    }

    public getLocalTheme(): string {
        if (localStorage.getItem('theme')) {
            return localStorage.getItem('theme');
        } else {
            return 'openpos-theme';
        }
    }
}
