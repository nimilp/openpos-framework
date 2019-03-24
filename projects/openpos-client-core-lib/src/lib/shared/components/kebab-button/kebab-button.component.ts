import { Component, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { IActionItem } from '../../../core/interfaces';
import { MatDialog } from '@angular/material';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';
import { Subscription } from 'rxjs';
import { KebabMenuComponent } from '../../../core/components/kebab-menu/kebab-menu.component';

@Component({
    selector: 'app-kebab-button',
    templateUrl: './kebab-button.component.html',
    styleUrls: ['./kebab-button.component.scss']
})
export class KebabButtonComponent implements OnDestroy {

    @Input()
    menuItems: IActionItem[];

    @Input()
    color?: string;

    @Input()
    set keyBinding( key: string) {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }

        this.subscription = this.keyPresses.subscribe( key, 100, event => {
            // ignore repeats
            if ( event.repeat || !Configuration.enableKeybinds ) {
                return;
            }
            if (event.type === 'keydown') {
                this.openKebabMenu();
            }
        });
    }

    @Output()
    menuItemClick = new EventEmitter<IActionItem>();

    private subscription: Subscription;

    constructor(private dialog: MatDialog, private keyPresses: KeyPressProvider) {
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    public openKebabMenu() {
        const dialogRef = this.dialog.open(KebabMenuComponent, {
            data: {
                menuItems: this.menuItems,
                payload: null,
                disableClose: false,
                autoFocus: false
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.menuItemClick.emit(result);
            }
        });
    }
}
