import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';


export interface SelfCheckoutFormInterface extends IAbstractScreen {
    submitButton: IActionItem;
    alternateSubmitActions: IActionItem[];
    imageUrl: string;
    autoComplete: boolean;
    instructions: string;
}
