import {Injectable} from '@angular/core';
import {SessionService} from './session.service';
import {CloseToastMessage, IToastScreen, ToastType} from '../interfaces/toast-screen.interface';
import {ActiveToast, ToastrService} from 'ngx-toastr';
import {ToastComponent} from "../../shared/components/toast/toast.component";

@Injectable({
    providedIn: 'root',
  })
export class ToastService {
    persistedToasts = new Map<String,ActiveToast<any>>();
    constructor( private sessionService: SessionService, private toastrService: ToastrService ) {
        sessionService.getMessages('Toast').subscribe(m => this.showMessage(m));
        sessionService.getMessages('CloseToast').subscribe(m => this.closeMessage(m));
        sessionService.getMessages('Connected').subscribe(m => this.toastrService.clear());
        window['toastService'] = this;
    }

    private closeMessage(message: any) {
        const toastMessage = message as CloseToastMessage;
        const messageToClose = this.persistedToasts.get(toastMessage.persistedId);
        if(messageToClose) {
            this.toastrService.remove(messageToClose.toastId);
        }
    }

    private showMessage(message: any) {
        const toastMessage = message as IToastScreen;
        const toast = this.toastrService.show(toastMessage.message, null, {
            timeOut: toastMessage.duration,
            extendedTimeOut: toastMessage.duration,
            disableTimeOut: this.isStickyToast(toastMessage),
            tapToDismiss: !toastMessage.persistent,
            positionClass: this.getPosition(toastMessage.verticalPosition),
            toastClass: `ngx-toastr app-${this.getType(toastMessage.toastType)}`,
            toastComponent: ToastComponent
        });
        toast.toastRef.componentInstance.iconName = toastMessage.icon;
        if(toastMessage.persistent) {
            this.persistedToasts.set(toastMessage.persistedId, toast);
        }
        this.sessionService.cancelLoading();
    }

    private isStickyToast(toastMessage: IToastScreen): boolean {
        return toastMessage.duration === 0;
    }

    private getPosition(verticalPosition: String): string {
        switch (verticalPosition) {
            case 'top':
                return 'toast-top-center';
            case 'bottom':
                return 'toast-bottom-center';
        }
        return null;
    }

    private getType(type: String): string {
        switch (type) {
            case ToastType.Success:
                return 'toast-success';
            case ToastType.Warn:
                return 'toast-warning';
            case ToastType.Info:
                return 'toast-info';
        }
        return null;
    }
}
