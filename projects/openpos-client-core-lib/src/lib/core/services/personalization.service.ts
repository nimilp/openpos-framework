import { Logger } from './logger.service';
import { Injectable, EventEmitter } from '@angular/core';
import { IThemeChangingEvent } from '../../shared/events';

@Injectable({
    providedIn: 'root',
  })
export class PersonalizationService {

    private serverBaseUrl: string;

    private previousTheme: string;

    private theme: string;

    public onThemeChanging = new EventEmitter<IThemeChangingEvent>();

    constructor(private log: Logger ) {
    }

    public personalize(serverName: string, serverPort: string, deviceId: string,
                       personalizationProperties?: Map<string, string>, sslEnabled?: boolean) {

        this.log.info(`personalizing with server: ${serverName}, port: ${serverPort}, deviceId: ${deviceId}`);
        localStorage.setItem('serverName', serverName);
        localStorage.setItem('serverPort', serverPort);
        localStorage.setItem('deviceId', deviceId);
        this.setPersonalizationProperties(personalizationProperties);

        if (sslEnabled) {
            localStorage.setItem('sslEnabled', '' + sslEnabled);
        } else {
            localStorage.setItem('sslEnabled', 'false');
        }
        this.serverBaseUrl = null; // will be regenerated on next fetch
    }

    private setPersonalizationProperties(personalizationProperties?: Map<string, string>) {

        const keys = Array.from(personalizationProperties.keys());

        localStorage.setItem('personalizationProperties', JSON.stringify(keys));

        if (personalizationProperties) {
            for (const key of keys) {
                localStorage.setItem(key, personalizationProperties.get(key));
            }
        }
    }

    private removePersonalizationProperties() {
        const keyString = localStorage.getItem('personalizationProperties');
        if (keyString) {
            const keys: string[] = JSON.parse(keyString);
            if (keys) {
                for (const key of keys) {
                    localStorage.removeItem(key);
                }
            }
        }
        localStorage.removeItem('personalizationProperties');
    }

    public dePersonalize() {
        const theme = this.getTheme();
        localStorage.removeItem('serverName');
        localStorage.removeItem('serverPort');
        localStorage.removeItem('deviceId');
        localStorage.removeItem('theme');
        this.removePersonalizationProperties();
        localStorage.removeItem('sslEnabled');
        this.setTheme(theme, true);
    }

    public getPersonalizationProperties(): Map<string, string> {
        const map = new Map<string, string>();
        const keyString = localStorage.getItem('personalizationProperties');
        if (keyString) {
            const keys: string[] = JSON.parse(keyString);
            if (keys) {
                for (const key of keys) {
                    const value = localStorage.getItem(key);
                    map.set(key, value);
                }
            }
        }
        return map;
    }

    public getWebsocketUrl(): string {
        let protocol = 'ws://';
        if (this.isSslEnabled()) {
            protocol = 'wss://';
        }
        let url: string = protocol + this.getServerName();
        if (this.getServerPort()) {
            url = url + ':' + this.getServerPort();
        }
        url = url + '/api/websocket';
        return url;
    }

    public getTheme(): string {
        const localTheme = localStorage.getItem('theme');
        if (this.theme) {
            return  this.theme;
        } else if (localTheme) {
            return localTheme;
        } else {
            return 'openpos-theme';
        }
    }

    public isSslEnabled(): boolean {
        return 'true' === localStorage.getItem('sslEnabled');
    }

    public setSslEnabled(enabled: boolean) {
        localStorage.setItem('sslEnabled', enabled + '');
    }

    public setTheme(theme: string, storeLocal: boolean) {
        this.theme = theme;
        if (storeLocal) {
            localStorage.setItem('theme', theme);
        }
        if (this.previousTheme !== theme) {
            this.log.info(`Theme changing from '${this.previousTheme}' to '${theme}'`);
            this.onThemeChanging.emit({currentTheme: this.previousTheme, newTheme: theme});
            this.previousTheme = theme;
        }
    }

    public setServerName(name: string) {
        localStorage.setItem('serverName', name);
    }

    public getServerName(): string {
        return localStorage.getItem('serverName');
    }

    public getDeviceName(): string {
        return localStorage.getItem('deviceName');
    }

    public setDeviceName(deviceName: string): void {
        localStorage.setItem('deviceName', deviceName);
    }

    public setServerPort(port: string) {
        localStorage.setItem('serverPort', port);
    }

    public getServerPort(): string {
        return localStorage.getItem('serverPort');
    }

    public getDeviceId(): string {
        return localStorage.getItem('deviceId');
    }

    public setDeviceId(id: string) {
        localStorage.setItem('deviceId', id);
    }

    public getPersonalizationResults(): string {
        return localStorage.getItem('personalizationResults');
    }

    public setPersonalizationResults(personalizationResults: string) {
        localStorage.setItem('personalizationResults', personalizationResults);
    }

    public refreshApp() {
        window.location.href = 'index.html';
    }

    public isPersonalized(): boolean {
        if (this.getServerName() && this.getDeviceId() && this.getServerPort()) {
            return true;
        } else {
            return false;
        }
    }

    public getServerBaseURL(): string {
        if (!this.serverBaseUrl) {
            const protocol = this.isSslEnabled() ? 'https' : 'http';
            this.serverBaseUrl = `${protocol}://${this.getServerName()}${this.getServerPort() ? `:${this.getServerPort()}` : ''}`;
            this.log.info(`Generated serverBaseURL: ${this.serverBaseUrl}`);
        }
        return this.serverBaseUrl;
    }

    public getApiServerBaseURL(): string {
        return `${this.getServerBaseURL()}/api`;
    }

}


