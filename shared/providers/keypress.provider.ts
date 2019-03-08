import { Injectable, OnDestroy, OnInit } from '@angular/core';
import { Observable, merge, Subject, Subscription } from 'rxjs';

@Injectable()
export class KeyPressProvider implements OnDestroy {

    private keyPressSources: Observable<KeyboardEvent>[] = [];
    private subscription: Subscription;

    private subscribers = new Map<string, Map<number, (KeyboardEvent) => void>>();

    ngOnDestroy(): void {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }
    }

    registerKeyPressSource( source$: Observable<KeyboardEvent>) {
        this.keyPressSources.push(source$);
        this.rebuildKeyPressObserver();
    }

    subscribe( key: string, priority: number, next: (KeyboardEvent) => void): Subscription {
        if ( !this.subscribers.has(key) ) {
            this.subscribers.set( key, new Map<number, (KeyboardEvent) => void>());
        }

        // if ( this.subscribers.get(key).has(priority)) {
        //     throw new Error( `Another subscriber already exists with key ${key} and priority ${priority}`);
        // }
        this.subscribers.get(key).set(priority, next);

        return new Subscription( () => {
            this.subscribers.get(key).delete(priority);
        });
    }

    private rebuildKeyPressObserver() {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }

        this.subscription = merge(...this.keyPressSources).subscribe( event => {
            if ( this.subscribers.has(event.key)) {
                const priorityMap = this.subscribers.get(event.key);
                const prioritiesList = Array.from(priorityMap.keys()).sort();
                if ( prioritiesList.length > 0 ) {
                    const priority = prioritiesList[0];
                    this.subscribers.get(event.key).get(priority)(event);
                }
            }
        });
    }

}
