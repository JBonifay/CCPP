import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {TopbarComponent} from './topbar.component';
import {SidebarComponent} from './sidebar.component';

@Component({
  selector: 'app-layout',
  imports: [
    RouterOutlet,
    TopbarComponent,
    SidebarComponent
  ],
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.css'],
})
export class AppLayoutComponent {

}
