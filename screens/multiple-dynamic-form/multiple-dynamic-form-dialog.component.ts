import { Component } from '@angular/core';
import { MultipleDynamicFormComponent } from '../../screens/multiple-dynamic-form/multiple-dynamic-form.component';
import { OpenposMediaService } from '../../core';
import { Observable } from 'rxjs';
import { IMultipleFormOption } from './multiple-dynamic-form-screen.interface';

@Component({
    selector: 'app-multiple-dynamic-form-dialog',
    templateUrl: './multiple-dynamic-form-dialog.component.html'
})
export class MultipleDynamicFormDialogComponent extends MultipleDynamicFormComponent {
    current = 0;


    public isMobile: Observable<boolean>;
    public showOptions = true;
    public selectedOption: IMultipleFormOption;

    constructor( private mediaService: OpenposMediaService ) {
        super();
        const sizeMap = new Map([
            ['xs', true],
            ['sm', false],
            ['md', false],
            ['lg', false],
            ['xl', false]
        ]);
        this.isMobile = this.mediaService.mediaObservableFromMap(sizeMap);
    }

      onMakeOptionSelection( formOption: IMultipleFormOption): void {
          this.selectedOption = formOption;
          this.showOptions = false;
      }
    
      onBackButtonPressed(): void {
       this.showOptions = true;
      }
}
