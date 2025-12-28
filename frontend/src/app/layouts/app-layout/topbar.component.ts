import {Component} from '@angular/core';
import {Button} from 'primeng/button';
import {OverlayBadge} from 'primeng/overlaybadge';
import {Avatar} from 'primeng/avatar';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  imports: [
    Button,
    OverlayBadge,
    Avatar
  ]
})
export class TopbarComponent {

}
