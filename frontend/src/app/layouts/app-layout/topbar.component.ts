import {Component, inject, OnInit} from '@angular/core';
import {OverlayBadge} from 'primeng/overlaybadge';
import {Avatar} from 'primeng/avatar';
import {ToggleButton} from 'primeng/togglebutton';
import {FormsModule} from '@angular/forms';
import {VisitorModeService} from '../../shared/services/visitor-mode.service';
import {WorkspaceConfigStore} from '../../features/settings/store/workspace-config.store';

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
export class TopbarComponent implements OnInit {
  readonly visitorMode = inject(VisitorModeService);
  readonly workspaceConfigStore = inject(WorkspaceConfigStore);

  ngOnInit() {
    this.workspaceConfigStore.loadConfig();
  }
}
