import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { PersonalizationService } from './personalization.service';
import { MatDialog } from '@angular/material';
import { StompRService } from '@stomp/ng2-stompjs';
import { DeviceService } from './device.service';
import { AppInjector } from '../app-injector';
import { Injector } from '@angular/core';


describe('SessionService', () => {

    let stompServiceSpy: jasmine.SpyObj<StompRService>;
    let sessionService: SessionService;
    let deviceServiceSpy: jasmine.SpyObj<DeviceService>;

    beforeEach(() => {
        const stompSpy = jasmine.createSpyObj('StompRService', ['publish']);
        const personalizationSpy = jasmine.createSpyObj('PersonalizationService', ['getNodeId']);
        const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
        const deviceSpy = jasmine.createSpyObj('DeviceService', ['isRunningInCordova']);

        TestBed.configureTestingModule({
            providers: [
                { provide: PersonalizationService, useValue: personalizationSpy },
                SessionService,
                { provide: MatDialog, useValue: matDialogSpy },
                { provide: StompRService, useValue: stompSpy },
                { provide: DeviceService, useValue: deviceSpy}
            ]
        });
        AppInjector.Instance = TestBed.get(Injector);


        deviceServiceSpy = TestBed.get(DeviceService);
        deviceServiceSpy.isRunningInCordova.and.returnValue(false);

        stompServiceSpy = TestBed.get(StompRService);
        sessionService = TestBed.get(SessionService);
    });

    describe('onAction', () => {
        it('should call StompService.publish when called', () => {
            sessionService.onAction('foo', 'bar');
            expect(stompServiceSpy.publish.calls.count).toBeTruthy();
        });
    });

});
