import {Component, inject} from '@angular/core';
import {OverlayBadge} from 'primeng/overlaybadge';
import {Avatar} from 'primeng/avatar';
import {ToggleButton} from 'primeng/togglebutton';
import {FormsModule} from '@angular/forms';
import {VisitorModeService} from '../../shared/services/visitor-mode.service';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  imports: [
    OverlayBadge,
    Avatar,
    ToggleButton,
    FormsModule
  ]
})
export class TopbarComponent {
  readonly visitorMode = inject(VisitorModeService);
}
