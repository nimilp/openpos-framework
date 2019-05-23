import { IScanner } from './scanner.interface';
import { Injectable } from '@angular/core';
import { Observable, fromEvent, merge, of } from 'rxjs';
import { map, filter, bufferToggle, timeout, catchError } from 'rxjs/operators';
import { IScanData } from './scan.interface';
import { SessionService } from '../services/session.service';

@Injectable({
    providedIn: 'root'
  })
  export class WedgeScannerPlugin implements IScanner {

    private startChar = '*';
    private endChar = 'Enter';
    private bufferTimeout = 100;
    private scannerActive: boolean;

    constructor( sessionService: SessionService ) {
        sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configName === 'WedgeScanner')
        ).subscribe( m => {
            this.startChar = m.startChar;
            this.endChar = m.endChar;
        });
    }

    startScanning(): Observable<IScanData> {
        this.scannerActive = true;

        const startScanBuffer = fromEvent(document, 'keydown').pipe(
            filter( (e: KeyboardEvent) => this.scannerActive && e.key === this.startChar ));

        const stopScanBuffer = fromEvent(document, 'keydown').pipe(
            filter((e: KeyboardEvent) => !this.scannerActive || e.key === this.endChar));

        const timoutScanBuffer = startScanBuffer.pipe(
            timeout(this.bufferTimeout),
            catchError( e => of(true))
        );

        return fromEvent(document, 'keydown').pipe(
            map( (e: KeyboardEvent) => e.key ),
            bufferToggle(
                    startScanBuffer,
                    () => merge( stopScanBuffer, timoutScanBuffer)
            ),
            filter( s => s[0] === this.startChar && s[s.length - 1] === this.endChar),
            // Join the buffer into a string and remove the start and stop characters
            map( (s) => s.join('').slice(1, s.length - 1)),
            map( s => this.getScanData(s))
        );
    }

    stopScanning() {
        this.scannerActive = false;
    }

    private getScanData( s: string ): IScanData {
        return { type: s, data: s};
    }

}
